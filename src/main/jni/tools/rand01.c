/*
 * File: rand01.c
 *   By: David Hiebeler
 *       hiebeler@cam.cornell.edu
 *
 * Routines for generating uniform random number in [0,1), and for
 * returning the value 1 a specified fraction of the time.
 */
#include "mtrand.h"
#include <stdlib.h>

/* #define RAND01_PRECISION 1000000000 */
#define RAND01_PRECISION 1000000

/* Generate a random number in [0,1) */
double
rand01old()
{
	int i;
	double d;

	i = random() % (RAND01_PRECISION);
	d = ((double) i) / (double)RAND01_PRECISION;
	return d;
}


/* Generate a random number in [0,1) */
double
rand01()
{
	double d;

	d = ((double) genrand_int31()) / (((double)RAND_MAX) + 1.0);
	return d;
}

/* Generate a random number in [0,1] */
double
rand01_inclusive()
{
	double d;
	d = ((double) genrand_int31()) / (((double)RAND_MAX));
	return d;
}

/* Generate a random number in [x,y] */
double
randxy(int x, int y)
{
	return x + (y-x)*rand01_inclusive();
}

/*
 * Return 1 a specified fraction of the time, else return 0
 */
int
randFracold(double frac)
{
	int i;
	double d;

	i = random() % RAND01_PRECISION;
	d = ((double) i) / (double)RAND01_PRECISION;
	if (d < frac)
		return 1;
	else
		return 0;
}


/*
 * Return 1 a specified fraction of the time, else return 0
 */
int
randFrac(double frac)
{
	if (((double)genrand_int31() ) / (double)RAND_MAX < frac)
		return 1;
	else
		return 0;
}
