package branch_and_bound;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Representation of the solution using HashMap
 */
@SuppressWarnings("unused")
public class Solution {
    private static final String DEFAULT_PATH_FILES = "./data/";
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * The instance to be solved
     */
    private RunningInstance instance;

    /**
     * The scheduled jobs stored as pairs (schedule_instant, job_id)
     */
    private TreeMap<Integer, RunningJob> schedule;

    public Solution(RunningInstance instance) {
        this.instance = instance;
        schedule = new TreeMap<>();
    }

    public Solution(RunningInstance instance, Solution aSol) {
        this.instance = instance;
        this.schedule = new TreeMap<>(aSol.getSchedule());
    }

    /**
     * Schedule the job with id jobId at the supplied instant
     * @param jobId the id of the job to be scheduled
     * @param startInstant the start instant
     * @param processingTime the processing time
     */
    void scheduleJob(int jobId, int startInstant, int processingTime) {
        // Get the job
        RunningJob job = getJob(jobId);
        // Schedule at the supplied instant for processingTime
        job.schedule(startInstant, processingTime);
        // Add the job to the schedule
        getSchedule().put(startInstant, job);
    }

    /**
     * Get the scheduled jobs
     * @return scheduledJobs
     */
    ArrayList<RunningJob> getScheduledJobs() {
        return new ArrayList<>(getSchedule().values());
    }

    /**
     * Get the completed jobs
     * @return scheduledJobs
     */
    public ArrayList<RunningJob> getCompletedJobs() {
        ArrayList<RunningJob> completedJobs = new ArrayList<>();
        for (RunningJob job : getJobs()) {
            if (job.isCompleted()) {
                completedJobs.add(job);
            }
        }
        return completedJobs;
    }

    /**
     * Get the size of the solution
     * @return the size of the schedule
     */
    public int size() {
        return getSchedule().size();
    }

    /**
     * Get the sum of the completion time of the scheduled jobs
     * @return the sum of the completion time for the schedule
     */
    int sumOfCompletionTimes() {
        int sumOfCompletionTimes = 0;
        for (RunningJob job : getJobs()) {
            sumOfCompletionTimes += job.getCompletionTime();
        }
        return sumOfCompletionTimes;
    }

    /**
     * Get the total time required by the actual solution (makespan)
     * i.e. the completion time of the last job
     * @return the makespan
     */
    int makeSpan() {
        Map.Entry<Integer, RunningJob> entry = getSchedule().lastEntry();
        if (entry == null)
            return 0;
        RunningJob job = entry.getValue();
        return job.getCompletionTime();
    }

    /**
     * Exporta la solución con el mismo formato que el
     * fichero de especificación de problema a un fichero
     * que se crea en el directorio data
     * @param fileName the filename
     */
    public void exportSolutionToFile(String fileName) {
        if (!fileName.contains(DEFAULT_PATH_FILES)) {
            fileName = DEFAULT_PATH_FILES + fileName;
        }

        try {
            PrintWriter writer = new PrintWriter(fileName, DEFAULT_ENCODING);
            writer.print(this);
            writer.close();

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /* Getters and Setters */

    /**
     * Get a string representation of the solution
     * @return the string representation
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String instant;
        String jobId;
        String completionTime;

        sb.append(getInstance().getNumberOfJobs()).append("\n");
        for(Map.Entry<Integer,RunningJob> entry : getSchedule().entrySet()) {
            RunningJob job = entry.getValue();
            completionTime = Integer.toString(job.getCompletionTime());
            instant = Integer.toString(entry.getKey());
            jobId = Integer.toString(job.getId());

            sb.append(instant).append(" ").append(jobId).append(" ").append(completionTime).append("\n");
        }
        sb.append("Sum of completion times: ").append(sumOfCompletionTimes()).append("\n");

        return sb.toString();
    }

    /**
     * Get the instance
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
     * Get the schedule
     * @return the schedule
     */
    public TreeMap<Integer, RunningJob> getSchedule() {
        return schedule;
    }

    /**
     * Set the schedue
     * @param schedule the schedule
     */
    public void setSchedule(TreeMap<Integer, RunningJob> schedule) {
        this.schedule = schedule;
    }

    /**
     * Get the jobs of the instance
     * @return the list of the jobs
     */
    public ArrayList<RunningJob> getJobs() {
        return new ArrayList<>(getInstance().getRunningJobs().values());
    }

    /**
     * Get the job which has the supplied id
     * @param jobId the job id
     * @return the job
     */
    public RunningJob getJob(int jobId) { return getInstance().getRunningJobs().get(jobId); }
}