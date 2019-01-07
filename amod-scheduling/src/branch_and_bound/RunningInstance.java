package branch_and_bound;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represent a running instance of the algorithm
 */
@SuppressWarnings("unused")
public class RunningInstance {
    /**
     * The instance
     */
    private Instance instance;

    /**
     * The running jobs represented as pairs (jobId, job)
     */
    private HashMap<Integer, RunningJob> runningJobs;


    RunningInstance(Instance instance) {
        this.instance = instance;
        runningJobs = new HashMap<>();

        for (Job job : this.instance.getJobs().values()) {
            int jobId = job.getId();
            RunningJob runningJob = new RunningJob(job);
            runningJobs.put(jobId, runningJob);
        }
    }

    RunningInstance(Instance instance, ArrayList<RunningJob> runningJobs) {
        this.instance = instance;
        this.runningJobs = new HashMap<>();

        for (Job job : this.instance.getJobs().values()) {
            int jobId = job.getId();
            RunningJob runningJob = new RunningJob(job);
            this.runningJobs.put(jobId, runningJob);
        }

        for (RunningJob j: runningJobs) {
            this.runningJobs.put(j.getId(), j.copy());
        }
    }

    /* Getters and setters */

    /**
     * Getter for the instance
     * @return the instance
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * Setter for the instance
     * @param instance the instance
     */
    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    /**
     * Get the running jobs
     * @return the hashmap containing the running jobs
     */
    HashMap<Integer, RunningJob> getRunningJobs() {
        return runningJobs;
    }

    /**
     * Set the running jobs
     * @param runningJobs the running jobs hashmap
     */
    public void setRunningJobs(HashMap<Integer, RunningJob> runningJobs) {
        this.runningJobs = runningJobs;
    }

    /**
     * Add a running jobs
     * @param runningJob the running job to be added
     */
    public void addRunningJob(RunningJob runningJob) {
        this.runningJobs.put(runningJob.getJob().getId(), runningJob);
    }

    /**
     * Get the jobs released at currentInstant
     * @param currentInstant the current instant
     * @return the list of the job ids
     */
    public ArrayList<Integer> getReleasedJobs(int currentInstant) {
        return this.instance.getReleasedJobs(currentInstant);
    }

    /**
     * Get the completed jobs
     * @return the list of the completed jobs
     */
    public ArrayList<Integer> getCompletedJobs() {
        ArrayList<Integer> completedJobs = new ArrayList<>();
        for (RunningJob job : getRunningJobs().values()) {
            if (job.isCompleted()) {
                completedJobs.add(job.getId());
            }
        }
        return completedJobs;
    }

    /**
     * Get the processing times of the jobs
     * @return the hashmap containing pairs (jobId, processingTime)
     */
    public HashMap<Integer, Integer> getProcessingTimes() {
        return this.instance.getProcessingTimes();
    }

    /**
     * Get the number of jobs
     * @return the number of jobs
     */
    public int getNumberOfJobs() {
        return this.instance.getNumberOfJobs();
    }

    /**
     * Get the minimum processing time
     * @return the min processing time
     */
    public int getProcessingTimeMin() {
        return this.instance.getProcessingTimeMin();
    }

    /**
     * Get the maximum processing time
     * @return the maximum processing times
     */
    public int getProcessingTimeMax() {
        return this.instance.getProcessingTimeMax();
    }

    /**
     * Set the number of jobs
     * @param numberOfJobs the number of jobs
     */
    public void setNumberOfJobs(int numberOfJobs) {
        this.instance.setNumberOfJobs(numberOfJobs);
    }

    /**
     * Set the minimum processing time
     * @param processingTimeMin the min processing time
     */
    public void setProcessingTimeMin(int processingTimeMin) {
        this.instance.setProcessingTimeMin(processingTimeMin);
    }

    /**
     * Set the maximum processing time
     * @param processingTimeMax the max processing times
     */
    public void setProcessingTimeMax(int processingTimeMax) {
        this.instance.setProcessingTimeMax(processingTimeMax);
    }

    /**
     * Get all the jobs
     * @return the hashmap containing pairs (jobId, job)
     */
    public HashMap<Integer, Job> getJobs() {
        return this.instance.getJobs();
    }

    /**
     * Get the name of the instance
     * @return the name of the instance
     */
    public String getName() {
        return this.instance.getName();
    }

    /**
     * Set the name of the instance
     * @param name the name of the instance
     */
    public void setName(String name) {
        this.instance.setName(name);
    }

    /**
     * Print the instance on the console
     */
    public void printInstance() {
        this.instance.printInstance();
    }

    /**
     * This method print an instance
     * If outputFilename is not null, print on a file
     */
    public void printInstance(String outputFilename) {
        this.instance.printInstance(outputFilename);
    }
}
