package branch_and_bound;

import dnl.utils.text.table.TextTable;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

import static branch_and_bound.Constants.INFINITY;

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
    private Job[] jobs;

    //private TreeSet<Integer> jobsOrderedByReleaseTime;

    /**
     * Default constructor
     */
    public Instance() {
        this.numberOfJobs = 0;
        this.processingTimeMin = 0;
        this.processingTimeMax = INFINITY;
        //this.jobsOrderedByReleaseTime = new TreeSet<>(new MinReleaseTimeFirst());
    }

    /**
     * Compare two jobs considering the release time
     */
    class MinReleaseTimeFirst implements Comparator<Integer> {

        @Override
        public int compare(Integer j1, Integer j2) {
            return Integer.compare(getJob(j1).getReleaseTime(), getJob(j2).getReleaseTime());
        }
    }

    /**
     * Compare two jobs considering the release time
     */
    class MinProcessingTimeFirst implements Comparator<Integer> {

        @Override
        public int compare(Integer j1, Integer j2) {
            return Integer.compare(getJob(j1).getProcessingTime(), getJob(j2).getProcessingTime());
        }
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
    @SuppressWarnings("unused")
    public int getProcessingTimeMin() {
        return this.processingTimeMin;
    }

    /**
     * Getter for processing time max
     *
     * @return the maximum processing time
     */
    @SuppressWarnings("unused")
    public int getProcessingTimeMax() {
        return this.processingTimeMax;
    }

    /**
     * Setter for the number of jobs
     *
     * @param numberOfJobs the number of jobs
     */
    void setNumberOfJobs(int numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
        this.jobs = new Job[numberOfJobs];
    }

    /**
     * Setter for min processing time
     *
     * @param processingTimeMin the minimum processing time
     */
    void setProcessingTimeMin(int processingTimeMin) {
        this.processingTimeMin = processingTimeMin;
    }

    /**
     * Setter for max processing time
     *
     * @param processingTimeMax the maximum processing time
     */
    void setProcessingTimeMax(int processingTimeMax) {
        this.processingTimeMax = processingTimeMax;
    }

    /**
     * Getter for the jobs hashmap
     *
     * @return the jobs hasmap
     */
    public Job[] getJobs() {
        return this.jobs;
    }

    /**
     * Sort jobs by release time
     *
     * @return the jobs hasmap
     */
    PriorityQueue<Integer> getJobsSortedByReleaseTime() {
        PriorityQueue<Integer> sortedJobs = new PriorityQueue<>(numberOfJobs, new MinReleaseTimeFirst());
        for (int jobId = 1; jobId <= numberOfJobs; jobId++) {
            sortedJobs.add(jobId);
        }
        return sortedJobs;
    }

    /**
     * Sort jobs by processing time
     *
     * @return the jobs hasmap
     */
    PriorityQueue<Integer> getJobsSortedByProcessingTime() {
        PriorityQueue<Integer> sortedJobs = new PriorityQueue<>(numberOfJobs, new MinProcessingTimeFirst());
        for (int jobId = 1; jobId <= numberOfJobs; jobId++) {
            sortedJobs.add(jobId);
        }
        return sortedJobs;
    }

    /**
     * Getter for the job
     * @return the job
     */
    Job getJob(int jobId) {
        return this.jobs[jobId-1];
    }

    /**
     * Add a new job to the jobs hashmap
     * @param job the new job
     */
    void addJob(Job job) {
        this.jobs[job.getId()-1] = job;
        //this.jobsOrderedByReleaseTime.add(job.getId());
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
    void setName(String name) {
        this.name = name;
    }

    /**
     * Print the instance on the console
     */
    void printInstance() {
        printInstance(null);
    }

    /**
     * This method print an instance
     * If outputFilename is not null, print on a file
     */
    void printInstance(String outputFilename) {

        try {
            // If outputFilename is not null, redirect output to file
            if (outputFilename != null) {
                PrintStream fileStream = new PrintStream(outputFilename);
                System.setOut(fileStream);
            }

            // Print instance metadata
            System.out.println(String.format("********* Instance %s *********", this.name));
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

            for (int i = 0; i < this.jobs.length; i++) {
                data[i][0] = this.jobs[i].getId();
                data[i][1] = this.jobs[i].getProcessingTime();
                data[i][2] = this.jobs[i].getReleaseTime();
            }

            // Print table
            TextTable tt = new TextTable(columnNames, data);
            tt.printTable();

            System.out.println("************************************");
            System.out.println("____________________________________");
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
