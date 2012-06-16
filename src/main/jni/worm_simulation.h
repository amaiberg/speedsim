/*
 * worm_simulation.h
 *
 *  Created on: Mar 5, 2012
 *      Author: emma
 */

#ifndef WORM_SIMULATION_H_
#define WORM_SIMULATION_H_

typedef enum SimType{SIR, SIRS, SIS} SimType;

typedef enum EventType{infection, recovery, typeSwitch, susceptible} EventType; /* for debugging */

void init(int size, int NUMI0, double MU, double PHI, double GAMMA, double MINTIME,
		double MAXTIME, double TIMEINTERVAL, double PLONG_A, double PMED_A, double PSHORT_A,
		double PLONG_B, double PMED_B, double PSHORT_B, double PSWITCH,
		char *landFname, char *logFname, SimType SIMTYPE, int ONELINE, int seeded,
		unsigned int randomSeed, int rank,FILE * LandscapePointer,long off, long len);

double ** next();

void doInfection(double plongSucc, double pmedSucc, double pSuccess, double *Sn,
		double *SumSIn, double *SIn, double *SumSIhhSums, double *SIhhSums,
		double **SIhh, double *SumIIhh, double *SumIIn, double *SumIRn);

void doRecovery(double *In, double *currentI, double **Ihh, double **SIhh,
		double *SIhhSums, double *SumSIhhSums, double *Sn, double *SIn, double *SumSIn,
		double *SumIIhh, double *SumIIn, double **IRhh, double *IRhhSums, double *SumIRhhSums,
		double *IRn, double *SumIRn, double **Ihh_other, double **IRhh_other,
		double *IRhhSums_other, double *SumIRhhSums_other, double **SIhh_other,
		double *SIhhSums_other, double *SumSIhhSums_other);

char* getEvent(EventType t);

#endif /* WORM_SIMULATION_H_ */

