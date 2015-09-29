/*
 * Copyright 2015 Jan Ouwens
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

import nl.jqno.equalsverifier.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.GenericPrefabValueFactory.CollectionPrefabValueFactory;
import nl.jqno.equalsverifier.internal.GenericPrefabValueFactory.MapPrefabValueFactory;
import nl.jqno.equalsverifier.internal.TypeTag.Wildcard;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class GenericPrefabValueFactoryTest {
    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag STRINGLIST_TYPETAG = new TypeTag(List.class, STRING_TYPETAG);
    private static final TypeTag STRINGSET_TYPETAG = new TypeTag(Set.class, STRING_TYPETAG);
    private static final TypeTag STRINGSTRINGMAP_TYPETAG = new TypeTag(Map.class, STRING_TYPETAG, STRING_TYPETAG);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag WILDCARD_TYPETAG = new TypeTag(Wildcard.class);
    private static final TypeTag WILDCARDLIST_TYPETAG = new TypeTag(List.class, WILDCARD_TYPETAG);
    private static final TypeTag WILDCARDMAP_TYPETAG = new TypeTag(Map.class, WILDCARD_TYPETAG, WILDCARD_TYPETAG);
    private static final TypeTag RAWLIST_TYPETAG = new TypeTag(List.class);
    private static final TypeTag RAWMAP_TYPETAG = new TypeTag(Map.class);

    private static final GenericPrefabValueFactory<List> LIST_FACTORY = new StubListPrefabValueFactory();
    private static final GenericPrefabValueFactory<Set> SET_FACTORY = new StubSetPrefabValueFactory();
    private static final GenericPrefabValueFactory<Map> MAP_FACTORY = new StubMapPrefabValueFactory();

    private final PrefabValues prefabValues = new PrefabValues();
    private String red;
    private String black;
    private Object redObject;
    private Object blackObject;

    @Before
    public void setUp() {
        JavaApiPrefabValues.addTo(prefabValues);
        red = prefabValues.getRed(STRING_TYPETAG);
        black = prefabValues.getBlack(STRING_TYPETAG);
        redObject = prefabValues.getRed(OBJECT_TYPETAG);
        blackObject = prefabValues.getBlack(OBJECT_TYPETAG);
    }

    @Test
    public void createRedListOfString() {
        List<String> expected = new ArrayList<>();
        expected.add(red);

        List<String> actual = LIST_FACTORY.createRed(STRINGLIST_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createBlackListOfString() {
        List<String> expected = new ArrayList<>();
        expected.add(black);

        List<String> actual = LIST_FACTORY.createBlack(STRINGLIST_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createRedSetOfString() {
        Set<String> expected = new HashSet<>();
        expected.add(red);

        Set<String> actual = SET_FACTORY.createRed(STRINGSET_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createBlackSetOfString() {
        Set<String> expected = new HashSet<>();
        expected.add(black);

        Set<String> actual = SET_FACTORY.createBlack(STRINGSET_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createRedMapOfStringToString() {
        Map<String, String> expected = new HashMap<>();
        expected.put(red, black);

        Map<String, String> actual = MAP_FACTORY.createRed(STRINGSTRINGMAP_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createBlackMapOfStringToString() {
        Map<String, String> expected = new HashMap<>();
        expected.put(black, black);

        Map<String, String> actual = MAP_FACTORY.createBlack(STRINGSTRINGMAP_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createRedListOfWildcard() {
        List<Object> objects = new ArrayList<>();
        objects.add(redObject);
        List<?> expected = objects;

        List<?> actual = LIST_FACTORY.createRed(WILDCARDLIST_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createBlackListOfWildcard() {
        List<Object> objects = new ArrayList<>();
        objects.add(blackObject);
        List<?> expected = objects;

        List<?> actual = LIST_FACTORY.createBlack(WILDCARDLIST_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createRedRawList() {
        List expected = new ArrayList<>();
        expected.add(redObject);

        List actual = LIST_FACTORY.createRed(RAWLIST_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createBlackRawList() {
        List expected = new ArrayList<>();
        expected.add(blackObject);

        List actual = LIST_FACTORY.createBlack(RAWLIST_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createRedMapOfWildcard() {
        Map<Object, Object> objects = new HashMap<>();
        objects.put(redObject, blackObject);
        Map<?, ?> expected = objects;

        Map<?, ?> actual = MAP_FACTORY.createRed(WILDCARDMAP_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createBlackMapOfWildcard() {
        Map<Object, Object> objects = new HashMap<>();
        objects.put(blackObject, blackObject);
        Map<?, ?> expected = objects;

        Map<?, ?> actual = MAP_FACTORY.createBlack(WILDCARDMAP_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createRedRawMap() {
        Map expected = new HashMap<>();
        expected.put(redObject, blackObject);

        Map actual = MAP_FACTORY.createRed(RAWMAP_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    @Test
    public void createBlackRawMap() {
        Map expected = new HashMap<>();
        expected.put(blackObject, blackObject);

        Map actual = MAP_FACTORY.createBlack(RAWMAP_TYPETAG, prefabValues);
        assertEquals(expected, actual);
    }

    private static class StubListPrefabValueFactory extends CollectionPrefabValueFactory<List> {
        @Override
        public List createEmpty() {
            return new ArrayList<>();
        }
    }

    private static class StubSetPrefabValueFactory extends CollectionPrefabValueFactory<Set> {
        @Override
        public Set createEmpty() {
            return new HashSet<>();
        }
    }

    private static class StubMapPrefabValueFactory extends MapPrefabValueFactory<Map> {
        @Override
        public Map createEmpty() {
            return new HashMap<>();
        }
    }
}
