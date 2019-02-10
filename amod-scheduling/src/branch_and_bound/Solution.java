package branch_and_bound;

/**
 * Representation of the solution
 */
public class Solution {
    /**
     * The reference to the instance to be solved
     */
    private static Instance instance;

    /**
     * The makespan of the solution
     * (i.e. the completion time of the last job in the schedule)
     */
    private int makespan;

    /**
     * The sum of the completion times of the scheduled jobs
     */
    private int sumOfCompletionTimes;

    /**
     * The array of the jobs
     * For the jobs in the scheduled contains the start instant
     * For the jobs not in the schedule contains the remaining processing time
     */
    private int[] jobs;

    /**
     * Constructor
     * @param i the instance to be solved
     */
    Solution(Instance i) {
        // The instance to be solved
        instance = i;

        // Initialize the array of the jobs
        jobs = new int[instance.getNumberOfJobs()];

        // Makespan initially set to 0 because there is no job in the schedule
        makespan = 0;

        // Sum of completion times initially set to 0 because there is no job in the schedule
        sumOfCompletionTimes = 0;
    }

    /**
     * Constructor
     * @param i the instance to be solved
     * @param aSol the starting partial solution
     */
    Solution(Instance i, Solution aSol) {
        // The instance to be solved
        instance = i;

        // Initialize the array of the jobs
        jobs = new int[instance.getNumberOfJobs()];

        // Initialize the start instant for the jobs already scheduled
        for (int jobId = 1; jobId <= numberOfJobs(); jobId++) {
            if (aSol.isScheduled(jobId)) {
                int startInstant = aSol.getStartInstantForScheduledJob(jobId);
                setStartInstantForScheduledJob(jobId, startInstant);
            }
        }

        // Makespan initially set to the makespan of current partial solution
        makespan = aSol.makespan;

        // Sum of completion times initially set to the sum of completion times of current partial solution
        sumOfCompletionTimes = aSol.sumOfCompletionTimes;
    }

    /**
     * Schedule the job with id jobId at the supplied instant
     *
     * @param jobId          the id of the job to be scheduled
     * @param startInstant   the start instant
     */
    void processAndScheduleJob(int jobId, int startInstant) {
        if (isScheduled(jobId)) {
            // Cannot schedule a job already scheduled
            System.err.println("Already scheduled. Error in processAndScheduleJob()");
            System.exit(-1);
        }

        // Update the makespan
        if (startInstant + instance.getJob(jobId).getProcessingTime() > makespan) {
            makespan = startInstant + instance.getJob(jobId).getProcessingTime();
        }

        // Update the sum of completion times
        sumOfCompletionTimes += startInstant + instance.getJob(jobId).getProcessingTime();

        // Add the job to the schedule
        setStartInstantForScheduledJob(jobId, startInstant);
    }

    void processJob(int jobId, int processingTime) {
        if (isScheduled(jobId)) {
            // Cannot process a job already scheduled
            System.err.println("Job scheduled. Error in processAndScheduleJob()");
            System.exit(-1);
        } else {
            // Job not scheduled
            // Update processed time
            int processedTime = getProcessedTimeForNotScheduledJob(jobId);
            processedTime = processedTime + processingTime;
            setProcessedTimeForNotScheduledJob(jobId, processedTime);
        }
    }

    /**
     * Check if the job with id jobId is scheduled
     *
     * @param jobId the id of the job
     * @return true if the job is scheduled, false otherwise
     */
    boolean isScheduled(int jobId) {
        return this.jobs[jobId-1] > 0;
    }

    /**
     * Get the numberOfJobs of the solution
     *
     * @return the number of the jobs of the schedule
     */
    int numberOfJobs() {
        return this.jobs.length;
    }

    /**
     * Get the sum of the completion time of the scheduled jobs
     *
     * @return the sum of the completion time for the schedule
     */
    int sumOfCompletionTimesForScheduledJobs() {
        return sumOfCompletionTimes;
    }

