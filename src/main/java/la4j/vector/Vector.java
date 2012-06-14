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

public class Vector implements Cloneable {

	private double vector[];
	private int length;

	public Vector() {
		length = 0;
		vector = new double[0];
	}

	public Vector(double array[]) {
		length = array.length;
		vector = array;
	}

	public Vector(int len) {
		length = len;
		vector = new double[length];
	}

	public double get(int i) {
		return vector[i];
	}

	public void set(int i, double value) {
		vector[i] = value;
	}

	public int length() {
		return length;
	}

	@Override
	public Vector clone() {
		Vector clon = new Vector(length);

		System.arraycopy(vector, 0, clon.vector, 0, length);

		return clon;
	}

	public double[] toArray() {
		return vector;
	}

	public double[] toArrayCopy() {
		double arraycopy[] = new double[length];

		System.arraycopy(vector, 0, arraycopy, 0, length);

		return arraycopy;
	}

	public void setLength(int length) {
        double arraycopy[] = new double[length];

        System.arraycopy(vector, 0, arraycopy, 0, length);

        vector = arraycopy;
    }


	public Vector add(double d) {
		double self[] = new double[length];
		for (int i = 0; i < length; i++) {
			self[i] = vector[i] + d;
		}

		return new Vector(self);
	}

	public Vector add(Vector v) throws VectorException {
		if (length == v.length) {
			double self[] = new double[length];
			for (int i = 0; i < length; i++) {
				self[i] = vector[i] + v.vector[i];
			}

			return new Vector(self);
		} else {
			throw new VectorException(
					"can not sum this vectors: wrong dimmentions");
		}
	}

	public Vector multiply(double d) {
		double self[] = new double[length];
		for (int i = 0; i < length; i++) {
			self[i] = vector[i] * d;
		}

		return new Vector(self);
	}

	public Vector multiply(Vector v) throws VectorException {
		if (length == v.length) {
			double self[] = new double[length];
			for (int i = 0; i < length; i++) {
				self[i] = vector[i] * v.vector[i];
			}

			return new Vector(self);
		} else {
			throw new VectorException(
					"can not sum this vectors: wrong dimmentions");
		}

	}

	public Vector substract(double d) {
		double self[] = new double[length];
		for (int i = 0; i < length; i++) {
			self[i] = vector[i] - d;
		}

		return new Vector(self);
	}

	public Vector substsract(Vector v) throws VectorException {
		if (length == v.length) {
			double self[] = new double[length];
			for (int i = 0; i < length; i++) {
				self[i] = vector[i] - v.vector[i];
			}

			return new Vector(self);
		} else {
			throw new VectorException(
					"can not sum this vectors: wrong dimmentions");
		}
	}

	public Vector div(double d) {
		double self[] = new double[length];
		for (int i = 0; i < length; i++) {
			self[i] = vector[i] / d;
		}
		return new Vector(self);
	}

	public Vector div(Vector v) throws VectorException {
		if (length == v.length) {
			double self[] = new double[length];
			for (int i = 0; i < length; i++) {
				self[i] = vector[i] / v.get(i);
			}
			return new Vector(self);
		} else {
			throw new VectorException(
					"can not sum this vectors: wrong dimmentions");
		}
	}

	public double scalarProduct(Vector v) throws VectorException {
		if (length == v.length) {
			double s = 0.0;
			for (int i = 0; i < vector.length; i++) {
				s += vector[i] * v.vector[i];
			}

			return s;
		} else {
			throw new VectorException(
					"can not calc scalar product of this vectors: wrong dimmentions");
		}
	}

	public double norm() {
		try {
			return Math.sqrt(scalarProduct(this));
		} catch (VectorException ex) {
			return 0.0;
		}
	}

	public Vector normalize() {
		return div(norm());
	}

	public void swap(int i, int j) {
		double d = vector[i];
		vector[i] = vector[j];
		vector[j] = d;
	}
	
	public Vector get(int from, int to) {
		Vector v = new Vector(to -from);
		if(to < length)
			for(int i=0;i< to-from;i++)
				v.set(i,this.get(from+i));
		else return null;
		return v;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < vector.length; i++) {
			sb.append(String.format("%6.3f", vector[i]));
			sb.append((i < vector.length - 1 ? ", " : ""));
		}
		sb.append("]");

		return sb.toString();
	}
}
