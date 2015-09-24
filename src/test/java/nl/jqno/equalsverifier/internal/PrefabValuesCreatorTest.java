/*
 * Copyright 2010-2013 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.internal;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.testhelpers.MockStaticFieldValueStash;
import nl.jqno.equalsverifier.testhelpers.PrefabValuesFactory;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.*;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Enum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class PrefabValuesCreatorTest {
    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MockStaticFieldValueStash stash;
    private PrefabValues prefabValues;

    @Before
    public void setup() {
        stash = new MockStaticFieldValueStash();
        prefabValues = PrefabValuesFactory.withPrimitives(stash);
    }

    @Test
    public void stashed() {
        prefabValues.putFor(POINT_TAG);
        assertEquals(Point.class, stash.lastBackuppedType);
    }

    @Test
    public void simple() {
        prefabValues.putFor(POINT_TAG);
        Point red = prefabValues.getRed(POINT_TAG);
        Point black = prefabValues.getBlack(POINT_TAG);
        assertFalse(red.equals(black));
    }

    @Test
    public void createSecondTimeIsNoOp() {
        prefabValues.putFor(POINT_TAG);
        Point red = prefabValues.getRed(POINT_TAG);
        Point black = prefabValues.getBlack(POINT_TAG);

        prefabValues.putFor(POINT_TAG);

        assertSame(red, prefabValues.getRed(POINT_TAG));
        assertSame(black, prefabValues.getBlack(POINT_TAG));
    }

    @Test
    public void createEnum() {
        TypeTag typeTag = new TypeTag(Enum.class);
        prefabValues.putFor(typeTag);
        assertNotNull(prefabValues.getRed(typeTag));
        assertNotNull(prefabValues.getBlack(typeTag));
    }

    @Test
    public void createOneElementEnum() {
        TypeTag typeTag = new TypeTag(OneElementEnum.class);
        prefabValues.putFor(typeTag);
        assertNotNull(prefabValues.getRed(typeTag));
        assertNotNull(prefabValues.getBlack(typeTag));
    }

    @Test
    public void createEmptyEnum() {
        thrown.expect(ReflectionException.class);
        thrown.expectMessage("Enum EmptyEnum has no elements");
        prefabValues.putFor(new TypeTag(EmptyEnum.class));
    }

    @Test
    public void oneStepRecursiveType() {
        TypeTag typeTag = new TypeTag(Node.class);
        prefabValues.put(typeTag, new Node(), new Node());
        prefabValues.putFor(typeTag);
    }

    @Test
    public void dontAddOneStepRecursiveType() {
        thrown.expect(RecursionException.class);
        prefabValues.putFor(new TypeTag(Node.class));
    }

    @Test
    public void oneStepRecursiveArrayType() {
        TypeTag typeTag = new TypeTag(NodeArray.class);
        prefabValues.put(typeTag, new NodeArray(), new NodeArray());
        prefabValues.putFor(typeTag);
    }

    @Test
    public void dontAddOneStepRecursiveArrayType() {
        thrown.expect(RecursionException.class);
        prefabValues.putFor(new TypeTag(NodeArray.class));
    }

    @Test
    public void addTwoStepRecursiveType() {
        prefabValues.put(new TypeTag(TwoStepNodeB.class), new TwoStepNodeB(), new TwoStepNodeB());
        prefabValues.putFor(new TypeTag(TwoStepNodeA.class));
    }

    @Test
    public void dontAddTwoStepRecursiveType() {
        thrown.expect(RecursionException.class);
        prefabValues.putFor(new TypeTag(TwoStepNodeA.class));
    }

    @Test
    public void twoStepRecursiveArrayType() {
        prefabValues.put(new TypeTag(TwoStepNodeArrayB.class), new TwoStepNodeArrayB(), new TwoStepNodeArrayB());
        prefabValues.putFor(new TypeTag(TwoStepNodeArrayA.class));
    }

    @Test
    public void dontAddTwoStepRecursiveArrayType() {
        thrown.expect(RecursionException.class);
        prefabValues.putFor(new TypeTag(TwoStepNodeArrayA.class));
    }

    @Test
    public void sameClassTwiceButNoRecursion() {
        prefabValues.putFor(new TypeTag(NotRecursiveA.class));
    }

    @Test
    public void recursiveWithAnotherFieldFirst() {
        thrown.expectMessage(containsString(RecursiveWithAnotherFieldFirst.class.getSimpleName()));
        thrown.expectMessage(not(containsString(RecursiveThisIsTheOtherField.class.getSimpleName())));
        prefabValues.putFor(new TypeTag(RecursiveWithAnotherFieldFirst.class));
    }

    @Test
    public void exceptionMessage() {
        thrown.expectMessage(TwoStepNodeA.class.getSimpleName());
        thrown.expectMessage(TwoStepNodeB.class.getSimpleName());
        prefabValues.putFor(new TypeTag(TwoStepNodeA.class));
    }

    @Test
    public void skipStaticFinal() {
        prefabValues.putFor(new TypeTag(StaticFinalContainer.class));
    }

    static class StaticFinalContainer {
        public static final StaticFinalContainer X = new StaticFinalContainer();
    }
}
