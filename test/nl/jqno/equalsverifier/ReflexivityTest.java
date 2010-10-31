/*
 * Copyright 2009-2010 Jan Ouwens
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
package nl.jqno.equalsverifier;

import static nl.jqno.equalsverifier.Helper.assertFailure;
import nl.jqno.equalsverifier.points.Point;

import org.junit.Test;

public class ReflexivityTest {
	@Test
	public void reflexivity() {
		EqualsVerifier<ReflexivityBrokenPoint> ev = EqualsVerifier.forClass(ReflexivityBrokenPoint.class);
		assertFailure(ev, "Reflexivity", "object does not equal itself", ReflexivityBrokenPoint.class.getSimpleName());
	}
	
	static class ReflexivityBrokenPoint extends Point {
		// Instantiator.scramble will flip this boolean.
		private boolean broken = false;
		
		public ReflexivityBrokenPoint(int x, int y) {
			super(x, y);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (broken && this == obj) {
				return false;
			}
			return super.equals(obj);
		}
	}
}
