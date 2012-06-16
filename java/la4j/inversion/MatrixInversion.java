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

import la4j.err.MatrixInversionException;
import la4j.matrix.Matrix;

/**
 * Matrix inversion class;
 * 
 * @author Vladimir Kostyukov
 * @date 2011/02/06
 */
public class MatrixInversion {
	
	private Matrix matrix;
	private MatrixInvertor invertor;

	public MatrixInversion(Matrix matrix) {
		this.matrix = matrix;
		this.invertor = new EmptyInvertor();
	}
	
	public MatrixInversion(Matrix matrix, MatrixInvertor invertor) {
		this.matrix = matrix;
		this.invertor = invertor;
	}

	public Matrix inverse() throws MatrixInversionException {
		return invertor.inverse(this);
	}
	
	public Matrix matrix() {
		return matrix;
	}

	public MatrixInvertor getInvertor() {
		return invertor;
	}

	public void setInvertor(MatrixInvertor invertor) {
		this.invertor = invertor;
	}
}
