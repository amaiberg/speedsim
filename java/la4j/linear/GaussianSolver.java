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
import la4j.matrix.MatrixUtils;
import la4j.vector.Vector;

public class GaussianSolver implements LinearSystemSolver {

	
	public Vector solve(LinearSystem linearSystem) throws LinearSystemException {

		Matrix a = linearSystem.coefficientsMatrix();
		Vector b = linearSystem.rightHandVector();

		Matrix extenda = MatrixUtils.expandMatrixByColumn(a, b);

		Matrix treangle = createExtendTreangleMatrix(extenda);

		Vector x = retraceGaus(treangle);

		return x;
	}

	private Matrix createExtendTreangleMatrix(Matrix m)
			throws LinearSystemException {
		Matrix treangle = m.clone();

		/* loop by col */
		for (int i = 0; i < treangle.rows(); i++) {
			int r = -1;
			double maxItem = 0.0;

			for (int k = i; k < treangle.rows(); k++) {
				if (Math.abs(treangle.get(k, i)) > maxItem) {
					maxItem = Math.abs(treangle.get(k, i));
					r = k;
				}
			}

			// is matrix confluent
			if (r == -1 || Math.abs(maxItem) < EPS) {
				if (treangle.get(i, treangle.columns()) > EPS)
					throw new LinearSystemException("matrix is confluent");
				else
					throw new LinearSystemException(
							"linear system has many solution");
			}

			if (r > i) {
				treangle.swapRows(r, i);
			}

			for (int j = i + 1; j < treangle.rows(); j++) {
				double C = treangle.get(j, i) / treangle.get(i, i);

				for (int k = i; k < treangle.columns(); k++) {
					if (k == i) {
						treangle.set(j, k, C);
					} else {
						treangle.set(j, k,
								treangle.get(j, k) - treangle.get(i, k) * C);
					}
				}
			}
		}
		return treangle;
	}

	private Vector retraceGaus(Matrix exta) throws LinearSystemException {

		if (exta.trace() == (double) 0.0) {
			throw new LinearSystemException("linear system has no solution");
		}

		double x[] = new double[exta.columns() - 1];
		for (int i = x.length - 1; i >= 0; i--) {
			double summand = 0;
			for (int j = i + 1; j < x.length; j++) {
				summand += x[j] * exta.get(i, j);
			}

			x[i] = (exta.get(i, exta.columns() - 1) - summand) / exta.get(i, i);

		}

		return new Vector(x);
	}
}
