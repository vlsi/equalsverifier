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

/**
 * Returns pre-created instances of types to use as prefab value.
 *
 * @param <T> The type for which to return prefab values.
 */
public class SimplePrefabValueFactory<T> implements PrefabValueFactory<T> {
    private final T red;
    private final T black;

    public SimplePrefabValueFactory(T red, T black) {
        this.red = red;
        this.black = black;
    }

    @Override
    public T createRed(TypeTag typeTag, PrefabValues prefabValues) {
        return red;
    }

    @Override
    public T createBlack(TypeTag typeTag, PrefabValues prefabValues) {
        return black;
    }
}
