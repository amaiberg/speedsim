#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <worm_simulation.h>


int SIZE;
JNIEXPORT jint JNICALL
Java_speedlab4_CTestSimModel_initsim( JNIEnv* env,jobject thisObj, jint size, jobject fd_sys,jlong off,jlong len){

	FILE* LandscapePointer;
	jclass fdClass = (*env)->FindClass(env, "java/io/FileDescriptor");
	if (fdClass != NULL)
	{
		jclass fdClassRef = (jclass) (*env)->NewGlobalRef(env, fdClass);
		jfieldID fdClassDescriptorFieldID = (*env)->GetFieldID(env,
				fdClass, "descriptor", "I");
		if (fdClassDescriptorFieldID != NULL && fd_sys != NULL)
		{
			jint fd = (*env)->GetIntField(env, fd_sys,
					fdClassDescriptorFieldID);
			int myfd = dup(fd);
			LandscapePointer = fdopen(myfd, "rb");
			if (LandscapePointer)
			{
	
				SIZE = size;
				init((int32_t)size,1000,1,100,3,10,50,1,0.125,0.5,0.375,0.0,0.0,0.0,0.0,"land0","-",SIRS,0,0,0,0, LandscapePointer, (long)off, (long)len);
				return 0;
			}
			else return 1;
		}
	}

	return 1;

}

jobjectArray
Java_speedlab4_CTestSimModel_next( JNIEnv* env)
{
	int size = (int32_t)SIZE;
	double ** sim_array;
	sim_array = next();
	//  if(sim_array == NULL)
	//   printf("%d",** sim_array);
	jobjectArray result;
	int i,j;
	jclass intArrCls = (*env)->FindClass(env, "[D");

	if (intArrCls == NULL) {
		return NULL; /* exception thrown */
	}
	result = (*env)->NewObjectArray(env, size, intArrCls,NULL);

	if (result == NULL) {
		return NULL; /* out of memory error thrown */
	}

	for (i = 0; i < size; i++)
	{
		jdouble tmp[500]; /* make sure it is large enough! */
		jdoubleArray darr = (*env)->NewDoubleArray(env, size);

		if (darr == NULL)
		{
			return NULL; /* out of memory error thrown */
		}

		for (j = 0; j < size; j++)
		{
			tmp[j] = sim_array[i][j]; //((rand() % 100) > 50)? 1.0: 0.0;
		}

		(*env)->SetDoubleArrayRegion(env, darr, 0, size, tmp);
		(*env)->SetObjectArrayElement(env, result, i, darr);
		(*env)->DeleteLocalRef(env, darr);
	}
	return result;
}
