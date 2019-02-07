package branch_and_bound;

import java.util.*;

import static branch_and_bound.Constants.INFINITY;

/**
 * This class represents a node in the enumeration tree
 */
public class TreeNode {
    /**
     * The reference to the instance to be solved
     */
    private static Instance instance;

    /**
     * The partial solution associated to the tree node
     * It corresponds to the path from the tree root to the tree node
     */
    private Solution partialSolution;

    /**
     * The lower bound associated to the node
     */
    private int lowerBound;

    /**
     * True, if the schedule is preemptive
     * False otherwise
     */
    private boolean isPreemptive;

    /**
     * The level of the node in the tree
     * If k = numberOfJobs the tree node is a leaf of the tree
     */
    private int k;

    /**
     * Non preemptive schedule
     */
    private int[] schedule;

    /**
     * Constructor for tree node
     *
     * @param i the instance to be solved
     * @param k the level of the node in the tree
     */
    TreeNode(Instance i, int k) {
        // The instance to be solved
        instance = i;
        // Lowerbound for the current tree node
        lowerBound = -1;
        // Set the level of the node in the tree
        this.k = k;
        // Set the partial solution
        partialSolution = new Solution(instance);
        // Set the preemptive flag
        isPreemptive = true;
    }

    /**
     * Constructor for tree node
     *
     * @param i the instance to be solved
     * @param sol starting partial solution
     * @param k the level of the node in the tree
     */
    TreeNode(Instance i, Solution sol, int k) {
        // The instance to be solved
        instance = i;
        // Lower bound for the current tree node
        lowerBound = -1;
        // Set the level of the node in the tree
        this.k = k;
        // Set the partial solution
        partialSolution = new Solution(instance, sol);
        // Set the preemptive flag
        isPreemptive = false;
    }

    /**
     * Compare two jobs considering the processing time
     */
    class MinProcessingTimeFirst implements Comparator<Integer> {

        @Override
        public int compare(Integer j1, Integer j2) {
            return Integer.compare(
                    getPartialSolution().getRemainingTimeForNotScheduledJob(j1),
                    getPartialSolution().getRemainingTimeForNotScheduledJob(j2)
            );
        }
    }

    /**
     * Compare two jobs considering the release time
     */
    class MinReleaseTimeFirst implements Comparator<Integer> {

        @Override
        public int compare(Integer j1, Integer j2) {
            return Integer.compare(instance.getJob(j1).getReleaseTime(), instance.getJob(j2).getReleaseTime());
        }
    }

