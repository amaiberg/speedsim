#include <stdio.h>
#include <stdlib.h>
#include "rand01.h"
#include "errors.h"

/*
 * Comment this
 */
int nvalsrndrescale(double *pvec, int n) {
	double *csum = malloc(n*sizeof(double));
	double rNum;
	double sum = 0;
	int cnt;
	csum[0] = *pvec;
	for (cnt = 1; cnt < n; cnt++) {
		csum[cnt] = csum[cnt-1] + pvec[cnt];
	}
	sum = csum[n-1];
	cnt = 0;
	rNum = rand01()*sum;
	while (csum[cnt] < rNum)
		cnt++;
	free(csum);
	return cnt;
}

/*
 * This is an integer version of Professor David Hiebeler's nvalsrndrescale
 * for when you really really want to nvalsrndrescale on ints instead of doubles.
 */
int dnvalsrndrescale(int *pvec, int n) {
	int *csum = malloc(n*sizeof(int));
	double rNum;
	int sum = 0;
	int cnt;
	csum[0] = *pvec;
	for (cnt = 1; cnt < n; cnt++) {
		csum[cnt] = csum[cnt-1] + pvec[cnt];
	}
	sum = csum[n-1];
	cnt = 0;
	rNum = rand01()*sum;
	while (csum[cnt] < rNum)
		cnt++;
	free(csum);
	return cnt;
}

/*
 * This method is for when you have relative probabilities which don't
 * sum to one, and you know the sum ahead of time.
 * It should run much faster.
 */
int randvalRescaleSum(double *probs, int n, double sum){
	double d, dsum;
	int i;

	if(sum < 0.0){
		fprintf(stderr, "Passed sum < 0.0 (%f) in randvalRescaleSum\n", sum);
		//err("Passed sum < 0.0 to randvalRescaleSum...");
	}

	d = rand01()*sum;
	dsum = 0.0;
	for (i=0; i < n; i++) {
		dsum += probs[i];
		if (dsum > d)
			return i;
	}
	//fprintf(stderr, "error error randvalrescalesum 1\n");
	//err("Bad value in randvalRescaleSum");
	return (int)randxy(0,n-1);
}
