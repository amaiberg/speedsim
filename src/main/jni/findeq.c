/*
 * File: findeq.c
 *   by: Andrew Audibert
 *       aaudibert10@gmail.com
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include "findeq.h"

eqdata *initEqData(double *array, int maxentries, double refreshrate) {

	eqdata *eqPtr;
	if ((eqPtr = (eqdata *)malloc(sizeof(eqdata)))==NULL) {
		fprintf(stderr, "Malloc Error %d\n", errno);
		return (eqdata *)NULL;
	}

	eqPtr->array = array;
	eqPtr->numentries = 0;
	eqPtr->maxentries = maxentries;
	eqPtr->nextInd = 0;
	eqPtr->refreshrate = refreshrate;
	eqPtr->sincerefresh = refreshrate;
	eqPtr->numinc = 0;
	eqPtr->numdec = 0;
	return eqPtr;
}

void addEqData(eqdata *eqPtr, double data) {

	double oldnum, prevnum;
	double sum;
	int i;
	if (eqPtr->numentries == 0) {
		eqPtr->min = data;
		eqPtr->max = data;
		eqPtr->avg = data;
		eqPtr->numentries++;
		eqPtr->array[0] = data;
		eqPtr->nextInd++;
	}
	else {
		prevnum = eqPtr->array[(eqPtr->nextInd+eqPtr->maxentries-1) % eqPtr->maxentries];
		if (data < eqPtr->min)
			eqPtr->min = data;
		if (data > eqPtr->max)
			eqPtr->max = data;
		if (data > prevnum)
			eqPtr->numinc++;
		if (data < prevnum)
			eqPtr->numdec++;

		if (eqPtr->numentries < eqPtr->maxentries) {
			eqPtr->array[eqPtr->nextInd] = data;
			eqPtr->numentries++;
			eqPtr->nextInd = (eqPtr->nextInd + 1) % eqPtr->maxentries;
			eqPtr->avg += (data-eqPtr->avg)/(eqPtr->numentries);
		} else {
			oldnum = eqPtr->array[eqPtr->nextInd];
			eqPtr->array[eqPtr->nextInd] = data;
			eqPtr->avg += (data-oldnum)/(eqPtr->maxentries);
			eqPtr->nextInd = (eqPtr->nextInd + 1) % eqPtr->maxentries;
			eqPtr->sincerefresh++;
			if (eqPtr->array[eqPtr->nextInd] > oldnum)
				eqPtr->numinc--;
			if (eqPtr->array[eqPtr->nextInd] < oldnum)
				eqPtr->numdec--;
			if (oldnum == eqPtr->max) {
				eqPtr->max = eqPtr->array[0];
				for (i = 1; i < eqPtr->maxentries; i++) {
					if (eqPtr->array[i] > eqPtr->max)
						eqPtr->max = eqPtr->array[i];
				}
			}
			if (oldnum == eqPtr->min) {
				eqPtr->min = eqPtr->array[0];
				for (i = 1; i < eqPtr->maxentries; i++) {
					if (eqPtr->array[i] < eqPtr->min)
						eqPtr->min = eqPtr->array[i];
				}
			}
			if (eqPtr->sincerefresh >= eqPtr->refreshrate) {
				eqPtr->sincerefresh = 0;
				sum = 0;
				for (i = 0; i < eqPtr->maxentries; i++) {
					sum += eqPtr->array[i];
				}
				eqPtr->avg = sum/eqPtr->maxentries;
			}
		}
	}
}


double fullRegression (eqdata *eqPtr) {

	int i;
	double x, y, sumx, sumy, sumxy, sumxx;

	sumx = sumy =  sumxy = sumxx = 0.0;

	int num = eqPtr->numentries;

	x = 0;
	for (i = eqPtr->nextInd; i < num; i++) {
		y = eqPtr->array[i];
		sumx += x;
		sumy += y;
		sumxy += x * y;
		sumxx += x * x;
		x+=1000;
	}
	for (i = 0; i < eqPtr->nextInd; i++) {
		y = eqPtr->array[i];
		sumx += x;
		sumy += y;
		sumxy += x * y;
		sumxx += x * x;
		x+=1000;
	}
	return ((double)num*sumxy - sumx*sumy)/((double)num*sumxx - sumx*sumx);
}

void release(eqdata *eqPtr) {
	free(eqPtr);
	eqPtr = NULL;
}