    /**
     * Get a string representation of the solution
     *
     * @return the string representation
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String instant;
        String jobIdStr;
        String completionTime;

        sb.append(instance.getNumberOfJobs()).append("\n");
        for (int jobId = 1; jobId <= this.numberOfJobs(); jobId++) {
            if (!isScheduled(jobId)) {
                continue;
            }
            int startInstant = getStartInstantForScheduledJob(jobId);
            int processingTime = instance.getJob(jobId).getProcessingTime();
            completionTime = Integer.toString(startInstant + processingTime);
            instant = Integer.toString(startInstant);
            jobIdStr = Integer.toString(jobId);

            sb.append(instant).append(" ").append(jobIdStr).append(" ").append(completionTime).append("\n");
        }
        sb.append("Sum of completion times: ").append(sumOfCompletionTimesForScheduledJobs()).append("\n");

        return sb.toString();
    }

    /**
     * Get the total time required by the actual solution (makespan)
     * i.e. the completion time of the last job
     *
     * @return the makespan
     */
    int makeSpan() {
        return makespan;
    }

    /**
     * Check if the job has been completed
     *
     * @param jobId the id of the job
     * @return true if the job has been completed, false otherwise
     */
    boolean isCompletedForNotScheduledJob(int jobId) {
        if (getRemainingTimeForNotScheduledJob(jobId) < 0) {
            // A job cannot execute more than its remaining time
            System.err.println("A job cannot execute more than its remaining time");
            System.err.println("The remaining time must be a positive number");
            System.exit(-1);
        }
        return getRemainingTimeForNotScheduledJob(jobId) == 0;
    }

    /**
     * Get the remaining time
     *
     * @return the remaining time
     */
    int getRemainingTimeForNotScheduledJob(int jobId) {
        if (isScheduled(jobId)) {
            // Job completed and scheduled
            // The remaining time is not valid
            System.err.println("Error in getRemainingTimeForNotScheduledJob");
            System.exit(-1);
        }
        // Job not completed
        // Get the remaining time
        int processingTime = instance.getJob(jobId).getProcessingTime();
        int processedTime = getProcessedTimeForNotScheduledJob(jobId);
        return processingTime - processedTime;
    }

    /**
     * Get the remaining time
     *
     * @return the remaining time
     */
    @SuppressWarnings("unused")
    int getCompletionTimeForScheduledJob(int jobId) {
        if (!isScheduled(jobId)) {
            // Job not scheduled
            System.err.println("Job not yet completed or not scheduled");
            System.exit(-1);
        }
        // Job scheduled
        // Get the completion time
        int startInstant = getStartInstantForScheduledJob(jobId);
        int processingTime = instance.getJob(jobId).getProcessingTime();
        return startInstant + processingTime;
    }

    /**
     * Set processing time
     *
     * @param jobId the id of the job
     * @param processedTime the processed time
     */
    private void setProcessedTimeForNotScheduledJob(int jobId, int processedTime) {
        if (processedTime < 0) {
            System.err.println("Error in setProcessedTimeForNotScheduledJob");
        }
        this.jobs[jobId-1] = -processedTime;
    }

    /**
     * Set the start instant for a scheduled job
     *
     * @param jobId the id of the job
     * @param startInstant the start instant
     */
    private void setStartInstantForScheduledJob(int jobId, int startInstant) {
        if (startInstant < 0) {
            System.err.println("Error in setStartInstantForScheduledJob");
        }
        this.jobs[jobId-1] = startInstant+1;
    }

    /**
     * Get the processed time for a job not in the schedule
     *
     * @param jobId the id of the job
     * @return the processed time
     */
    private int getProcessedTimeForNotScheduledJob(int jobId) {
        if (isScheduled(jobId)) {
            System.err.println("Error in getProcessedTimeForNotScheduledJob");
        }
        return -this.jobs[jobId-1];
    }

    /**
     * Get the start instant for a scheduled job
     * @param jobId the id of the job
     * @return the start instant
     */
    private int getStartInstantForScheduledJob(int jobId) {
        if (!isScheduled(jobId)) {
            System.err.println("Error in getProcessedTimeForNotScheduledJob");
        }
        return this.jobs[jobId-1]-1;
    }
}
