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
import la4j.matrix.MatrixFactory;
import la4j.vector.Vector;
import la4j.vector.VectorFactory;

public class SquareRootSolver implements LinearSystemSolver {

	
	public Vector solve(LinearSystem linearSystem) throws LinearSystemException {
		/* try to build A = St D S */

		Matrix a = linearSystem.coefficientsMatrix();
		Vector b = linearSystem.rightHandVector();
		
		Matrix s = MatrixFactory.createSquareDenseMatrix(linearSystem.equations());
		Matrix d = MatrixFactory.createSquareDenseMatrix(linearSystem.equations());

		Vector x = VectorFactory.createVector(linearSystem.variables());
		Vector y = VectorFactory.createVector(linearSystem.variables());
		Vector z = VectorFactory.createVector(linearSystem.variables());

		double sum = 0;
		
		/* check matrix to symmetric */
		if (!a.isSymmetric()) {
			throw new LinearSystemException("matrix a is not symmetric");
		}
		
		final int rows = a.rows();
		final int columns = a.columns();

		for (int i = 0; i < rows; i++) {

			/* get dii */
			sum = 0;
			for (int l = 0; l < i; l++) {
				sum += Math.pow(s.get(l, i), 2) * d.get(l, l);
			}

			d.set(i, i, Math.signum(a.get(i, i) - sum));

			/* get sii */
			sum = 0;
			for (int l = 0; l < i; l++) {
				sum += s.get(l, i) * s.get(l, i) * d.get(l, l);
			}

			s.set(i, i, Math.sqrt(Math.abs(a.get(i, i) - sum)));

			if (Math.abs(s.get(i, i)) < EPS) {
				throw new LinearSystemException(
						"matrix s contains '0' at main diagonal");
			}

			/* get sij */
			for (int j = i + 1; j < columns; j++) {
				sum = 0;
				for (int l = 0; l < i; l++) {
					sum += s.get(l, i) * s.get(l, i) * d.get(l, l);
				}
				s.set(i, j, (a.get(i, j) - sum) / (s.get(i, i) * d.get(i, i)));

			}

			/* get zi */
			sum = 0;
			for (int l = 0; l < i; l++) {
				sum += z.get(l) * s.get(l, i);
			}
			z.set(i, (b.get(i) - sum) / s.get(i, i));

			/* get yi */
			y.set(i, z.get(i) / d.get(i, i));

		}

		/* try to calc xi */

		for (int i = rows - 1; i >= 0; i--) {
			sum = 0;
			for (int l = i + 1; l < columns; l++) {
				sum += x.get(l) * s.get(i, l);
			}

			x.set(i, (y.get(i) - sum) / s.get(i, i));
		}

		return x;
	}
}
