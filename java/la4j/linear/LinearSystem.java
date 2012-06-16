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
import la4j.err.MatrixException;
import la4j.err.VectorException;
import la4j.matrix.Matrix;
import la4j.vector.Vector;

/**
 * LinearSystem class;
 * 
 * @author Vladimir Kostyukov
 * @date 2011/02/06 
 */
public class LinearSystem {
	
	public static final double EPS = 10e-7;

	private int equations;
	private int variables;
	
	private Matrix a;
	private Vector b;
	
	private LinearSystemSolver solver;
	
	public LinearSystem(Matrix a, Vector b) {
		this.a = a;
		this.b = b;
		
		this.equations = a.rows();
		this.variables = a.columns();
		
		this.solver = new EmptySolver();
	}
	
	public LinearSystem(Matrix a, Vector b, LinearSystemSolver solver) {
		this(a, b);
		
		this.solver = solver;
	}

	public int equations() {
		return equations;
	}

	public int variables() {
		return variables;
	}

	public Matrix coefficientsMatrix() {
		return a;
	}
	
	public Vector rightHandVector() {
		return b;
	}
	
	public LinearSystemSolver getSolver() {
		return solver;
	}
	
	public void setSolver(LinearSystemSolver solver) {
		this.solver = solver;
	}
	
	public Vector solve() throws LinearSystemException {
		return solver.solve(this);
	}
	
	public boolean isSolution(Vector x) {

		try {
			Vector r = innacary(x);

			boolean ret = true;

			for (int i = 0; i < r.length(); i++) {
				ret = ret && (Math.abs(r.get(i)) < EPS);
			}

			return ret;

		} catch (LinearSystemException ex) {
			return false;
		}
	}

	public Vector innacary(Vector x) throws LinearSystemException {
		try {

			return a.multiply(x).substsract(b);

		} catch (MatrixException ex) {
			throw new LinearSystemException(ex.getMessage());
		} catch (VectorException ex) {
			throw new LinearSystemException(ex.getMessage());
		}
	}
}
