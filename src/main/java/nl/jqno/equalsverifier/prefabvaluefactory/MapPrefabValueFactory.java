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
package nl.jqno.equalsverifier.prefabvaluefactory;

import nl.jqno.equalsverifier.internal.PrefabValues;
import nl.jqno.equalsverifier.internal.TypeTag;

import java.util.Map;

/**
 * Can create instances of {@link Map}.
 *
 * Must be overridden, to implement {@link #createEmpty()}.
 *
 * @param <T> The type of the Map itself.
 */
@SuppressWarnings("unchecked")
public abstract class MapPrefabValueFactory<T extends Map> extends GenericPrefabValueFactory<T> {
    @Override
    public T createRed(TypeTag typeTag, PrefabValues prefabValues) {
        T result = createEmpty();
        // Use red for key and black for value to avoid having identical keys and values.
        // But don't do it in the Black map, or they may cancel each other out again.
        Object key = prefabValues.getRed(determineActualTypeTagFor(0, typeTag));
        Object value = prefabValues.getBlack(determineActualTypeTagFor(1, typeTag));
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
