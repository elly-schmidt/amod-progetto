import dnl.utils.text.table.TextTable;

import java.util.Arrays;
import java.util.LinkedList;

public class Schedule {

    private LinkedList<ScheduledJob> schedule;
    private Integer lastCompletionTime;
    private Integer completionTimeSum;
    private boolean isPreemptive;

    public Schedule() {
        this.schedule = new LinkedList<>();
        lastCompletionTime = 0;
        completionTimeSum = 0;
        isPreemptive = false;
    }

    public void addJob(Job job, Integer startInstant, Integer endInstant) {
        if (!schedule.isEmpty() && schedule.getLast().getEndInstant() > startInstant) {
            System.err.println("Error in addJob()");
            return;
        }
        schedule.add(new ScheduledJob(job, startInstant, endInstant));
        lastCompletionTime = endInstant;

        if (job.getRemainingTime() == 0) {
            completionTimeSum += endInstant;
        } else {
            isPreemptive = true;
        }

    }

    public Integer getLastCompletionTime() {
        return lastCompletionTime;
    }



    public void addJob(Job job, Integer releaseTime) {
        Integer startInstant = Math.max(lastCompletionTime, releaseTime);
        Integer endInstant = startInstant + job.getProcessingTime();
        schedule.add(new ScheduledJob(job, startInstant, endInstant));
        lastCompletionTime = endInstant;

        completionTimeSum += endInstant;
    }

    public Integer getCompletionTimeSum() {
        return this.completionTimeSum;
    }
/*
    public void print() {
        for (ScheduledJob job : schedule) {
            System.out.print(job.getJob().getId() + " --> ");
        }
        System.out.println("COMPLETION TIME SUM: " + this.getCompletionTimeSum());
    }
*/

    public void print() {



        // Table header
        String[] columnNames = new String[getLastCompletionTime()];
        for (int i = 0; i < getLastCompletionTime(); i++) {
            columnNames[i] = String.valueOf(i);
        }


        // Fill table with instance data
        Object[][] data = new Object[1][getLastCompletionTime()];
//        Arrays.fill(data, 0);

        for (ScheduledJob j : this.schedule) {
            for (int i = j.getStartInstant(); i < j.getEndInstant(); i++) {
                data[0][i] = j.getJob().getId();
            }
        }

        // Print table
        TextTable tt = new TextTable(columnNames, data);
        tt.printTable();


        /*
        for (ScheduledJob job : schedule) {
            System.out.print("****");
        }
        System.out.print("\n");

        for (ScheduledJob job : schedule) {
            System.out.print(job.getJob().getId() + " * ");
        }
        System.out.print("\n");

        for (ScheduledJob job : schedule) {
            System.out.print("****");
        }
        */
        System.out.println();

        System.out.println("COMPLETION TIME SUM: " + this.getCompletionTimeSum());
    }

    public boolean isPreemptive() {
        return isPreemptive;
    }
}
