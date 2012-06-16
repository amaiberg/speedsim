/*
 * File: findeq.h
 *   By: Andrew Audibert
 *       aaudibert10@gmail.com
 *       June 2010
 *
 * Header-file for eqilibrium-finding code, containing data structures.
 */
#ifndef FINDEQ_H
#define FINDEQ_H

typedef struct {
  double *array, avg;
  int numentries, maxentries, nextInd, refreshrate, sincerefresh;
  unsigned int min, max, numinc, numdec;
} eqdata;

eqdata *initEqData(double *array, int maxentries, double refreshrate);

void addEqData(eqdata *eqPtr, double data);

double fullRegression (eqdata *eqPtr);

void release (eqdata *eqPtr);

#endif
