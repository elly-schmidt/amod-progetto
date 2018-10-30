################  SCHEDULING  ###################
# on a single machine                           #
# with release dates                            #
# to minimize the sum of completion times       #
#                                               #
# --------------------------------------------- #
# AMOD project 				                    #
# Carmine Scarpitta,                            #
# Davide Romano Tranzocchi,                     #
# Elly Schmidt                                  #
#################################################

## ----------------------------------------------
## SETS
set jobs; #:= 1..n;

## ----------------------------------------------
## PARAMETERS
param n > 0 integer;            # number of jobs
param min_p > 0 integer;        # minimal processing times
param max_p > 0 integer;        # maximal processing times
param r {jobs} >= 0 integer;    # release dates
param p {jobs} >= 0 integer;    # processing times


## ----------------------------------------------
## VARIABLES
var x {jobs} >= 0 integer;     # starting times
var C {jobs} >= 0 integer;     # completion times

## ----------------------------------------------
## OBJECTIVE FUNCTION
minimize completionTime : sum {j in jobs} C[j];

## ----------------------------------------------
## CONSTRAINTS
subject to wait_for_release {j in jobs}:
    x[j] >= r[j];
subject to completion_after_processing {j in jobs}:
    C[j] = x[j] + p[j];
