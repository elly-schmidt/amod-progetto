

public class Job {

    private int id; // Job identifier
    private int processingTime; // Processing time
    private int releaseTime;    // Release time

    public Job() {

    }

    public Job(int id, int processingTime, int releaseTime) {
        this.id = id;
        this.processingTime = processingTime;
        this.releaseTime = releaseTime;
    }

    /* Getter and setters */

    public int getId() {
        return this.id;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public int getReleaseTime() {
        return this.releaseTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public void setReleaseTime(int releaseTime) {
        this.releaseTime = releaseTime;
    }
}