    /**
     * Compute the lower bound for this tree node
     * The partial solution is a non-preemptive schedule
     * we schedule the remaining jobs using preemption
     * This is a relaxation of the original problem and represents a lower bound for the instance
     */
    void calculateLowerBound() {
        int lowerBound = 0;

        // Order not released jobs by increasing release time
        PriorityQueue<Integer> notReleasedJobs = new PriorityQueue<>(instance.getNumberOfJobs()-k, new MinReleaseTimeFirst());
        for (int jobId = 1; jobId <= getPartialSolution().numberOfJobs(); jobId++) {
            if (!getPartialSolution().isScheduled(jobId)) {
                notReleasedJobs.add(jobId);
            }
        }

        // Order released jobs by increasing processing time
        PriorityQueue<Integer> releasedJobs = new PriorityQueue<>(instance.getNumberOfJobs()-k, new MinProcessingTimeFirst());

        // Update lower bound
        lowerBound += getPartialSolution().sumOfCompletionTimesForScheduledJobs();

        // Get the completion time of the partial instant
        // Starting from this instant we schedule the remaining jobs
        int currentInstant = getPartialSolution().makeSpan();

        // Build schedule if is non preemptive
        int[] schedule = new int[instance.getNumberOfJobs()];

        int preemptedJobId = -1;
        int nextReleaseTime = notReleasedJobs.peek() != null ? instance.getJob(notReleasedJobs.peek()).getReleaseTime() : INFINITY;
        while (!notReleasedJobs.isEmpty() || !releasedJobs.isEmpty()) {
            do {
                // Get all scheduled jobs
                if (nextReleaseTime <= currentInstant) {
                    int jobId = notReleasedJobs.poll();
                    releasedJobs.add(jobId);
                }
                // Get next release instant
                nextReleaseTime = notReleasedJobs.peek() != null ? instance.getJob(notReleasedJobs.peek()).getReleaseTime() : INFINITY;
            } while (!notReleasedJobs.isEmpty() && nextReleaseTime <= currentInstant);

            // Schedule all released jobs with preemption
            int processingTime;
            while (releasedJobs.size() > 0) {
                int jobId = releasedJobs.peek();
                if (preemptedJobId != -1 && jobId != preemptedJobId) {
                    // A job has been interrupted: the schedule is not preemptive
                    isPreemptive = true;
                }
                if (!isPreemptive && jobId != preemptedJobId) {
                    schedule[jobId-1] = currentInstant+1;
                }
                if (currentInstant + getPartialSolution().getRemainingTimeForNotScheduledJob(jobId) <= nextReleaseTime) {
                    // The job complete before the release of the next job, execute the whole job...
                    processingTime = getPartialSolution().getRemainingTimeForNotScheduledJob(jobId);
                    getPartialSolution().processJob(jobId, processingTime);
                    if (getPartialSolution().isCompletedForNotScheduledJob(jobId)) {
                        lowerBound += currentInstant + processingTime;
                        //...and remove from the released jobs
                        releasedJobs.poll();
                        preemptedJobId = -1;
                    } else {
                        System.err.println("Inconsistent completion time");
                        System.exit(-1);
                    }
                    // Update the current instant
                    currentInstant += processingTime;
                } else {
                    // The job is interrupted by a new release
                    preemptedJobId = jobId;
                    processingTime = nextReleaseTime - currentInstant;
                    getPartialSolution().processJob(jobId, processingTime);
                    if (getPartialSolution().isCompletedForNotScheduledJob(jobId)) {
                        lowerBound += currentInstant + processingTime;
                        // If he job has been completed, remove from the released jobs
                        releasedJobs.poll();
                        preemptedJobId = -1;
                    }
                    break;
                }
            }
            // Update the current instant
            currentInstant = nextReleaseTime;
        }

        // Update the lower bound for the tree node
        setLowerBound(lowerBound);

        if (!isPreemptive) {
            this.schedule = schedule;
        }
    }

    /**
     * Get the partial solution associated to the tree node
     * @return the partial solution
     */
    Solution getPartialSolution() {
        return partialSolution;
    }

    /**
     * Get the lower bound of the partial solution
     * @return the lower bound
     */
    int getLowerBound() {
        return lowerBound;
    }

    /**
     * Set the lower bound of the partial solution
     * @param lowerBound the lower bound
     */
    private void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * Get the level of the node in the tree
     * @return the level of the node in the tree
     */
    int getK() {
        return k;
    }

    /**
     * Set the level of the node in the tree
     * @param k the level of the node in the tree
     */
    @SuppressWarnings("unused")
    void setK(int k) {
        this.k = k;
    }


    /**
     * Get a string representation of the tree node
     * @return the string representation
     */
    public String toString() {
        String result = "";

        result += "\n\nLowerBound: " + getLowerBound() + "\n";
        result += getPartialSolution();
        return result;
    }


    /* Getters and Setters */

    /**
     * Check if the computed schedule is preemptive
     * @return true if the schedule is preemptive, false otherwise
     */
    boolean isPreemptive() {
        if (lowerBound == -1)
            calculateLowerBound();
        return isPreemptive;
    }

    /**
     * Update the solution with non preemptive schedule
     */
    void getNotPreemptiveSchedule() {
        for (int i = 0; i < schedule.length; i++) {
            if (schedule[i] >= 1) {
                int jobId = i+1;
                int startInstant = schedule[i]-1;
                partialSolution.processAndScheduleJob(jobId, startInstant);
            }
        }
    }
}