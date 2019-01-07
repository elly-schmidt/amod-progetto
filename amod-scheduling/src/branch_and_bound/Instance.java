package branch_and_bound;

import dnl.utils.text.table.TextTable;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import static branch_and_bound.Constants.INFINITY;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Instance {
    /**
     * The number of the jobs belonging to the instance
     */
    private int numberOfJobs;

    /**
     * The minimum processing time for the instance
     */
    private int processingTimeMin;

    /**
     * The maximum processing time for the instance
     */
    private int processingTimeMax;

    /**
     * The name of the instance
     */
    private String name;

    /**
     * An hashmap containing pair (id, job)
     */
    private HashMap<Integer, Job> jobs;

    /**
     * Default constructor
     */
    public Instance() {
        this.numberOfJobs = 0;
        this.processingTimeMin = 0;
        this.processingTimeMax = INFINITY;
        this.jobs = new HashMap<>();
    }

    /* Getters and setters */

    /**
     * Getter for the number of jobs
     * @return the number of jobs
     */
    public int getNumberOfJobs() {
        return this.numberOfJobs;
    }

    /**
     * Getter for processing time min
     * @return the minimum processing time
     */
    public int getProcessingTimeMin() {
        return this.processingTimeMin;
    }


    /**
     * Getter for processing time max
     * @return the maximum processing time
     */
    public int getProcessingTimeMax() {
        return this.processingTimeMax;
    }

    /**
     * Setter for the number of jobs
     * @param numberOfJobs the number of jobs
     */
    public void setNumberOfJobs(int numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
    }

    /**
     * Setter for min processing time
     * @param processingTimeMin the minimum processing time
     */
    public void setProcessingTimeMin(int processingTimeMin) {
        this.processingTimeMin = processingTimeMin;
    }

    /**
     * Setter for max processing time
     * @param processingTimeMax the maximum processing time
     */
    public void setProcessingTimeMax(int processingTimeMax) {
        this.processingTimeMax = processingTimeMax;
    }

    /**
     * Getter for the jobs hashmap
     * @return the jobs hasmap
     */
    public HashMap<Integer, Job> getJobs() {
        return this.jobs;
    }

    /**
     * Add a new job to the jobs hashmap
     * @param job the new job
     */
    public void addJob(Job job) {
        this.jobs.put(job.getId(), job);
    }

    public void removeJob(int jobId) {
        this.jobs.remove(jobId);
    }

    /**
     * Get the name of the instance
     * @return the name of the instance
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for the name of the instance
     * @param name the name of the instance
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the jobs which has been released at currentInsant
     * @param currentInstant the current instant
     * @return the list of the job ids
     */
    public ArrayList<Integer> getReleasedJobs(int currentInstant) {
        ArrayList<Integer> releasedJobs = new ArrayList<>();
        for (Job job : getJobs().values()) {
            if (currentInstant <= job.getReleaseTime()) {
                // The job has been released
                releasedJobs.add(job.getId());
            }
        }
        // Return the result
        return releasedJobs;
    }

    /**
     * Get the processing of all jobs
     * @return an hashmap containing pairs (jobId, processing time)
     */
    public HashMap<Integer, Integer> getProcessingTimes() {
        HashMap<Integer, Integer> processingTimes = new HashMap<>();
        for (Job job : getJobs().values()) {
            processingTimes.put(job.getId(), job.getProcessingTime());
        }
        // Return the result
        return processingTimes;
    }

    /**
     * Print the instance on the console
     */
    public void printInstance() {
        printInstance(null);
    }

    /**
     * This method print an instance
     * If outputFilename is not null, print on a file
     */
    public void printInstance(String outputFilename) {

        try {
            // If outputFilename is not null, redirect output to file
            if (outputFilename != null) {
                PrintStream fileStream = new PrintStream(outputFilename);
                System.setOut(fileStream);
            }

            // Print instance metadata
            System.out.println(String.format("********* branch_and_bound.Instance %s *********", this.name));
            System.out.println(String.format("Number of jobs: %d", this.numberOfJobs));
            System.out.println(String.format("Min processing time: %d", this.processingTimeMin));
            System.out.println(String.format("Max processing time: %d", this.processingTimeMax));
            System.out.println();

            // Build table

            // Table header
            String[] columnNames = {
                    "#",
                    "Processing time",
                    "Release time"
            };

            // Fill table with instance data
            Object[][] data = new Object[this.numberOfJobs][3];
            int i = 0;

            for (Job j : this.jobs.values()) {
                data[i][0] = j.getId();
                data[i][1] = j.getProcessingTime();
                data[i][2] = j.getReleaseTime();

                i++;
            }

            // Print table
            TextTable tt = new TextTable(columnNames, data);
            tt.printTable();

            System.out.println("************************************");
            System.out.println();
            System.out.println();
            System.out.println();

            // Reset standard output
            if (outputFilename != null) {
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
