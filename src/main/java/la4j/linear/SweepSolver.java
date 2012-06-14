/*
 * Copyright 2011, Vladimir Kostyukov
 * 
 * This file is part of la4j project (http://la4j.googlecode.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package la4j.linear;

import la4j.err.LinearSystemException;
import la4j.matrix.Matrix;
import la4j.vector.Vector;
import la4j.vector.VectorFactory;

public class SweepSolver implements LinearSystemSolver {

	
	public Vector solve(LinearSystem linearSystem) throws LinearSystemException {

		Matrix a = linearSystem.coefficientsMatrix();
		Vector b = linearSystem.rightHandVector();

		final int n = linearSystem.variables();

		Vector x = VectorFactory.createVector(n);

		for (int i = 0; i < n - 1; i++) {
			double max = Math.abs(a.get(i, i));

			int maxi = i;
			for (int j = i + 1; j < n; j++) {
				if (Math.abs(a.get(j, i)) > max) {
					max = Math.abs(a.get(j, i));
					maxi = j;
				}
			}

			if (maxi != i) {
				for (int j = 0; j < n; j++) {
					double t = a.get(i, j);
					a.set(i, j, a.get(maxi, j));
					a.set(maxi, j, t);
				}

				double tt = b.get(i);
				b.set(i, b.get(maxi));
				b.set(maxi, tt);
			}

			for (int j = i + 1; j < n; j++) {
				double c = a.get(j, i) / a.get(i, i);
				for (int k = i; k < n; k++) {
					a.set(j, k, a.get(j, k) - a.get(i, k) * c);
				}

				b.set(j, b.get(j) - b.get(i) * c);
			}
		}

		for (int i = n - 1; i >= 0; i--) {
			double sum = 0;
			for (int j = i + 1; j < n; j++) {
				sum += a.get(i, j) * x.get(j);
			}

			x.set(i, (b.get(i) - sum) / a.get(i, i));
		}

		return x;
	}
}
