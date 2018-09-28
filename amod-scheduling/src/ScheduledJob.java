public class ScheduledJob {

    private Job job;
    private Integer startInstant;
    private Integer endInstant;

    public ScheduledJob(Job job, Integer startInstant, Integer endInstant) {
        this.job = job;
        this.startInstant = startInstant;
        this.endInstant = endInstant;
    }

    public Job getJob() {
        return job;
    }

    public Integer getStartInstant() {
        return startInstant;
    }

    public Integer getEndInstant() {
        return endInstant;
    }

}
