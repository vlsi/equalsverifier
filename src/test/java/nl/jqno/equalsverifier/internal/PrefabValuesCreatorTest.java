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
        prefabValues.putFor(Point.class);
        assertEquals(Point.class, stash.lastBackuppedType);
    }

    @Test
    public void simple() {
        prefabValues.putFor(Point.class);
        Point red = prefabValues.getRed(POINT_TAG);
        Point black = prefabValues.getBlack(POINT_TAG);
        assertFalse(red.equals(black));
    }

    @Test
    public void createSecondTimeIsNoOp() {
        prefabValues.putFor(Point.class);
        Point red = prefabValues.getRed(POINT_TAG);
        Point black = prefabValues.getBlack(POINT_TAG);

        prefabValues.putFor(Point.class);

        assertSame(red, prefabValues.getRed(POINT_TAG));
        assertSame(black, prefabValues.getBlack(POINT_TAG));
    }

    @Test
    public void createEnum() {
        prefabValues.putFor(Enum.class);
        assertNotNull(prefabValues.getRed(new TypeTag(Enum.class)));
        assertNotNull(prefabValues.getBlack(new TypeTag(Enum.class)));
    }

    @Test
    public void createOneElementEnum() {
        prefabValues.putFor(OneElementEnum.class);
        assertNotNull(prefabValues.getRed(new TypeTag(OneElementEnum.class)));
        assertNotNull(prefabValues.getBlack(new TypeTag(OneElementEnum.class)));
    }

    @Test
    public void createEmptyEnum() {
        thrown.expect(ReflectionException.class);
        thrown.expectMessage("Enum EmptyEnum has no elements");
        prefabValues.putFor(EmptyEnum.class);
    }

    @Test
    public void oneStepRecursiveType() {
        prefabValues.put(Node.class, new Node(), new Node());
        prefabValues.putFor(Node.class);
    }

    @Test
    public void dontAddOneStepRecursiveType() {
        thrown.expect(RecursionException.class);
        prefabValues.putFor(Node.class);
    }

    @Test
    public void oneStepRecursiveArrayType() {
        prefabValues.put(NodeArray.class, new NodeArray(), new NodeArray());
        prefabValues.putFor(NodeArray.class);
    }

    @Test
    public void dontAddOneStepRecursiveArrayType() {
        thrown.expect(RecursionException.class);
        prefabValues.putFor(NodeArray.class);
    }

    @Test
    public void addTwoStepRecursiveType() {
        prefabValues.put(TwoStepNodeB.class, new TwoStepNodeB(), new TwoStepNodeB());
        prefabValues.putFor(TwoStepNodeA.class);
    }

    @Test
    public void dontAddTwoStepRecursiveType() {
        thrown.expect(RecursionException.class);
        prefabValues.putFor(TwoStepNodeA.class);
    }

    @Test
    public void twoStepRecursiveArrayType() {
        prefabValues.put(TwoStepNodeArrayB.class, new TwoStepNodeArrayB(), new TwoStepNodeArrayB());
        prefabValues.putFor(TwoStepNodeArrayA.class);
    }

    @Test
    public void dontAddTwoStepRecursiveArrayType() {
        thrown.expect(RecursionException.class);
        prefabValues.putFor(TwoStepNodeArrayA.class);
    }

    @Test
    public void sameClassTwiceButNoRecursion() {
        prefabValues.putFor(NotRecursiveA.class);
    }

    @Test
    public void recursiveWithAnotherFieldFirst() {
        thrown.expectMessage(containsString(RecursiveWithAnotherFieldFirst.class.getSimpleName()));
        thrown.expectMessage(not(containsString(RecursiveThisIsTheOtherField.class.getSimpleName())));
        prefabValues.putFor(RecursiveWithAnotherFieldFirst.class);
    }

    @Test
    public void exceptionMessage() {
        thrown.expectMessage(TwoStepNodeA.class.getSimpleName());
        thrown.expectMessage(TwoStepNodeB.class.getSimpleName());
        prefabValues.putFor(TwoStepNodeA.class);
    }

    @Test
    public void skipStaticFinal() {
        prefabValues.putFor(StaticFinalContainer.class);
    }

    static class StaticFinalContainer {
        public static final StaticFinalContainer X = new StaticFinalContainer();
    }
}
