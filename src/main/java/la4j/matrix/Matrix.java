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

import java.io.Serializable;

import la4j.err.MatrixException;
import la4j.vector.Vector;

/**
 * Interface for matrix;
 *  
 * @author Vladimir Kostyukov
 * @date 2011/02/06
 */
public interface Matrix extends Cloneable, Serializable {

	/**
	 * Get (i, j) element of matrix;
	 *  
	 * @param i the row of matrix
	 * @param j the column of matrix
	 * @return element at (i, j) in matrix
	 */
	public double get(int i, int j);
	
	/**
	 * Set (i, j) elemnet of matrix;
	 * 
	 * @param i the row of matrix
	 * @param j the column of matrix
	 * @param value value to be stored at (i, j) in matrix
	 */
	public void set(int i, int j, double value);

	/**
	 * Set rows number of matrix;
	 * 
	 * @param rows the rows number
	 */
	public void setRows(int rows);
	
	/**
	 * Set columns number of matrix;
	 * 
	 * @param columns the columns number
	 */
	public void setColumns(int columns);

	/**
	 * Convert matrix to double array;
	 *  
	 * @return array with matrix data
	 */
	public double[][] toArray();
	
	/**
	 * Convert matrix to double array with deep copy;
	 *  
	 * @return array with matrix data
	 */
	public double[][] toArrayCopy();

	/**
	 * Swap i and j rows of matrix;
	 * 
	 * @param i the i row
	 * @param j the j row
	 */
	public void swapRows(int i, int j);
	
	/**
	 * Swap i and i columns of matrix;
	 * 
	 * @param i column
	 * @param j column
	 */
	public void swapColumns(int i, int j);

	/**
	 * Get rows number of matrix;
	 * 
	 * @return rows number
	 */
	public int rows();
	
	/**
	 * Get columns number of matrix;
	 * 
	 * @return columns number
	 */
	public int columns();
	
	/**
	 * Transpose the matrix;
	 * 
	 * @return transposed matrix
	 */
	public Matrix transpose();
	
	/**
	 * Scale matrix;
	 * 
	 * @param value matrix to be scaled
	 * @return scaled matrix
	 */
	public Matrix multiply(double value);
	
	/**
	 * Multiply matrix by matrix;
	 * 
	 * @param matrix to be multiplied
	 * @return multiplied matrix
	 * @throws MatrixException
	 */
	public Matrix multiply(Matrix matrix) throws MatrixException;
	
	/**
	 * Multiply matrix by vector;
	 * 
	 * @param vector to be multiplied
	 * @return multiplied matrix
	 * @throws MatrixException
	 */
	public Vector multiply(Vector vector) throws MatrixException;

	/**
	 * Subtract matrix by matrix;
	 * 
	 * @param matrix to be subtracted
	 * @return subtracted matrix
	 * @throws MatrixException
	 */
	public Matrix subtract(Matrix matrix) throws MatrixException;
	
	/**
	 * Add matrix by matrix;
	 * 
	 * @param matrix to be added
	 * @return added matrix
	 * @throws MatrixException
	 */
	public Matrix add(Matrix matrix) throws MatrixException;
	
	/**
	 * Calculate the trace of matrix; 
	 * 
	 * @return trace of matrix
	 */
	public double trace();
	
	
	/**
	 * Calculate determinant of matrix;
	 *  
	 * @return determinant;
	 */
	public double determinant();

	/**
	 * Get the i-th row of matrix;
	 * 
	 * @param i row
	 * @return the i-th row
	 */
	public Vector getRow(int i);
	
	/**
	 * Get the i-th column of matrix;
	 * 
	 * @param i column
	 * @return the i-th column
	 */
	public Vector getColumn(int i);

	/**
	 * Set the i-th column of matrix;
	 * 
	 * @param i column 
	 * @param column 
	 */
	public void setColumn(int i, Vector column);
	
	/**
	 * Set the i-th row of matrix;
	 * 
	 * @param i row
	 * @param row
	 */
	public void setRow(int i, Vector row);

	/**
	 * Check the matrix symmetries; 
	 * 
	 * @return  
	 */
	public boolean isSymmetric();

	/**
	 * Check the matrix treanglies;
	 * 
	 * @return
	 */
	public boolean isTreangle();

	/**
	 * Clone the matrix;
	 * 
	 * @return cloned matrix
	 */
	public Matrix clone();

	/**
	 * Convert matrix to string;
	 * 
	 * @return converted matrix
	 */
	public String toString();
    
    public int sum();

	public void transform(Function function);
	
	public interface Function{
		
		public abstract double apply(double d);
		
	} 
	
	public class ProbFunction implements Function{
		
		double p;
		public ProbFunction(double p){
			this.p = p;
		}

		public double apply(double d) {
			if(d < 1 && Math.random() < p)
				return 1;
			else if (d<1)
				return 0;
			return 1;
		}
		
	}
	
	public class ProbDeathFunction implements Function{
		double p;
		public ProbDeathFunction(double p){
			this.p = p;
		}
 
		
		public double apply(double d) {
			if(d > 0 && Math.random() < p)
				return 0;
			else if (d<1)
				return 0;
			return 1;
		}
		
	}

	Matrix or(Matrix matrix);
	
}
