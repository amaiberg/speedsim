#include <math.h>
#include <stdio.h>
#include <assert.h>

double logyRegress(double *xvals, double *yvals, int numVals){
	int i;
	double x, y, sumx, sumy, sumxy, sumxx;
	sumx = sumy = sumxy = sumxx = 0.0;

	for(i = 0; i < numVals; i++){
		x = xvals[i];
		//assert(yvals[i] > 0);
		y = log(yvals[i]);
		sumx += x;
		sumy += y;
		sumxy += x*y;
		sumxx += x*x;
	}
	//fprintf(stderr, "sumx: %f; sumy: %f; sumxy: %f; sumxx: %f\n\n", sumx, sumy, sumxy, sumxx);
	return ((double)numVals*sumxy - sumx*sumy)/((double)numVals*sumxx - sumx*sumx);
}
