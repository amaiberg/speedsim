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
import la4j.matrix.Matrix;

/**
 * Matrix decomposition class;
 * 
 * @author Vladimir Kostyukov
 * @date 2011/02/06
 */
public class MatrixDecomposition {

	private Matrix matrix;
	private MatrixDecompositor decompositor;
	
	public MatrixDecomposition(Matrix matrix) {
		this.matrix = matrix;
		this.decompositor = new EmptyDecompositor();
	}

	public MatrixDecomposition(Matrix matrix, MatrixDecompositor decompositor) {
		this.matrix = matrix;
		this.decompositor = decompositor;
	}
	
	public Matrix matrix() {
		return matrix;
	}
	
	public Matrix[] decompose() throws MatrixDecompositionException {
		return decompositor.decompose(this);
	}

	public MatrixDecompositor getDecompositor() {
		return decompositor;
	}

	public void setDecompositor(MatrixDecompositor decompositor) {
		this.decompositor = decompositor;
	}
}
