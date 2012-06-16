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

package la4j.vector;

import la4j.err.VectorException;

public abstract class VectorUtils {
	private VectorUtils() {
	}

	public static Vector multiply(Vector a, double d) {
		return a.multiply(d);
	}

	public static Vector multiply(Vector a, Vector b) throws VectorException {
		return a.multiply(b);
	}

	public static Vector add(Vector a, double d) {
		return a.add(d);
	}

	public static Vector add(Vector a, Vector b) throws VectorException {
		return a.add(b);
	}

	public static Vector substract(Vector a, double d) {
		return a.substract(d);
	}

	public static Vector substract(Vector a, Vector b) throws VectorException {
		return a.substsract(b);
	}

	public static double scalarProduct(Vector a, Vector b)
			throws VectorException {
		return a.scalarProduct(b);
	}

	public static Vector div(Vector a, double d) {
		return a.div(d);
	}
	
	public static Vector div(Vector a, Vector b) throws VectorException {
		return a.div(b);
	}

	public static Vector normalize(Vector a) {
		return a.normalize();
	}

	public static double norm(Vector a) {
		return a.norm();
	}

	public static Vector expandVector(Vector a, double d) {
		Vector b = a.clone();
		b.setLength(b.length() + 1);
		b.set(b.length() - 1, d);

		return b;
	}
}
