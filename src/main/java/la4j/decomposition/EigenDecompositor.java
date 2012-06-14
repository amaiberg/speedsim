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

package la4j.decomposition;

import la4j.err.MatrixDecompositionException;
import la4j.err.MatrixException;
import la4j.matrix.Matrix;
import la4j.matrix.MatrixFactory;
import la4j.vector.Vector;
import la4j.vector.VectorFactory;

/**
 *  
 *  A = V*D*V^1, where
 *   D - is a diagonal matrix with eugenvalues in main diagonal,
 *   V - matrix with eugenvectors for each eugenvalue in columns;
 * 
 */
public class EigenDecompositor implements MatrixDecompositor {

	public static final int MAX_ITERATIONS = 100000;
	
	private Matrix d;
	private Matrix v;
 
	public Matrix[] decompose(MatrixDecomposition matrixDecomposition)
			throws MatrixDecompositionException {

		Matrix matrix = matrixDecomposition.matrix();

		if (!matrix.isSymmetric()) {
			throw new MatrixDecompositionException("matrix is not symmetric");
		}

		try {

			final int n = matrix.rows();

			d = matrix.clone();

			Vector r = generateR(d);

			v = MatrixFactory.createSquareIdentityDenseMatrix(n);

			int iter = 0;

			do {

				int k = findMax(r, -1);
				int l = findMax(d.getRow(k), k);

				Matrix u = generateU(d, k, l);

				v = v.multiply(u);

				d = u.transpose().multiply(d.multiply(u));

				r.set(k, generateRi(d.getRow(k), k));
				r.set(l, generateRi(d.getRow(l), l));

				iter++;

			} while (r.norm() > EPS && iter < MAX_ITERATIONS);

			if (iter > MAX_ITERATIONS) {
				throw new MatrixDecompositionException("to many iterations");
			}

			return new Matrix[] { d, v };
			
		} catch (MatrixException ex) {
			throw new MatrixDecompositionException(ex.getMessage());
		}
	}
	
	private int findMax(Vector r, int exl) {
		int ind = exl == 0 ? 1 : 0;
		final int length = r.length();
		
		for (int i = 0; i < length; i++) {
			if (i != exl && Math.abs(r.get(ind)) < Math.abs(r.get(i))) {
				ind = i;
			}
		}

		return ind;
	}

	private Vector generateR(Matrix a) {
		final int rows = a.rows();
		Vector res = VectorFactory.createVector(rows);
		
		for (int i = 0; i < rows; i++) {
			res.set(i, generateRi(a.getRow(i), i));
		}
		return res;
	}

	private double generateRi(Vector v, int k) {
		final int length = v.length();
		double sum = 0;

		for (int i = 0; i < length; i++) {
			if (i != k) {
				sum += v.get(i) * v.get(i);
			}
		}

		return sum;
	}

	private Matrix generateU(Matrix a, int k, int l) {
		double u[][] = MatrixFactory
				.createIdentityDenseMatrix(a.rows(), a.columns()).toArray();

		double alpha;
		double beta;

		if ((a.get(k, k) - a.get(l, l)) < EPS) {
			alpha = beta = Math.sqrt(0.5);
		} else {
			double mu = 2 * a.get(k, l) / (a.get(k, k) - a.get(l, l));
			mu = 1 / Math.sqrt(1 + mu * mu);
			alpha = Math.sqrt(0.5 * (1 + mu));
			beta = Math.signum(mu) * Math.sqrt(0.5 * (1 - mu));
		}

		u[k][k] = alpha;
		u[l][l] = alpha;
		u[k][l] = -beta;
		u[l][k] = beta;

		return MatrixFactory.createDenseMatrix(u);
	}


	public Matrix V() {
		return d;
	}
	
	public Matrix D() {
		return v;
	}
}
