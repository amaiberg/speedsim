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
import la4j.vector.VectorFactory;

public class DenseMatrix implements Matrix {

	private static final long serialVersionUID = 1L;

	public static final double EPS = 10e-7;

	/**
	 * matrix internal storage; 
	 */
	private double self[][];

	/**
	 *  rows of matrix;
	 */
	private int rows;
	
	/**
	 *  columns of matrix;
	 */
	private int columns;

	/* default constructor */
	public DenseMatrix() {
		self = new double[0][0];
		rows = 0;
		columns = 0;
	}

	/* constructor from double array */
	public DenseMatrix(double array[][]) {
		self = array;
		rows = array.length;
		columns = array[0].length;
	}

	/* constructor with specify dimention */
	public DenseMatrix(int row, int col) {
		self = new double[row][col];
		rows = row;
		columns = col;
	}
    
    public int sum(){
        int sum =0;
        for(int i =0 ; i< this.rows(); i++)
            for(int j=0;j<this.columns();j++)
                sum+= self[i][j] ;

        return sum;
    }

	
	public double get(int i, int j) {
		return self[i][j];
	}

	
	public void set(int i, int j, double value) {
		self[i][j] = value;
	}

	
	public void setRows(int rows) {
		this.rows = rows;

		double newSelf[][] = new double[rows][columns];

		for (int i = 0; i < rows; i++) {
			newSelf[i] = self[i];
		}

		self = newSelf;
	}

	
	public void setColumns(int columns) {
		this.columns = columns;

		double newSelf[][] = new double[rows][columns];

		for (int i = 0; i < rows; i++) {
	//		newSelf[i] = Arrays.copyOf(self[i], columns);
		}

		self = newSelf;
	}

	
	public double[][] toArray() {
		return self;
	}

	
	public double[][] toArrayCopy() {
		double copy[][] = new double[rows][columns];

		for (int i = 0; i < rows; i++) {
			System.arraycopy(self[i], 0, copy[i], 0, columns);
		}

		return copy;
	}

	
	public void swapRows(int i, int j) {
		double dd[] = self[i];
		self[i] = self[j];
		self[j] = dd;
	}

	
	public void swapColumns(int i, int j) {
		for (int _i = 0; _i < rows; _i++) {
			double d = self[_i][i];
			self[_i][i] = self[_i][j];
			self[_i][j] = d;
		}
	}

	
	public int rows() {
		return rows;
	}

	
	public int columns() {
		return columns;
	}

	
	public DenseMatrix transpose() {
		
		final int thisRows = rows;
		final int thisColumns = columns;
		
		double result[][] = new double[thisColumns][thisRows];
		for (int i = 0; i < thisRows; i++) {
			for (int j = 0; j < thisColumns; j++) {
				result[j][i] = self[i][j];
			}
		}
		return new DenseMatrix(result);
	}

	
	public DenseMatrix multiply(double value) {
		
		final int thisRows = rows;
		final int thisColumns = columns;
		
		double result[][] = new double[thisRows][thisColumns];
		for (int i = 0; i < thisRows; i++) {
			for (int j = 0; j < thisColumns; j++) {
				result[i][j] = self[i][j] * value;
			}
		}
		return new DenseMatrix(result);
	}

	
	public Matrix multiply(Matrix matrix) throws MatrixException {

		final int thisRows = this.rows;
		final int thisColumns = this.columns;
		final int thatRows = matrix.rows();
		final int thatColumns = matrix.columns();

		if (thisColumns != thatRows) {
			throw new MatrixException(
					"can not multiply this matrix: wrong dimmentions");
		}

		DenseMatrix result = new DenseMatrix(thisRows, thatColumns);

		double self[][] = result.toArray();

		double thatColumn[] = new double[thatRows];

		for (int j = 0; j < thatColumns; j++) {
			for (int k = 0; k < thisColumns; k++) {
				thatColumn[k] = matrix.get(k, j);
			}

			for (int i = 0; i < thisRows; i++) {
				double thisRow[] = self[i];
				double summand = 0;
				for (int k = 0; k < thisColumns; k++) {
					summand += thisRow[k] * thatColumn[k];
				}
				self[i][j] = summand;
			}
		}

		return result;
	}

	
	public Vector multiply(Vector vector) throws MatrixException {
		
		final int thisRows = rows;
		final int thisColumns = columns;
		
		if (thisColumns == vector.length()) {
			
			double result[] = new double[thisColumns];
			for (int i = 0; i < thisRows; i++) {
				double summand = 0;
				for (int j = 0; j < thisColumns; j++) {
					summand += self[i][j] * vector.get(j);
				}
				
				result[i] = summand;
			}
			return new Vector(result);
		} else {
			throw new MatrixException(
					"can not multiply this matrix on vector: wrong dimmentions");
		}
	}

	
	public Matrix subtract(Matrix matrix) throws MatrixException {
		
		final int thisRows = rows;
		final int thisColumns = columns;
		final int thatRows = matrix.rows();
		final int thatColumns = matrix.columns();
		
		final double thisSelf[][] = self;
		final double thatSelf[][] = matrix.toArray();
		
		if (thisRows == thatRows && thisColumns == thatColumns) {
			double result[][] = new double[thisRows][thisColumns];
			for (int i = 0; i < thisRows; i++) {
				for (int j = 0; j < thisColumns; j++) {
					result[i][j] = thisSelf[i][j] - thatSelf[i][j];
				}
			}

			return new DenseMatrix(result);

		} else {
			throw new MatrixException(
					"can not substract this matrix: wrong dimmentions");
		}

	}

	
	public Matrix add(Matrix matrix) throws MatrixException {
		
		final int thisRows = rows;
		final int thisColumns = columns;
		final int thatRows = matrix.rows();
		final int thatColumns = matrix.columns();
		
		final double thisSelf[][] = self;
		final double thatSelf[][] = matrix.toArray();
		
		if (thisRows == thatRows && thisColumns == thatColumns) {
			double result[][] = new double[thisRows][thisColumns];
			for (int i = 0; i < thisRows; i++) {
				for (int j = 0; j < thisColumns; j++) {
					result[i][j] = thisSelf[i][j] + thatSelf[i][j];
				}
			}
			return new DenseMatrix(result);
		} else {
			throw new MatrixException(
					"can not sum this matrix: wrong dimmentions");
		}
	}
	
	
	public Matrix or(Matrix matrix) {
		
		final int thisRows = rows;
		final int thisColumns = columns;
		final int thatRows = matrix.rows();
		final int thatColumns = matrix.columns();
		
		final double thisSelf[][] = self;
		final double thatSelf[][] = matrix.toArray();
		
		if (thisRows == thatRows && thisColumns == thatColumns) {
			double result[][] = new double[thisRows][thisColumns];
			for (int i = 0; i < thisRows; i++) {
				for (int j = 0; j < thisColumns; j++) {
					result[i][j] = (thatSelf[i][j] == 1 || thisSelf[i][j] ==1)? 1: 0;
				} 
			}
			return new DenseMatrix(result);
		}
		else return null;
	}
	
	
	public double trace() {
		double tr = 1;
		for (int i = 0; i < rows; i++) {
			tr *= self[i][i];
		}

		return tr;
	}

	
	public double determinant() {
		//return MatrixUtils.createTreangleMatrixKeepCoeficients(this).trace();
		return MatrixUtils.toTreangleMatrix(this).trace();
	}

	
	public Vector getRow(int i) {
		Vector v = VectorFactory.createVector(columns);
		for (int j = 0; j < columns; j++) {
			v.set(j, self[i][j]);
		}

		return v;
	}

	
	public Vector getColumn(int i) {
		Vector v = VectorFactory.createVector(rows);
		for (int j = 0; j < rows; j++) {
			v.set(j, self[j][i]);
		}

		return v;
	}

	
	public void setColumn(int i, Vector column) {
		for (int j = 0; j < column.length(); j++) {
			self[j][i] = column.get(j);
		}
	}

	
	public void setRow(int i, Vector row) {
		for (int j = 0; j < row.length(); j++) {
			self[i][j] = row.get(j);
		}
	}

	
	public boolean isSymmetric() {
		boolean ret = true;
		for (int i = 0; i < rows; i++) {
			for (int j = i + 1; j < columns; j++) {
				ret = ret && (Math.abs(self[i][j] - self[j][i]) < EPS);
			}
		}
		return ret && rows > 0 && columns > 0;
	}

	
	public boolean isTreangle() {
		boolean ret = true;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < i + 1; j++) {
				ret = ret && self[i][j] == 0.0;
			}
		}
		return ret && rows > 0 && columns > 0;
	}

	
	public Matrix clone() {
		DenseMatrix clon = new DenseMatrix(rows, columns);

		for (int i = 0; i < rows; i++) {
			System.arraycopy(self[i], 0, clon.self[i], 0, columns);
		}

		return clon;
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				sb.append(String.format("%8.3f", self[i][j]));
			}
			sb.append("\n");
		}

		return sb.toString();
	}
	
	public void transform(Function f){
		for (int i =0; i<rows;i++)
			for(int j=0;j<rows;j++)
				self[i][j] = f.apply(self[i][j]);
	}



}
