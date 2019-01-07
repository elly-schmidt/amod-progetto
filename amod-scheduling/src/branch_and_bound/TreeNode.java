package branch_and_bound;

import java.util.*;

import static branch_and_bound.Constants.INFINITY;

/**
 * This class represents a node in the enumeration tree
 */
@SuppressWarnings("unused")
public class TreeNode {
    /**
     * The instance to be solved
     */
    private RunningInstance instance;

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
     * The level of the node in the tree
     * If k = numberOfJobs the tree node is a leaf of the tree
     */
    private int k;

    TreeNode(Instance instance) {
        this.instance = new RunningInstance(instance);
        partialSolution = new Solution(this.instance);
        lowerBound = -1;
        k = -1;
    }

    TreeNode(Instance instance, Solution sol) {
        this.instance = new RunningInstance(instance, sol.getJobs());
        partialSolution = new Solution(this.instance, sol);
        lowerBound = -1;
        k = -1;
    }

    /**
     * Compare two jobs considering the processing time
     */
    class MinProcessingTimeFirst implements Comparator<RunningJob> {

        @Override
        public int compare(RunningJob j1, RunningJob j2) {
            return Integer.compare(j1.getRemainingTime(), j2.getRemainingTime());
        }
    }

    /**
     * Compare two jobs considering the release time
     */
    class MinReleaseTimeFirst implements Comparator<RunningJob> {

        @Override
        public int compare(RunningJob j1, RunningJob j2) {
            return Integer.compare(j1.getReleaseTime(), j2.getReleaseTime());
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

        // Get non-scheduled jobs
        ArrayList<RunningJob> notScheduledJobs = getNotScheduledJobs();

        // Order not released jobs by increasing release time
        PriorityQueue<RunningJob> notReleasedJobs = new PriorityQueue<>(new MinReleaseTimeFirst());
        notReleasedJobs.addAll(notScheduledJobs);

        // Order released jobs by increasing processing time
        PriorityQueue<RunningJob> releasedJobs = new PriorityQueue<>(new MinProcessingTimeFirst());

        // Jobs in the partial solution are scheduled without preemption
        // Sum the completion times
        for(Map.Entry<Integer,RunningJob> entry : getPartialSolution().getSchedule().entrySet()) {
            RunningJob job = entry.getValue();
            int completionTime = job.getCompletionTime();
            if (completionTime == 0) {
                System.err.println("Error completion time cannot be zero");
            }
            lowerBound += completionTime;
        }

        // Get the completion time of the partial instant
        // Starting from this instant we schedule the remaining jobs
        int currentInstant = getPartialSolution().makeSpan();

        int nextReleaseTime = notReleasedJobs.peek() != null ? notReleasedJobs.peek().getReleaseTime() : INFINITY;
        while (!notReleasedJobs.isEmpty() || !releasedJobs.isEmpty()) {
            do {
                // Get all scheduled jobs
                if (nextReleaseTime <= currentInstant) {
                    RunningJob job = notReleasedJobs.poll();
                    releasedJobs.add(job);
                }
                // Get next release instant
                nextReleaseTime = notReleasedJobs.peek() != null ? notReleasedJobs.peek().getReleaseTime() : INFINITY;
            } while (!notReleasedJobs.isEmpty() && nextReleaseTime <= currentInstant);

            // Schedule all released jobs with preemption
            int processingTime;
            while (releasedJobs.size() > 0) {
                RunningJob job = releasedJobs.peek();
                if (currentInstant + job.getRemainingTime() <= nextReleaseTime) {
                    // The job complete before the release of the next job, execute the whole job...
                    processingTime = job.getRemainingTime();
                    job.schedule(currentInstant, processingTime);
                    if (job.isCompleted()) {
                        //...and remove from the released jobs
                        releasedJobs.poll();
                    } else {
                        System.err.println("Inconsistent completion time");
                        System.exit(-1);
                    }
                    // Update the current instant
                    currentInstant += processingTime;
                } else {
                    // The job is interrupted by a new release
                    processingTime = nextReleaseTime - currentInstant;
                    job.schedule(currentInstant, processingTime);
                    if (job.isCompleted()) {
                        // If he job has been completed, remove from the released jobs
                        releasedJobs.poll();
                    }
                    break;
                }
            }
            // Update the current instant
            currentInstant = nextReleaseTime;
        }

        // Sum the completion times for the scheduled jobs
        for (RunningJob job : notScheduledJobs) {
            if (job.getCompletionTime() == 0) {
                // The completion time cannot be zero
                System.err.println("Completion time cannot be null");
                System.exit(-1);
            }
            // Update the sum of the completion times (lower bound)
            lowerBound += job.getCompletionTime();
        }
        // Update the lower bound for the tree node
        setLowerBound(lowerBound);
    }

    /**
     * This method returns the jobs not yet scheduled
     * The jobs not yet scheduled are all jobs that are not contained in the partial solution
     * @return the jobs not yet scheduled
     */
    ArrayList<RunningJob> getNotScheduledJobs() {
        // Get the jobs already scheduled...
        ArrayList<RunningJob> scheduledJobs = getPartialSolution().getScheduledJobs();

        //...and get the list of the jobs not yet scheduled
        ArrayList<RunningJob> notScheduledJobs = new ArrayList<>();
        for (RunningJob job : getJobs()) {
            boolean found = false;
            for (RunningJob j : scheduledJobs) {
                if (job.getId() == j.getId())  {
                    // The job has been already scheduled
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Job not found in the scheduled jobs list
                notScheduledJobs.add(job);
            }
        }

        // Return the results
        return notScheduledJobs;
    }

    /* Getters and Setters */

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

    /**
     * Get the running instance associated to the tree node
     * @return the instance
     */
    public RunningInstance getInstance() {
        return instance;
    }

    /**
     * Set the instance
     * @param instance the instance
     */
    public void setInstance(RunningInstance instance) {
        this.instance = instance;
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
    void setK(int k) {
        this.k = k;
    }

    /**
     * Get the jobs
     * @return the list of the jobs
     */
    public ArrayList<RunningJob> getJobs() {
        return new ArrayList<>(getInstance().getRunningJobs().values());
    }

    /**
     * Get job by id
     * @param jobId the job id
     * @return the job
     */
    public RunningJob getJob(int jobId) { return getInstance().getRunningJobs().get(jobId); }
}