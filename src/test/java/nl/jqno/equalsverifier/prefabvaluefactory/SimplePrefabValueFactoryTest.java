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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimplePrefabValueFactoryTest {
    private final String red = "red";
    private final String black = "black";
    private SimplePrefabValueFactory<String> factory;

    @Before
    public void setUp() {
        factory = new SimplePrefabValueFactory<>(red, black);
    }

    @Test
    public void createRed() {
        assertEquals(red, factory.createRed(null, null));
    }

    @Test
    public void createBlack() {
        assertEquals(black, factory.createBlack(null, null));
    }
}
