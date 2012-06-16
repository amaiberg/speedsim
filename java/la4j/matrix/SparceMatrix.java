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

public class SparceMatrix implements Matrix {

	private static final long serialVersionUID = 1L;

	
	public double get(int i, int j) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void set(int i, int j, double value) {
		// TODO Auto-generated method stub
		
	}

	
	public void setRows(int rows) {
		// TODO Auto-generated method stub
		
	}

	
	public void setColumns(int columns) {
		// TODO Auto-generated method stub
		
	}

	
	public double[][] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public double[][] toArrayCopy() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void swapRows(int i, int j) {
		// TODO Auto-generated method stub
		
	}

	
	public void swapColumns(int i, int j) {
		// TODO Auto-generated method stub
		
	}

	
	public int rows() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int columns() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Matrix transpose() {
		// TODO Auto-generated method stub
		return null;
	}
	public void transform(Function f){

	}


	
	public Matrix multiply(double value) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Matrix multiply(Matrix matrix) throws MatrixException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Vector multiply(Vector vector) throws MatrixException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Matrix subtract(Matrix matrix) throws MatrixException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Matrix add(Matrix matrix) throws MatrixException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public double trace() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double determinant() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Vector getRow(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Vector getColumn(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setColumn(int i, Vector column) {
		// TODO Auto-generated method stub
		
	}

	
	public void setRow(int i, Vector row) {
		// TODO Auto-generated method stub
		
	}

	
	public boolean isSymmetric() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isTreangle() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Matrix clone() {
		return null;
	}

    public int sum() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
	public Matrix or(Matrix matrix) {
		// TODO Auto-generated method stub
		return null;
	}
}
