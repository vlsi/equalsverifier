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

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Creates instances of generic types for use as prefab value.
 *
 * @param <T> The type to instantiate.
 */
public abstract class GenericPrefabValueFactory<T> {
    /**
     * Returns an empty instance of T, to be filled in by
     * {@link #createRed(TypeTag, PrefabValues)} and
     * {@link #createBlack(TypeTag, PrefabValues)}.
     */
    public abstract T createEmpty();

    /**
     * Calls {@link #createEmpty()} and fills it in with "red" values for use
     * as prefab value.
     *
     * @param typeTag Tag for the type to instantiate.
     * @param prefabValues From which to draw object instances.
     * @return A "red" instance of {@link T}.
     */
    public abstract T createRed(TypeTag typeTag, PrefabValues prefabValues);

    /**
     * Calls {@link #createEmpty()} and fills it in with "black" values for use
     * as prefab value.
     *
     * @param typeTag Tag for the type to instantiate.
     * @param prefabValues From which to draw object instances.
     * @return A "black" instance of {@link T}.
     */
    public abstract T createBlack(TypeTag typeTag, PrefabValues prefabValues);

    protected TypeTag determineActualTypeTagFor(int n, TypeTag typeTag) {
        TypeTag objectTypeTag = new TypeTag(Object.class);

        List<TypeTag> genericTypes = typeTag.getGenericTypes();
        if (genericTypes.size() <= n) {
            return objectTypeTag;
        }

        TypeTag innerTag = genericTypes.get(n);
        if (innerTag.getType().equals(TypeTag.Wildcard.class)) {
            return objectTypeTag;
        }

        return innerTag;
    }

    /**
     * Can create instances of {@link Collection}.
     *
     * Must be overridden, to implement {@link #createEmpty()}.
     *
     * @param <T> The type of the Collection itself.
     */
    @SuppressWarnings("unchecked")
    public abstract static class CollectionPrefabValueFactory<T extends Collection> extends GenericPrefabValueFactory<T> {
        @Override
        public T createRed(TypeTag typeTag, PrefabValues prefabValues) {
            T result = createEmpty();
            final Object entry = prefabValues.getRed(determineActualTypeTagFor(0, typeTag));
            result.add(entry);
            return result;
        }

        @Override
        public T createBlack(TypeTag typeTag, PrefabValues prefabValues) {
            T result = createEmpty();
            Object entry = prefabValues.getBlack(determineActualTypeTagFor(0, typeTag));
            result.add(entry);
            return result;
        }
    }

    /**
     * Can create instances of {@link Map}.
     *
     * Must be overridden, to implement {@link #createEmpty()}.
     *
     * @param <T> The type of the Map itself.
     */
    @SuppressWarnings("unchecked")
    public abstract static class MapPrefabValueFactory<T extends Map> extends GenericPrefabValueFactory<T> {
        @Override
        public T createRed(TypeTag typeTag, PrefabValues prefabValues) {
            T result = createEmpty();
            Object key = prefabValues.getRed(determineActualTypeTagFor(0, typeTag));
            Object value = prefabValues.getRed(determineActualTypeTagFor(1, typeTag));
            result.put(key, value);
            return result;
        }

        @Override
        public T createBlack(TypeTag typeTag, PrefabValues prefabValues) {
            T result = createEmpty();
            Object key = prefabValues.getBlack(determineActualTypeTagFor(0, typeTag));
            Object value = prefabValues.getBlack(determineActualTypeTagFor(1, typeTag));
            result.put(key, value);
            return result;
        }
    }
}
