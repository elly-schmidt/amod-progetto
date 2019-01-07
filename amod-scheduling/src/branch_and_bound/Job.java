package branch_and_bound;

/**
 * This class represents a job
 */
public class Job {
    /**
     * The identifier of the job
     */
    private int id;

    /**
     * The release time of the job
     */
    private int releaseTime;

    /**
     * The processing time of the job
     */
    private int processingTime;

    /**
     * Default constructor
     */
    public Job() {

    }

    public Job(int id, int processingTime, int releaseTime) {
        this.id = id;
        this.releaseTime = releaseTime;
        this.processingTime = processingTime;
    }

    /* Getter and setters */

    /**
     * Get the id of the job
     * @return the id of the job
     */
    public int getId() {
        return this.id;
    }

    /**
     * Get the release time of the job
     * @return the release time of the job
     */
    public int getReleaseTime() {
        return this.releaseTime;
    }

    /**
     * Set the id of the job
     * @param id the id of the job
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set the release time of the job
     * @param releaseTime the release time of the job
     */
    public void setReleaseTime(int releaseTime) {
        this.releaseTime = releaseTime;
    }

    /**
     * Set the processing time of the job
     * @param processingTime the processing time of the job
     */
    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    /**
     * Get the processing time of the job
     * @return the processing time of the job
     */
    public int getProcessingTime() {
        return processingTime;
    }

}
