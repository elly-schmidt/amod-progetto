package branch_and_bound;

/**
 * This class represents a running job
 */
@SuppressWarnings("unused")
public class RunningJob {
    /**
     * The job
     */
    private Job job;

    /**
     * The remaining time, initially set equal to the processing time
     */
    private int remainingTime;

    /**
     * The completion time, set after the job completed
     */
    private int completionTime;

    public RunningJob(Job job) {
        this.job = job;
        this.remainingTime = this.job.getProcessingTime();
        this.completionTime = 0;
    }

    /**
     * Schedule the job at the instant startInstant for executionTime
     * @param startInstant the start instant
     * @param executionTime the execution time
     */
    public void schedule(int startInstant, int executionTime) {
        // Decrease the remaining time of the job
        setRemainingTime(this.remainingTime - executionTime);
        if (this.remainingTime == 0) {
            // Job completed, set the completion time
            setCompletionTime(startInstant + executionTime);
        } else if (this.remainingTime < 0) {
            // A job cannot execute more than its remaining time
            System.err.println("A job cannot execute more than its remaining time");
            System.exit(-1);
        }
    }

    /* Getters and setters */

    /**
     * Get the job
     * @return the job
     */
    public Job getJob() {
        return job;
    }

    /**
     * Set the job
     * @param job the job
     */
    public void setJob(Job job) {
        this.job = job;
    }

    /**
     * Get the remaining time
     * @return the remaining time
     */
    int getRemainingTime() {
        return remainingTime;
    }

    /**
     * Set the remaining time
     * @param remainingTime the remaining time
     */
    void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    /**
     * Get the completion time
     * @return the completion time
     */
    public int getCompletionTime() {
        return this.completionTime;
    }

    /**
     * Set the completion time
     * @param completionTime the completion time
     */
    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    /**
     * Check if the job has been completed
     * @return true if the job has been completed, false otherwise
     */
    public boolean isCompleted() {
        return this.remainingTime == 0;
    }

    /**
     * Get the job id
     * @return the job id
     */
    public int getId() {
        return this.job.getId();
    }

    /**
     * Get the release time
     * @return the release time
     */
    public int getReleaseTime() {
        return this.job.getReleaseTime();
    }

    /**
     * Set the job id
     * @param id the job id
     */
    public void setId(int id) {
        this.job.setId(id);
    }

    /**
     * Set the release time
     * @param releaseTime the release time
     */
    public void setReleaseTime(int releaseTime) {
        this.job.setReleaseTime(releaseTime);
    }

    /**
     * Set the processing time
     * @param processingTime the processing time
     */
    public void setProcessingTime(int processingTime) {
        this.job.setProcessingTime(processingTime);
    }

    /**
     * Get the processing time
     * @return the processing time
     */
    public int getProcessingTime() {
        return this.job.getProcessingTime();
    }

    /**
     * Make a deep copy of the running job
     * and reset remaining time and completion time
     * @return a copy of the job
     */
    RunningJob copy() {
        RunningJob j = new RunningJob(this.job);
        j.setRemainingTime(this.remainingTime);
        j.setCompletionTime(this.completionTime);
        return j;
    }
}
