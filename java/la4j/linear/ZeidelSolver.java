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

public class ZeidelSolver implements LinearSystemSolver {

	private final static int MAX_ITERATIONS = 100000;

	
	public Vector solve(LinearSystem linearSystem) throws LinearSystemException {

		if (!isMethodCanBeUsed(linearSystem)) {
			throw new LinearSystemException("Zeidel method can not be used");
		} else {
			
			Matrix a = linearSystem.coefficientsMatrix();
			Vector b = linearSystem.rightHandVector();

			Vector current = new Vector(linearSystem.variables());

			final int rows = a.rows();
			final int columns = a.columns();
			
			double aa[][] = a.toArrayCopy(); // get coefficients
			int iteration = 0;
			

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					if (i != j) {
						aa[i][j] /= aa[i][i];
					}
				}
			}

			while (iteration < MAX_ITERATIONS
					&& !linearSystem.isSolution(current)) {

				for (int i = 0; i < rows; i++) {
					double sum = b.get(i) / aa[i][i];
					for (int j = 0; j < columns; j++) {
						if (i != j) {
							sum -= aa[i][j] * current.get(j);
						}
					}

					current.set(i, sum);
				}

				iteration++;

			}

			if (iteration == MAX_ITERATIONS) {
				throw new LinearSystemException("method Zeidel can not be used");
			}

			return current;
		}
	}

	private boolean isMethodCanBeUsed(LinearSystem linearSystem) {

		Matrix a = linearSystem.coefficientsMatrix();

		for (int i = 0; i < a.rows(); i++) {
			double sum = 0;

			for (int j = 0; j < a.columns(); j++) {
				if (i != j) {
					sum += Math.abs(a.get(i, j));
				}
			}

			if (sum > Math.abs(a.get(i, i)) - EPS) {
				return false;
			}
		}

		return true;
	}
}
