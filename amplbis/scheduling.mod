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

#TODO read dataset
#Scusate che non ho fatto piu' ancora :(
#table jobs IN "ODBC" "instances.xlsx":
#  jobs <- [jobs], processing t, release t;

## ----------------------------------------------
## PARAMETERS
#param n > 0 integer;            # number of jobs
#param minp > 0 integer;         # minimal processing times
#param maxp > 0 integer;         # maximal processing times
param r {jobs} >= 0 integer;    # release dates
param p {jobs} >= 0 integer;    # processing times
param M >= 0 integer;

## ----------------------------------------------
## VARIABLES
var x {jobs} >= 0 integer;     			# starting times
var C {jobs} >= 0 integer;     			# completion times
var y {i in jobs, j in jobs} binary;	# precedence decision vars

## ----------------------------------------------
## OBJECTIVE FUNCTION
minimize completionTime : sum {j in jobs} C[j];

## ----------------------------------------------
## CONSTRAINTS
subject to wait_for_release {j in jobs}:
    x[j] >= r[j];
subject to completion_after_processing {j in jobs}:
    C[j] = x[j] + p[j];
subject to disjunction_first {i in jobs, j in jobs: i != j}:
	M * (1 - y[i,j]) + x[j] >= C[i];
subject to disjunction_second {i in jobs, j in jobs: i != j}:
	M * y[i,j] + x[i] >= C[j];
