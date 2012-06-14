#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
//#include "mpi.h"
#include "worm_simulation.h"

void printusage (char *s) {
	fprintf(stderr, "Usage: %s parameterFile [-nparams x] [-v] [-r randomSeed]\n", s);
	fprintf(stderr, "	parameterFile: text file with each line listing a set of simulation parameters\n");
	fprintf(stderr, "	-nparams x: number of parameter sets from the file to run (all if not specified)\n");
	fprintf(stderr, "	-v: print verbose information about each simulation (to stdout)\n");
	fprintf(stderr, "	-r randomSeed: seed the random number generator with (unsigned int) randomSeed\n");
	exit(2);
}

int main (int argc, char **argv) {

	unsigned int randomSeed;
	int i, NUMLINES, seeded, oneline, rank, numProc, size, numI0, nextLine, currentLine;
	double minTime, maxTime, mu, phi, gamma, pShort_A, pMed_A, pLong_A, pShort_B, pMed_B, pLong_B, pSwitch, timeInterval;
	char *logFname, buff[1024], typeStr[256], landFname[256];
	SimType simType;

	/* get name of parameter file from command line (must be first argument) */
	FILE *fr;
	if(argc <= 1)
		printusage(argv[0]);
	if((fr = fopen(argv[1], "r")) == NULL){
		fprintf(stderr, "Unable to open parameter file \"%s\" for reading (Error %d).\n", argv[1], errno);
		printusage(argv[0]);
	}

	/* default values */
	NUMLINES = 0;
	logFname = "-";
	seeded = 0;
	oneline = 1;
	timeInterval = 0.05;

	/* parse command line input */
	i = 2;

	while(i < argc){
		if(!strcmp(argv[i], "-nparams")){
			if (i >= argc-1)
				printusage(argv[0]);
			if(sscanf(argv[i+1], "%d", &NUMLINES) != 1)
				printusage(argv[0]);
			i += 2;
		}
		else if(!strcmp(argv[i], "-v")){
			//if (i < argc-1){
			//	strncpy(logFname, argv[i+1], 255);
			//	logFname[255] = '\0';
			//}
			oneline = 0;
			i++;
		}
		else if(!strcmp(argv[i], "-r")){
			if (i >= argc-1)
				printusage(argv[0]);
			if(sscanf(argv[i+1], "%u", &randomSeed) != 1)
				printusage(argv[0]);
			seeded = 1;
			i += 2;
		}
		else{
			printusage(argv[0]);
		}
	}

//	MPI_Init(&argc, &argv);
//	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
//	MPI_Comm_size(MPI_COMM_WORLD, &numProc);

	/* get number of lines in file if not given at command line (NUMLINES = 0) */
	if(NUMLINES == 0){
		while(fscanf(fr, "%*[^\n]%*c") != EOF)
			NUMLINES++;
		fseek(fr, 0, SEEK_SET);
	}

	nextLine = rank + 1;
	currentLine = 0;
	while (nextLine <= NUMLINES) {
		while (currentLine < nextLine) {
			fgets(buff, sizeof(buff), fr);
			currentLine++;
		}
		sscanf(buff, "simType: %s size: %d landFname: %s minTime: %lf maxTime: %lf numI0: %d mu: %lf phi: %lf gamma: %lf pLong_A: %lf pMed_A: %lf pShort_A: %lf pLong_B: %lf pMed_B: %lf pShort_B: %lf pSwitch: %lf", typeStr, &size, landFname, &minTime, &maxTime, &numI0, &mu, &phi, &gamma, &pLong_A, &pMed_A, &pShort_A, &pLong_B, &pMed_B, &pShort_B, &pSwitch);

		/* convert simulation type string into SimType enum */
		if(strcmp(typeStr, "SIR") == 0)
			simType = SIR;
		else if(strcmp(typeStr, "SIRS") == 0)
			simType = SIRS;
		else if(strcmp(typeStr, "SIS") == 0)
			simType = SIS;
		else{
			fprintf(stderr, "Invalid simulation type: %s", typeStr);
			printusage(argv[0]);
		}

		worm_simulation(size, numI0, mu, phi, gamma, minTime, maxTime, timeInterval, pLong_A, pMed_A, pShort_A, pLong_B, pMed_B, pShort_B, pSwitch, landFname, logFname, simType, oneline, seeded, randomSeed, rank);
		nextLine += numProc;
	}
	fclose(fr);
//	MPI_Finalize();
	return 0;
}
