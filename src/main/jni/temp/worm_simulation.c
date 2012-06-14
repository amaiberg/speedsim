/*
 * File: worm_simulation.c
 *   by: Andrew Audibert
 *       aaudibert10@gmail.com
 *
 * Given a file describing an initial configuration of
 * susceptible locations, this program runs a worm
 * simulation and creates a text file showing the
 * proportion of the locations infected at every
 * given time interval.
 *
 * The landscape file should be titled
 * "Internet_Landscape###.txt", where ### is the number
 * of neighborhoods/households per neighborhood.
 *
 * The indentification number at the end of the output
 * file from this program should be specified as a
 * command line argument.
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <math.h>
#include <errno.h>
#include <assert.h>
#include "../tools/rand01.h"
#include "../tools/exprand.h"
#include "../tools/mtrand.h"
#include "../tools/nvalsrndrescale.h"
#include "../tools/regression.h"
#include "findeq.h"
#include "worm_simulation.h"

double TOTAL;
int SIZE, HH_PER_N, HH_MAX_POP, NUM_N, N_MAX_POP;
int hind, nind;
int numIn;
double tmp;
double eventCount;
SimType simType;
EventType eventType;

double SumSIn_A, SumSIn_B, SumSIhhSums_A, SumSIhhSums_B;
double SumIRhhSums_A, SumIRhhSums_B, SumIRn_A, SumIRn_B;
double SumIIhh_A, SumIIn_A, SumIIhh_B, SumIIn_B;
double currentI_A, currentI_B, currentI, currentS, currentR;

double **Ihh_A, **SIhh_A, **IRhh_A;
double **Ihh_B, **SIhh_B, **IRhh_B;
double **Shh, **Rhh;
double *Sn, *Rn;
double *In_A, *SIhhSums_A, *SIn_A, *IRhhSums_A, *IRn_A;
double *In_B, *SIhhSums_B, *SIn_B, *IRhhSums_B, *IRn_B;

void init(int size, int NUMI0, double MU, double PHI, double GAMMA, double MINTIME,
		double MAXTIME, double TIMEINTERVAL, double PLONG_A, double PMED_A,
		double PSHORT_A, double PLONG_B, double PMED_B, double PSHORT_B, double PSWITCH,
		char *landFname, char *logFname, SimType SIMTYPE, int ONELINE, int seeded,
		unsigned int randomSeed, int rank) {

	/* seed random number generator */
	if (seeded)
		init_genrand(randomSeed);
	else
		randomSeed = my_srandom_save_seed();

	SIZE = size;
	HH_MAX_POP = SIZE*SIZE;						// max number of individuals per household
	HH_PER_N = SIZE;							// number of households per neighborhood
	NUM_N = SIZE;								// number of neighborhoods
	N_MAX_POP = HH_MAX_POP*HH_PER_N;			// max number of individuals per neighborhood
	TOTAL = (double)HH_MAX_POP*HH_PER_N*NUM_N;	// total number of individuals overall

	simType = SIMTYPE;							// type of simulation (SIR, SIRS, or SIS)

	/* for debugging */
	fprintf(stderr, "rank: %d, PID: %d, seed: %u\n", rank, getpid(), randomSeed);

	/* structure for determining equilibrium at the end */
	double *eqarray = malloc(256*sizeof(double));
	eqdata *eqtest = initEqData(eqarray, 50, 50);

	/* stuff to keep track of initial growth rate */
	int IGTime = 10; /* estimated length of initial growth interval (after delay) */
	int IGRDataLen = IGTime/TIMEINTERVAL;
	int peakIdx = 0;
	int IGRDataCnt = 0; /* current # entries in IGRData */
	int recordIGRdata = 1; /* whether to continue keeping track of I vals */
	double *IGRData = malloc(IGRDataLen*sizeof(double));
	double *IGRtData = malloc(IGRDataLen*sizeof(double));
	double lastSlope, currentSlope, slopeDelta;

	int i, j, doneSetup, startregress, rapidGrowth, peakInumIn, negSlopeCnt;

	/* notes:
	 * SIhh contains S*I for each household  (so it's a 256x256 matrix)
	 * SIhhSums contains that value, summed within each nhood (so it's a vector of length 256)
	 * SumSIhhSums contains that summed over all nhoods, so it's a scalar value
	 */
	double currentTime, peakTime, lastTimeRecorded, lastTime;
	double plongSuccA, pmedSuccA, pshortSuccA, pSuccessA;
	double plongSuccB, pmedSuccB, pshortSuccB, pSuccessB, pSuccess;
	double rSI, rIS, rIR, rRS, rSwitch, totalRate;
	double eventrnd;

	double peakI, lastI, Qn, Qhh, peakIQn, peakIQhh, totalS;
	double tmp, reg;

	FILE *LandscapePointer;
	FILE *DataPointer;

	if((LandscapePointer = fopen(landFname, "r")) == NULL){
		fprintf(stderr, "Unable to open file \"%s\" for reading (Error %d: %s).\n", landFname, errno, strerror(errno));
		return;
	}
	if (strcmp(logFname, "-")){
		if((DataPointer = fopen(logFname, "a")) == NULL){
			fprintf(stderr, "Unable to open file \"%s\" for writing (Error %d: %s).\n", logFname, errno, strerror(errno));
			return;
		}
	}
	else
		DataPointer = stdout;

	/* initialize some variables */
	lastTimeRecorded = 0.0;
	currentTime = lastTime = 0.0;
	peakTime = 0.0;
	startregress = 0;
	rIS = 0.0;
	rIR = 0.0;
	rRS = 0.0;
	rSwitch = 0.0;
	doneSetup = 0;
	currentS = 0.0;
	currentI = 0.0;
	currentI_A = currentI_B = lastI = 0.0;
	currentR = 0.0;
	SumSIn_A = SumSIn_B = 0.0;
	SumSIhhSums_A = SumSIhhSums_B = 0.0;
	peakI = 0.0;
	peakIQn = 0.0;
	peakIQhh = 0.0;
	peakInumIn = 0;
	reg = 0.0;
	lastSlope = currentSlope = 0.0;
	rapidGrowth = 0;
	slopeDelta = 0.5;
	SumIIhh_A = SumIIn_A = SumIIhh_B = SumIIn_B = 0.0;
	SumIRhhSums_A = SumIRhhSums_B = 0.0;
	SumIRn_A = SumIRn_B = 0.0;
	negSlopeCnt = 0;

	/* malloc one-dimensional arrays */
	In_A = (double*) malloc(NUM_N*sizeof(double));
	SIhhSums_A = (double*) malloc(NUM_N*sizeof(double));
	SIn_A = (double*) malloc(NUM_N*sizeof(double));
	IRhhSums_A = (double*) malloc(NUM_N*sizeof(double));
	IRn_A = (double*) malloc(NUM_N*sizeof(double));
	In_B = (double*) malloc(NUM_N*sizeof(double));
	SIhhSums_B = (double*) malloc(NUM_N*sizeof(double));
	SIn_B = (double*) malloc(NUM_N*sizeof(double));
	IRhhSums_B = (double*) malloc(NUM_N*sizeof(double));
	IRn_B = (double*) malloc(NUM_N*sizeof(double));

	Sn = (double*) malloc(NUM_N*sizeof(double));
	Rn = (double*) malloc(NUM_N*sizeof(double));

	/* malloc first dimension of two-dimensional arrays */
	Ihh_A = (double**) malloc(NUM_N*sizeof(double*));
	SIhh_A = (double**) malloc(NUM_N*sizeof(double*));
	IRhh_A = (double**) malloc(NUM_N*sizeof(double*));
	Ihh_B = (double**) malloc(NUM_N*sizeof(double*));
	SIhh_B = (double**) malloc(NUM_N*sizeof(double*));
	IRhh_B = (double**) malloc(NUM_N*sizeof(double*));

	Shh = (double**) malloc(NUM_N*sizeof(double*));
	Rhh = (double**) malloc(NUM_N*sizeof(double*));

	/* initialize arrays and read in household susceptible data to Shh */
	for (i = 0; i < NUM_N; i++) {
		In_A[i] = In_B[i] = 0.0;
		SIhhSums_A[i] = SIhhSums_B[i] = 0.0;
		IRhhSums_A[i] = IRhhSums_B[i] = 0.0;
		Rn[i] = Sn[i] = 0.0;
		SIn_A[i] = IRn_A[i] = SIn_B[i] = IRn_B[i] = 0.0;

		Ihh_A[i] = (double*) malloc(HH_PER_N*sizeof(double));
		SIhh_A[i] = (double*) malloc(HH_PER_N*sizeof(double));
		IRhh_A[i] = (double*) malloc(HH_PER_N*sizeof(double));
		Ihh_B[i] = (double*) malloc(HH_PER_N*sizeof(double));
		SIhh_B[i] = (double*) malloc(HH_PER_N*sizeof(double));
		IRhh_B[i] = (double*) malloc(HH_PER_N*sizeof(double));

		Rhh[i] = (double*) malloc(HH_PER_N*sizeof(double));
		Shh[i] = (double*) malloc(HH_PER_N*sizeof(double));

		for (j = 0; j < HH_PER_N; j++) {
			fscanf(LandscapePointer, "%lf", &Shh[i][j]);
			Ihh_A[i][j] = Ihh_B[i][j] = 0.0;
			SIhh_A[i][j] = SIhh_B[i][j] = 0.0;
			IRhh_A[i][j] = IRhh_B[i][j] = 0.0;
			Rhh[i][j] = 0.0;
			Sn[i] += Shh[i][j];
			currentS += Shh[i][j];
		}
	}
	fclose(LandscapePointer);

	/* take note of total possible number of susceptibles */
	totalS = currentS;

	/* Infect the first location (start with type A) */
	nind = randvalRescaleSum(Sn, NUM_N, currentS);
	hind = randvalRescaleSum(Shh[nind], HH_PER_N, Sn[nind]);

	/* These changes can all be easily derived algebraically (AB - (A+1)(B-1) = B - A - 1) */

	/* for S*I */
	tmp = Shh[nind][hind] - Ihh_A[nind][hind] - 1.0;
	SIhh_A[nind][hind] += tmp;
	SIhhSums_A[nind] += tmp;
	SumSIhhSums_A += tmp;

	tmp = Sn[nind] - In_A[nind] - 1.0;
	SIn_A[nind] += tmp;
	SumSIn_A += tmp;

	/* the rest (easy) */
	currentI_A++;
	currentI++;
	numIn = 1;
	currentS--;
	In_A[nind]++;
	Sn[nind]--;
	Ihh_A[nind][hind]++;
	Shh[nind][hind]--;
	SumIIhh_A += 2*Ihh_A[nind][hind] + 1;
	SumIIn_A += 2*In_A[nind] + 1;

	/* for I*(MAX_POP-S) */
	tmp = HH_MAX_POP - Shh[nind][hind];
	IRhh_A[nind][hind] += tmp;
	IRhhSums_A[nind] += tmp;
	SumIRhhSums_A += tmp;

	tmp = N_MAX_POP - Sn[nind];
	IRn_A[nind] += tmp;
	SumIRn_A += tmp;

	/* Begin running the simulation. */
	eventCount = 1.0;
}	

