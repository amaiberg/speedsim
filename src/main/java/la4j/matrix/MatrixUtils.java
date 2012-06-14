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

import la4j.err.MatrixException;
import la4j.vector.Vector;

public abstract class MatrixUtils {

	private MatrixUtils() {
	};

	public static DenseMatrix multyply(DenseMatrix a, double d) {
		return a.multiply(d);
	}

	public static Matrix multiply(Matrix a, Matrix b) throws MatrixException {
		return a.multiply(b);
	}

	public static Vector multiply(Matrix a, Vector b) throws MatrixException {
		return a.multiply(b);
	}

	public static Matrix add(Matrix a, Matrix b) throws MatrixException {
		return a.add(b);
	}

	public static Matrix substract(Matrix a, Matrix b) throws MatrixException {
		return a.subtract(b);
	}

	public static DenseMatrix transpose(DenseMatrix a) {
		return a.transpose();
	}

	public static Matrix expandMatrixByRow(Matrix a, Vector row) {
		Matrix b = a.clone();
		b.setRows(b.rows() + 1);

		try {
			for (int i = 0; i < b.columns(); i++) {
				b.set(b.rows() - 1, i, row.get(i));
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
		}

		return b;
	}

	public static Matrix expandMatrixByColumn(Matrix a, Vector column) {
		Matrix b = a.clone();
		b.setColumns(b.columns() + 1);

		try {
			for (int i = 0; i < b.rows(); i++) {
				b.set(i, b.columns() - 1, column.get(i));
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
		}

		return b;
	}

	public static Matrix expandMatrix(Matrix a, Vector row, Vector column) {
		Matrix b = a.clone();
		b.setRows(b.rows() + 1);
		b.setColumns(b.columns() + 1);

		try {
			for (int i = 0; i < b.columns(); i++) {
				b.set(b.rows() - 1, i, row.get(i));
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
		}

		try {
			for (int i = 0; i < b.rows(); i++) {
				b.set(i, b.columns() - 1, column.get(i));
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
		}

		return b;
	}

	public static Matrix toTreangleMatrixWithCoefficients(Matrix a) {
		
		Matrix treangle = a.clone();

		double treangleSelf[][] = treangle.toArray();

		final int rows = treangle.rows();
		final int columns = treangle.columns();

		for (int i = 0; i < rows; i++) {
			
			for (int j = i + 1; j < rows; j++) {
			
				final double C = treangleSelf[j][i] / treangleSelf[i][i];

				for (int k = i; k < columns; k++) {
					if (k == i) {
						treangleSelf[j][k] = C;
					} else {
						treangleSelf[j][k] = treangleSelf[j][k]
								- (treangleSelf[i][k] * C);
					}
				}
			}
		}
		return treangle;
	}
	
	public static int sum(Matrix a){
		double mat[][] = a.toArray();
		int sum = 0;
		for(int i=0; i< mat.length;i++)
			for(int j=0;j<mat.length;j++)
				sum+= mat[i][j];
		return sum;
	}

	public static Matrix toTreangleMatrix(Matrix a) {

		Matrix treangle = a.clone();

		double treangleSelf[][] = treangle.toArray();

		final int rows = treangle.rows();
		final int columns = treangle.columns();

		for (int i = 0; i < rows; i++) {
			
			for (int j = i + 1; j < rows; j++) {
			
				final double C = treangleSelf[j][i] / treangleSelf[i][i];

				for (int k = i; k < columns; k++) {
					if (k == i) {
						treangleSelf[j][k] = 0.0;
					} else {
						treangleSelf[j][k] = treangleSelf[j][k]
								- (treangleSelf[i][k] * C);
					}
				}
			}
		}
		return treangle;
	}
}
