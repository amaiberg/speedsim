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

import java.util.Random;

public abstract class VectorFactory {

	private VectorFactory() {
	}

	public static Vector createVector() {
		return new Vector();
	}

	public static Vector createVector(int len) {
		return new Vector(len);
	}

	public static Vector createVector(double array[]) {
		return new Vector(array);
	}

	public static Vector createVectorWithCopy(double array[]) {
		double arraycopy[] = new double[array.length];

		System.arraycopy(array, 0, arraycopy, 0, array.length);

		return new Vector(arraycopy);
	}

	public static Vector createRandomVector(int len,int vals) {

		Vector v = new Vector(len);

		Random r = new Random();
		for (int i = 0; i < len; i++) {
			v.set(i, r.nextInt(vals));
		}

		return v;
	}
}
