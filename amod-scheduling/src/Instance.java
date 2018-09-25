import dnl.utils.text.table.TextTable;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;


public class Instance {

    private int numberOfJobs;
    private int processingTimeMin;
    private int processingTimeMax;
    private String name;    // The name of the instance

    private ArrayList<Job> jobs;

    public Instance() {
        this.numberOfJobs = 0;
        this.processingTimeMin = 0;
        this.processingTimeMax = 0;
        this.jobs = new ArrayList<>();
    }

    /* Getters and setters */

    public int getNumberOfJobs() {
        return this.numberOfJobs;
    }

    public int getProcessingTimeMin() {
        return this.numberOfJobs;
    }

    public int getProcessingTimeMax() {
        return this.numberOfJobs;
    }

    public void setNumberOfJobs(int numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
    }

    public void setProcessingTimeMin(int processingTimeMin) {
        this.processingTimeMin = processingTimeMin;
    }

    public void setProcessingTimeMax(int processingTimeMax) {
        this.processingTimeMax = processingTimeMax;
    }

    public ArrayList<Job> getJobs() {
        return this.jobs;
    }

    public void addJob(Job job) {
        this.jobs.add(job);
    }

    public void removeJob(Job job) {
        this.jobs.remove(job);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
            int i = 0;

            for (Job j : this.jobs) {
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
