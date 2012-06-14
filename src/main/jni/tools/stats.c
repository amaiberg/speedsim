/*
 * stats.c
 *
 * TODO comment here
 *
 * Created on: Jan 2, 2012
 * Author: Emma Strubell
 */

#include <math.h>
#include <stdio.h>
#include "../tools/rand01.h"

#define betamode(a, b) ((a-1)/(a+b-2))
#define betasd(x, a, b) (pow(x,a-1)*pow(1-x,b-1))

#define binommean(n, p) (n*p)
#define binomvar(n, p) (n*p*(1-p))
#define binom_s3(n, p) (binomvar(n, p) + 0.5)
#define binom_a(n, p) (binommean(n, p) + 0.5)

const double S1 = 0.44945808102945;	// 3/2 - sqrt(3/M_E);
const double S2 = 0.85776388496071;	// sqrt(2/M_E);

/* Returns a beta random number with shape parameters a and b (rejection method).
 * Adapted from: http://www.mas.ncl.ac.uk/~ndjw1/teaching/sim/reject/beta.html
 */
double beta(double a, double b){
	double x, y, fx;
	do{
		x = rand01_inclusive();
		y = randxy(0, betasd(betamode(a, b), a, b));
		fx = betasd(x, a, b);
	}while(y >= fx);
	return x;
}

/* Naive method of generating a binomial (requires n iterations) */
double binomial_naive(int n, double p){
	double x = 0.0;
	int i;
	for(i = 0; i < n; i++)
		if(rand01_inclusive() < p)
			x++;
	//printf("%f: %d\n", p, x);
	return x;
}

/* Returns a binomial random number with shape parameters n and p */
double binomial(int n, double p){
	return binomial_naive(n, p);
	//return binomial_rejection(n, p);
}

/* Ratio-of-uniforms rejection method for generating a binomial
 * See: http://www.agner.org/random/sampmet.pdf */
double binomial_rejection(double n, double p){
	double x, y, s, a, fx, k, u, v;
	a = binom_a(n, p);
	s = S2 * binom_s3(n, p) + S1;
	do{
		u = rand01_inclusive();
		v = randxy(0,2)-1;		// interval [-1, 1]
		x = floor(s*v/u + a);
		y = u*u;
		//fx = ;
	}while(v >= fx/k);
	return u;
}
