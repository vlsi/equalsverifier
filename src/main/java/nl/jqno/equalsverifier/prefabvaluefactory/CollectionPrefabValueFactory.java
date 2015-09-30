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

import java.util.Collection;

/**
 * Can create instances of {@link Collection}.
 *
 * Must be overridden, to implement {@link #createEmpty()}.
 *
 * @param <T> The type of the Collection itself.
 */
@SuppressWarnings("unchecked")
public abstract class CollectionPrefabValueFactory<T extends Collection> extends GenericPrefabValueFactory<T> {
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
