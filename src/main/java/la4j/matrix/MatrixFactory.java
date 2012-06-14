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

package la4j.matrix;

import java.util.Random;

public abstract class MatrixFactory {

	private MatrixFactory() {
	}

	public static DenseMatrix createDenseMatrix() {
		return new DenseMatrix();
	}
	
	public static SparceMatrix createSparceMatrix() {
		return new SparceMatrix();
	}

	public static DenseMatrix createDenseMatrix(int row, int col) {
		return new DenseMatrix(row, col);
	}
	
	public static DenseMatrix createSparceMatrix(int row, int col) {
		return null;
	}

	public static DenseMatrix createDenseMatrix(double array[][]) {
		return new DenseMatrix(array);
	}

	public static DenseMatrix createMatrixWithCopy(double array[][]) {
		int row = array.length;
		int col = array[0].length;

		double arraycopy[][] = new double[col][row];

		for (int i = 0; i < row; i++) {
			System.arraycopy(array[i], 0, arraycopy[i], 0, col);
		}

		return new DenseMatrix(arraycopy);
	}

	public static DenseMatrix createRandomDenseMatrix(int row, int col) {
		DenseMatrix m = new DenseMatrix(row, col);

		Random gen = new Random();

		for (int i = 0; i < m.rows(); i++) {
			for (int j = 0; j < m.columns(); j++) {
				m.set(i, j, gen.nextDouble());
			}
		}

		return m;
	}
	
	public static DenseMatrix createRandomSymmetricDenseMatrix(int row, int col) {
		DenseMatrix m = new DenseMatrix(row, col);
		
		Random gen = new Random();
		
		for (int i = 0; i < m.rows(); i++) {
			for (int j=i; j < m.columns(); j++) {
				double value = gen.nextDouble(); 
				m.set(i, j, value);
				m.set(j, i, value);
			}		
		}
		
		return m;
	}
	
	

	public static DenseMatrix createIdentityDenseMatrix(int row, int col) {
		DenseMatrix m = new DenseMatrix(row, col);

		for (int i = 0; i < m.rows(); i++) {
			m.set(i, i, (double) 1.0);
		}

		return m;
	}
	
	public static Matrix createSquareUnifMatrix(int dim, double p){
		Matrix m = MatrixFactory.createSquareDenseMatrix(dim);
		for(int i=0;i<dim;i++)
			for(int j=0;j<dim;j++)
				m.set(i, j, (Math.random() < p)? 1: 0);
		return m;
	}

	public static SquareDenseMatrix createSquareDenseMatrix(int dim) {
		return new SquareDenseMatrix(dim, dim);
	}

	public static DenseMatrix createSquareIdentityDenseMatrix(int dim) {
		return createIdentityDenseMatrix(dim, dim);
	}
}
