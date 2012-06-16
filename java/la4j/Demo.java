/**
 * Copyright 2011 la4j 
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

package la4j;

import la4j.decomposition.EigenDecompositor;
import la4j.decomposition.MatrixDecomposition;
import la4j.err.MatrixDecompositionException;
import la4j.err.MatrixException;
import la4j.err.MatrixInversionException;
import la4j.inversion.GaussianInvertor;
import la4j.inversion.MatrixInversion;
import la4j.matrix.Matrix;
import la4j.matrix.MatrixFactory;
import la4j.matrix.MatrixUtils;

public class Demo {
	
	public static final int SIZE = 1000;
	
	public static void main(String args[]) throws Exception {
		
	//	matrixOperations();
	//	linearSystemSolving();
	//	matrixIversion();
		matrixDecomposition();
	}

	public static void matrixOperations() throws MatrixException {

		Matrix a = MatrixFactory.createRandomDenseMatrix(SIZE, SIZE);
		Matrix b = MatrixFactory.createRandomDenseMatrix(SIZE, SIZE);
		
		
		long t1 = System.currentTimeMillis();
		Matrix c = MatrixUtils.multiply(a, b);
		long t2 = System.currentTimeMillis();
		
		System.out.println("la4j matrix (1000x1000) multiplication = " + ((double) (t2 - t1) / 1000) + " s");
	}
	/*
	public static void linearSystemSolving() throws LinearSystemException {
		
		Matrix a = MatrixFactory.createRandomDenseMatrix(SIZE, SIZE);
	//	Vector b = VectorFactory.createRandomVector(SIZE);
		
		LinearSystem linearSystem = new LinearSystem(a, b);
		
		linearSystem.setSolver(new GaussianSolver());
		long t1 = System.currentTimeMillis();
		Vector x1 = linearSystem.solve();
		long t2 = System.currentTimeMillis();
		
		System.out.println("la4j linear system (1000x1000) solving (gaussian) = " + ((double) (t2 - t1) / 1000) + " s");

		linearSystem.setSolver(new SweepSolver());
		long t3 = System.currentTimeMillis();
		Vector x2 = linearSystem.solve();
		long t4 = System.currentTimeMillis();
		
		System.out.println("la4j linear system (1000x1000) solving (sweep) = " + ((double) (t4 - t3) / 1000) + " s");
	}
	*/
	public static void matrixInversion() throws MatrixInversionException {
		
		Matrix a = MatrixFactory.createRandomDenseMatrix(SIZE, SIZE);
		MatrixInversion matrixInversion = new MatrixInversion(a);
		matrixInversion.setInvertor(new GaussianInvertor());
		long t1 = System.currentTimeMillis();
		Matrix invertedA = matrixInversion.inverse();
		long t2 = System.currentTimeMillis();
		
		System.out.println("la4j matrix (1000x1000) inversion (gaussian) = " + ((double) (t2 - t1) / 1000) + " s");
	}
	
	public static void matrixDecomposition() throws MatrixDecompositionException {
		
		Matrix a = MatrixFactory.createRandomSymmetricDenseMatrix(50, 50);
		MatrixDecomposition matrixDecomposition = new MatrixDecomposition(a);
		EigenDecompositor decompositor = new EigenDecompositor();
		matrixDecomposition.setDecompositor(decompositor);
		long t1 = System.currentTimeMillis();
		
		/**
		 * matices[0] = decompositor.D();
		 * matrice[1] = decompositor.V(); 
		 */
		Matrix[] matrices = matrixDecomposition.decompose();
		
		long t2 = System.currentTimeMillis();
		
		System.out.println("la4j matrix (50x50) decomposition (eigen) = " + ((double) (t2 - t1) / 1000) + " s");
	}
}