double ** next(){
	if (((simType==SIR)?1:(currentTime < MAXTIME)) && (currentI > 0) && !(startregress && (eqtest->numentries == 50)
				&& ((eqtest->max - eqtest->min) < 3000) && (eqtest->numinc > 20) && (eqtest->numdec > 20)
				&& (fabs(fullRegression(eqtest)) < .001))) {

		if (!doneSetup && (currentI_A >= NUMI0)) { /* If the initial infections have been seeded */
			doneSetup = 1;
		}

		/* Start regression check for equilibrium if there are > 1000 infected individuals */
		if (!startregress && (simType != SIR) && (currentI > 1000))
			startregress = 1;

		/* each infected location (currentI) has a (currentS/total) chance of finding a
		   susceptible location when using long-distance dispersal
		   So the following is the total rate of successful long-dist infections. */
		plongSuccA = PLONG_A * currentI_A * currentS/TOTAL;
		plongSuccB = PLONG_B * currentI_B * currentS/TOTAL;

		/* each neighborhood has rate PMED*SIn/NMAX of successful medium-distance infections.
		   The total rate of any medium distance event happening is
		   the sum of each neighborhood's rate of medium-distance success. */
		pmedSuccA = PMED_A * SumSIn_A / (double)N_MAX_POP;
		pmedSuccB = PMED_B * SumSIn_B / (double)N_MAX_POP;

		/* the rate of any given single household having a successful
		   short-distance dispersal is PSHORT * Shh * Ihh/n1. SumSIhhSums represents the sum of
		   every household's SI product, so the overall rate is PSHORT*SumSIhhSums_A/n1. */
		pshortSuccA = PSHORT_A * SumSIhhSums_A/(double)HH_MAX_POP;
		pshortSuccB = PSHORT_B * SumSIhhSums_B/(double)HH_MAX_POP;

		/* Total rate of successful infections */
		pSuccessA = plongSuccA + pmedSuccA + pshortSuccA;
		pSuccessB = plongSuccB + pmedSuccB + pshortSuccB;
		pSuccess = pSuccessA + pSuccessB;

		rSI = PHI * pSuccess;
		if (simType == SIS) {
			rIS = MU * currentI;
		}
		else if (simType == SIRS) {
			rIR = MU * currentI;
			rRS = GAMMA * currentR;
		}
		else {
			rIR = MU * currentI;
		}

		rSwitch = PSWITCH * PHI * PSHORT_A * SumIRhhSums_A/HH_MAX_POP; //(currentI_A*(1-(currentS/TOTAL)));

		totalRate = rSI + rIR + rIS + rRS + rSwitch;

		//printf("%f = %f + %f + %f + %f + %f\n", totalRate, rSI, rIR, rIS, rRS, rSwitch);

		/* only update time if the initial infections have already been seeded */
		if (doneSetup)
			currentTime += randExp(totalRate);

		//printf("time: %f; currentI: %f\n", currentTime, currentI);

		/* Now that the time of the next event is known, it's possible to update the data
		   file with the infection total for every TIMEINTERVAL since the last update. */

		if (((simType==SIR)?1:(lastTimeRecorded < MAXTIME)) && ((currentTime - lastTimeRecorded) > TIMEINTERVAL) && doneSetup) {
			lastTimeRecorded += TIMEINTERVAL;
			addEqData(eqtest, currentI);
}
		/* choose the type of event (always an infection if still setting up initial infected pop.) */
		eventrnd = doneSetup? rand01(): 0.0;

		if (eventrnd < (rSI/totalRate)) {

			eventType = infection;

			double typernd = rand01();
			/* Determine whether this is a type A or B infection */
			if(typernd < currentI_A/currentI){
				//printf("type A infection\n");
				doInfection(plongSuccA, pmedSuccA, pSuccess, Sn, &SumSIn_A, SIn_A,
						&SumSIhhSums_A, SIhhSums_A, SIhh_A, &SumIIhh_A, &SumIIn_A, &SumIRn_A);
			}
			else{
				//printf("type B infection\n");
				doInfection(plongSuccB, pmedSuccB, pSuccess, Sn, &SumSIn_B, SIn_B,
						&SumSIhhSums_B, SIhhSums_B, SIhh_B, &SumIIhh_B, &SumIIn_B, &SumIRn_B);
			}

			/* If there is an IS or IR recovery */
		} else if (eventrnd < (rSI+rIS+rIR)/totalRate) {

			if(simType == SIS)
				eventType = susceptible;
			else
				eventType = recovery;

			/* Determine whether it will be a type A or B recovery */
			if (rand01() < currentI_A/currentI){
				/* type A */
				doRecovery(In_A, &currentI_A, Ihh_A, SIhh_A,
						SIhhSums_A, &SumSIhhSums_A, Sn, SIn_A, &SumSIn_A, &SumIIhh_A,
						&SumIIn_A, IRhh_A, IRhhSums_A, &SumIRhhSums_A, IRn_A, &SumIRn_A,
						Ihh_B, IRhh_B, IRhhSums_B, &SumIRhhSums_B, SIhh_B, SIhhSums_B,
						&SumSIhhSums_B);
			}
			else{
				/* type B */
				doRecovery(In_B, &currentI_B, Ihh_B, SIhh_B,
						SIhhSums_B, &SumSIhhSums_B, Sn, SIn_B, &SumSIn_B, &SumIIhh_B,
						&SumIIn_B, IRhh_B, IRhhSums_B, &SumIRhhSums_B, IRn_B, &SumIRn_B,
						Ihh_A, IRhh_A, IRhhSums_A, &SumIRhhSums_A, SIhh_A, SIhhSums_A,
						&SumSIhhSums_A);
			}

			currentI--;
			if (simType == SIS) {
				currentS++;
				Sn[nind]++;
				Shh[nind][hind]++;
			} else {
				currentR++;
				Rn[nind]++;
				Rhh[nind][hind]++;
			}

			/* neighborhood no longer infected; decrease count */
			if ((In_A[nind] == 0.0) && (In_B[nind] == 0.0))
				numIn--;
		}
		/* if there's an RS loss of resistance */
		else if ((simType == SIRS) && (eventrnd < (rSI+rIS+rIR+rRS)/totalRate)) {

			eventType = susceptible;

			nind = randvalRescaleSum(Rn, NUM_N, currentR);
			hind = randvalRescaleSum(Rhh[nind], HH_PER_N, Rn[nind]);

			/* adjust type A S*I structures */
			tmp = Ihh_A[nind][hind];
			SIhh_A[nind][hind] += tmp;
			SIhhSums_A[nind] += tmp;
			SumSIhhSums_A += tmp;

			tmp = In_A[nind];
			SIn_A[nind] += tmp;
			SumSIn_A += tmp;

			/* adjust type B S*I structures */
			tmp = Ihh_B[nind][hind];
			SIhh_B[nind][hind] += tmp;
			SIhhSums_B[nind] += tmp;
			SumSIhhSums_B += tmp;

			tmp = In_B[nind];
			SIn_B[nind] += tmp;
			SumSIn_B += tmp;

			/* adjust type A I*(MAX_NUM-S)*/
			tmp = -Ihh_A[nind][hind];
			IRhh_A[nind][hind] += tmp;
			IRhhSums_A[nind] += tmp;
			SumIRhhSums_A += tmp;

			tmp = -In_A[nind];
			IRn_A[nind] += tmp;
			SumIRn_A += tmp;

			/* adjust type B I*(MAX_NUM-S)*/
			tmp = -Ihh_B[nind][hind];
			IRhh_B[nind][hind] += tmp;
			IRhhSums_B[nind] += tmp;
			SumIRhhSums_B += tmp;

			tmp = -In_B[nind];
			IRn_B[nind] += tmp;
			SumIRn_B += tmp;

			/* other stuff */
			currentR--;
			Rn[nind]--;
			Rhh[nind][hind]--;
			currentS++;
			Sn[nind]++;
			Shh[nind][hind]++;

		}
		/* else there is a switch from type A to type B */
		else{

			eventType = typeSwitch;


			//TODO check to make sure this is what you want?
			nind = randvalRescaleSum(IRn_A, NUM_N, SumIRn_A);
			hind = randvalRescaleSum(IRhh_A[nind], HH_PER_N, IRhhSums_A[nind]);

			/* adjust type A S*I structures */
			tmp = -Shh[nind][hind];
			SIhh_A[nind][hind] += tmp;
			SIhhSums_A[nind] += tmp;
			SumSIhhSums_A += tmp;

			tmp = -Sn[nind];
			SIn_A[nind] += tmp;
			SumSIn_A += tmp;

			/* adjust type B S*I structures */
			tmp = Shh[nind][hind];
			SIhh_B[nind][hind] += tmp;
			SIhhSums_B[nind] += tmp;
			SumSIhhSums_B += tmp;

			tmp = Sn[nind];
			SIn_B[nind] += tmp;
			SumSIn_B += tmp;

			/* adjust type A I*(MAX_NUM-S)*/
			tmp = Shh[nind][hind] - HH_MAX_POP;
			IRhh_A[nind][hind] += tmp;
			IRhhSums_A[nind] += tmp;
			SumIRhhSums_A += tmp;

			tmp = Sn[nind] - N_MAX_POP;
			IRn_A[nind] += tmp;
			SumIRn_A += tmp;

			/* adjust type B I*(MAX_NUM-S)*/
			tmp = HH_MAX_POP - Shh[nind][hind];
			IRhh_B[nind][hind] += tmp;
			IRhhSums_B[nind] += tmp;
			SumIRhhSums_B += tmp;

			tmp = N_MAX_POP - Sn[nind];
			IRn_B[nind] += tmp;
			SumIRn_B += tmp;

			/* other stuff */
			currentI_A--;
			currentI_B++;

			SumIIhh_A -= 2*Ihh_A[nind][hind] - 1.0;
			SumIIn_A -= 2*In_A[nind] - 1.0;
			In_A[nind]--;
			Ihh_A[nind][hind]--;

			SumIIhh_B += 2*Ihh_B[nind][hind] + 1.0;
			SumIIn_B += 2*In_B[nind] + 1.0;

			In_B[nind]++;
			Ihh_B[nind][hind]++;
		}

}

void freeAll(){
	/* clean up; close/free everything */
//	if (DataPointer != stdout)
//		fclose(DataPointer);
//	release(eqtest);
	free(eqarray);
	free(IGRData);
	free(IGRtData);

	free(In_A);
	free(SIhhSums_A);
	free(SIn_A);
	free(IRn_A);
	free(IRhhSums_A);
	free(In_B);
	free(SIhhSums_B);
	free(SIn_B);
	free(IRn_B);
	free(IRhhSums_B);
	free(Rn);
	free(Sn);

	for(i = 0; i < HH_PER_N; i++){
		free(Ihh_A[i]);
		free(SIhh_A[i]);
		free(IRhh_A[i]);
		free(Ihh_B[i]);
		free(SIhh_B[i]);
		free(IRhh_B[i]);
		free(Shh[i]);
		free(Rhh[i]);
	}
	free(Ihh_A);
	free(SIhh_A);
	free(IRhh_A);
	free(Ihh_B);
	free(SIhh_B);
	free(IRhh_B);
	free(Shh);
	free(Rhh);

}

void doInfection(double plongSucc, double pmedSucc, double pSuccess, double *Sn,
		double *SumSIn, double *SIn, double *SumSIhhSums, double *SIhhSums,
		double **SIhh, double *SumIIhh, double *SumIIn, double *SumIRn){
	double distrnd;

	/* choose distance over which the infection occurs */
	distrnd = rand01();

	/*=== Figure out the location to be infected ===*/
	/* the event is a long distance dispersal */
	if (distrnd < plongSucc/pSuccess) {
		nind = randvalRescaleSum(Sn, NUM_N, currentS);
		hind = randvalRescaleSum(Shh[nind], HH_PER_N, Sn[nind]);

		/* the event is a medium-distance dispersal */
	} else if (distrnd < (plongSucc+pmedSucc)/pSuccess) {
		nind = randvalRescaleSum(SIn, NUM_N, *SumSIn);
		hind = randvalRescaleSum(Shh[nind], HH_PER_N, Sn[nind]);

		/* the event is a short-distance dispersal */
	} else {
		nind = randvalRescaleSum(SIhhSums, NUM_N, *SumSIhhSums);
		hind = randvalRescaleSum(SIhh[nind], HH_PER_N, SIhhSums[nind]);
	}

	/* Update the state arrays */

	/* for S*I */
	tmp = Shh[nind][hind] - Ihh_A[nind][hind] - 1.0;
	SIhh_A[nind][hind] += tmp;
	SIhhSums_A[nind] += tmp;
	SumSIhhSums_A += tmp;

	tmp = Sn[nind] - In_A[nind] - 1.0;
	SIn_A[nind] += tmp;
	SumSIn_A += tmp;

	/* for I*(MAX_POP-S) */
	tmp = Ihh_A[nind][hind] - Shh[nind][hind] + HH_MAX_POP + 1.0;
	IRhh_A[nind][hind] += tmp;
	IRhhSums_A[nind] += tmp;
	SumIRhhSums_A += tmp;

	tmp = In_A[nind] - Sn[nind] + N_MAX_POP + 1.0;
	IRn_A[nind] += tmp;
	SumIRn_A += tmp;

	/* deal with effects on type B arrays */
	tmp = Ihh_B[nind][hind];
	IRhh_B[nind][hind] += tmp;
	IRhhSums_B[nind] += tmp;
	SumIRhhSums_B += tmp;

	tmp = -Ihh_B[nind][hind];
	SIhh_B[nind][hind] += tmp;
	SIhhSums_B[nind] += tmp;
	SumSIhhSums_B += tmp;

	SumIIhh_A += 2*Ihh_A[nind][hind] + 1.0;
	SumIIn_A += 2*In_A[nind] + 1.0;

	/* newly infected neighborhood; increase count */
	if (In_A[nind] == 0.0)
		numIn++;

	currentI++;
	currentI_A++;
	currentS--;
	In_A[nind]++;
	Sn[nind]--;
	Ihh_A[nind][hind]++;
	Shh[nind][hind]--;
}

void doRecovery(double *In, double *currentI, double **Ihh, double **SIhh,
		double *SIhhSums, double *SumSIhhSums, double *Sn, double *SIn, double *SumSIn,
		double *SumIIhh, double *SumIIn, double **IRhh, double *IRhhSums, double *SumIRhhSums,
		double *IRn, double *SumIRn, double **Ihh_other, double **IRhh_other,
		double *IRhhSums_other, double *SumIRhhSums_other, double **SIhh_other,
		double *SIhhSums_other, double *SumSIhhSums_other){

	nind = randvalRescaleSum(In, NUM_N, *currentI);
	hind = randvalRescaleSum(Ihh[nind], HH_PER_N, In[nind]);

	/* for S*I */
	if (simType == SIS) {
		tmp = Ihh[nind][hind] - Shh[nind][hind] - 1.0;
	} else {
		tmp = -Shh[nind][hind];
	}
	SIhh[nind][hind] += tmp;
	SIhhSums[nind] += tmp;
	*SumSIhhSums += tmp;

	if (simType == SIS) {
		tmp = In[nind] - Sn[nind] - 1.0;
	} else {
		tmp = -Sn[nind];
	}
	SIn[nind] += tmp;
	*SumSIn += tmp;

	/* for I*(MAX_POP-S) */
	if (simType == SIS) {
		tmp = -Ihh[nind][hind] + Shh[nind][hind] - HH_MAX_POP + 1.0;
	} else {
		tmp = Shh[nind][hind] - HH_MAX_POP;
	}
	IRhh[nind][hind] += tmp;
	IRhhSums[nind] += tmp;
	*SumIRhhSums += tmp;

	if (simType == SIS) {
		tmp = -In[nind] + Sn[nind] - N_MAX_POP + 1.0;
	} else {
		tmp = Sn[nind] - N_MAX_POP;
	}
	IRn[nind] += tmp;
	*SumIRn += tmp;

	/* deal with effects on arrays of other type */
	if(simType == SIS){
		tmp = -Ihh_other[nind][hind];
		IRhh_other[nind][hind] += tmp;
		IRhhSums_other[nind] += tmp;
		*SumIRhhSums_other += tmp;

		tmp = Ihh_other[nind][hind];
		SIhh_other[nind][hind] += tmp;
		SIhhSums_other[nind] += tmp;
		*SumSIhhSums_other += tmp;
	}

	/* other stuff */
	(*currentI)--;
	*SumIIhh -= 2*Ihh[nind][hind] - 1.0;
	*SumIIn -= 2*In[nind] - 1.0;
	In[nind]--;
	Ihh[nind][hind]--;
}

char* getEvent(EventType t){
	switch(t){
		case infection:
			return "infection";
			break;
		case recovery:
			return "recovery";
			break;
		case typeSwitch:
			return "typeSwitch";
			break;
		case susceptible:
			return "susceptible";
			break;
	}
	return "undefined?!";
}
