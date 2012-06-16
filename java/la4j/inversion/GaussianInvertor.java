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

package la4j.inversion;

import la4j.err.LinearSystemException;
import la4j.err.MatrixInversionException;
import la4j.matrix.Matrix;
import la4j.matrix.MatrixFactory;
import la4j.matrix.MatrixUtils;
import la4j.vector.Vector;
import la4j.vector.VectorFactory;

public class GaussianInvertor implements MatrixInvertor {

	
	public Matrix inverse(MatrixInversion matrixInversion)
			throws MatrixInversionException {

		Matrix matrix = matrixInversion.matrix();
		
		Matrix treangle = MatrixUtils
				.toTreangleMatrixWithCoefficients(matrix);
		
		final int rows = treangle.rows();
		final int columns = treangle.columns();
		
		double inverse[][] = new double[columns][rows];
		double treangleSelf[][] = treangle.toArray();

		try {

			for (int i = 0; i < rows; i++) {
				Vector ident = VectorFactory.createVector(rows);
				ident.set(i, 1.0);
				
				/*
				 * TODO: revert this loops 
				 */
				for (int j = 0; j < rows - 1; j++) {
					for (int k = j + 1; k < columns; k++) {
						double identk = ident.get(k) - ident.get(j)
								* treangleSelf[k][j];
						ident.set(k, identk);
					}
				}

				Vector x = retraceGaus(treangle, ident);
				inverse[i] = x.toArray();
			}

		} catch (LinearSystemException ex) {
			throw new MatrixInversionException(ex.getMessage());
		}

		return MatrixFactory.createDenseMatrix(inverse).transpose();
	}

	private Vector retraceGaus(Matrix a, Vector b) throws LinearSystemException {

		final int columns = a.columns();

		if (a.trace() == (double) 0.0) {
			throw new LinearSystemException("linear system has no solution");
		}

		double x[] = new double[columns];
		
		for (int i = columns - 1; i >= 0; i--) {
			double summand = 0;
			for (int j = i + 1; j < columns; j++) {
				summand += x[j] * a.get(i, j);
			}

			x[i] = (b.get(i) - summand) / a.get(i, i);
		}

		return new Vector(x);
	}
}
