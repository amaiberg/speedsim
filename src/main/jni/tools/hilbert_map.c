/*
 * hilbert_map.c
 *
 * Adapted from: http://en.wikipedia.org/wiki/Hilbert_curve
 *
 *  Created on: Feb 16, 2012
 *      Author: emma
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "hilbert_map.h"

int main(int argc, char **argv){
	if(argc != 2){
		fprintf(stderr, "One argument: dimension of array (must be a power of 2).\n");
		exit(1);
	}

	int i, j, x, y, size;
	size = atoi(argv[1]);

//	for(i = 0; i < size; i++){
//		for(j = 0; j < size; j++){
//			printf("%3d ", xy_to_hilbert_dist(size,i,j));
//		}
//		printf("\n");
//	}

	//for(i = 0; i < size*size; i++){
	//	hilbert_dist_to_xy(size, i, &x, &y);
	//	printf("(%d, %d)\n", x, y);
	//}

	return 0;
}

int xy_to_hilbert_dist(int n, int x, int y){
    int rx, ry, s, d=0;
    for (s=n/2; s>0; s/=2) {
        rx = (x & s) > 0;
        ry = (y & s) > 0;
        d += s * s * ((3 * rx) ^ ry);
        rotate(s, &x, &y, rx, ry);
    }
    return d;
}

void hilbert_dist_to_xy(int n, int d, int *x, int *y){
    int rx, ry, s, t=d;
    *x = *y = 0;
    for (s=1; s<n; s*=2) {
        rx = 1 & (t/2);
        ry = 1 & (t ^ rx);
        rotate(s, x, y, rx, ry);
        *x += s * rx;
        *y += s * ry;
        t /= 4;
    }
}

void rotate(int n, int *x, int *y, int rx, int ry){
    int t;
    if (ry == 0) {
        if (rx == 1) {
            *x = n-1 - *x;
            *y = n-1 - *y;
        }
        t  = *x;
        *x = *y;
        *y = t;
    }
}

/* if you're passing, e.g., a 256 x 256 matrix, n=16 */
double **hilbert_embed(double **old_matrix, double **new_matrix, int n){
	int i, j, nx, ny, hx, hy, size;
	size = n*n;
	for(i = 0; i < size; i++){
		hilbert_dist_to_xy(n, i, &nx, &ny);
		for(j = 0; j < size; j++){
			hilbert_dist_to_xy(n, j, &hx, &hy);
			new_matrix[n*nx+hx][n*ny+hy] = old_matrix[i][j];
		}
	}
	return new_matrix;
}
