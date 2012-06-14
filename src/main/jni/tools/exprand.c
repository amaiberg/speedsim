/*
 * File: exprand.c
 *   By: David Hiebeler
 *       hiebeler@cam.cornell.edu
 *       Sept 1998
 *
 * Generate pseudo-random values with exponential distribution
 */

#include <stdlib.h>
#include <math.h>
#include "mtrand.h"

#define RAND01_PRECISION 100000

/* Generate a random number from an exponential distribution with
 * parameter lambda (the mean is 1/lambda) */
double
randExp(double lambda)
{
    double u;
    /* long int r; */

    /* first generate a random number between 1...RAND01_PRECISION inclusive */
    /* r = (random()%RAND01_PRECISION) + 1; */
    /* then transform r into a random double in (0,1] */
    /* u = ((double)r) / (double)RAND01_PRECISION; */
    u = ((double) genrand_int31()+1.0) / (double)(RAND_MAX+1.0);  /* be careful not to generate the value 0 */
    /* now transform u to an exponential distribution */
    return ((-1.0/lambda) * log(u)); /* technically should use log(1-u) but
				      * this also gives the proper result */
}


/* Generate a random number from an exponential distribution with
 * the given mean. */
double
randExpMean(double mean)
{
    double u;
    /* long int r; */

    /* first generate a random number between 1...RAND01_PRECISION inclusive */
    /* r = (random()%RAND01_PRECISION) + 1; */
    /* then transform r into a random double in (0,1] */
    /* u = ((double)r) / (double)RAND01_PRECISION; */
    u = ((double) random()) / (double)RAND_MAX;
    /* now transform u to an exponential distribution */
    return (-1.0 * mean * log(u)); /* technically should use log(1-u) but
				      * this also gives the proper result */
}
