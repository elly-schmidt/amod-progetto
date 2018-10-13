import com.rits.cloning.Cloner;

import java.lang.reflect.Array;
import java.util.*;

public class BranchAndBoundAlgorithm {

    private float upperBound;   // Best known upper bound for a scheduling instance
    private HashMap<Integer, Job> jobs;
    private Instance instance;

    public BranchAndBoundAlgorithm(Instance instance) {
        this.instance = instance;
        this.jobs = new HashMap<>(instance.getJobs());

        // Initialization

        // Compute upper bound
        Schedule optimalSchedule = computeUpperBound();
        this.upperBound = optimalSchedule.getCompletionTimeSum();

        // List of nodes in the current path from the root of the BnB tree. This path represents a candidate solution.
        LinkedList<Integer> partialSequence = new LinkedList<>();

        LinkedList<Integer> otherNodes = new LinkedList<>();
        for (Job j : this.jobs.values()) {
            otherNodes.add(j.getId()); // initially all jobs are in otherNodes
        }

        // Execute Branch And Bound Algorithm
        Schedule schedule = branchAndBound(partialSequence, otherNodes);
        if (schedule != null) {
            optimalSchedule = schedule;
        }

        // Print results
        optimalSchedule.print();
    }

    private Schedule computeLowerBound(LinkedList<Integer> partialSequence) {
        return computeOptimalPreemptiveSchedule(partialSequence);
    }

    public Schedule branchAndBound(LinkedList<Integer> partialSequence, LinkedList<Integer> remainingJobs) {
        Schedule schedule = null;
        Schedule optimalSchedule = null;

        while (remainingJobs.size() > 0) {
            // Go down the tree
            int jobId = remainingJobs.pop();
            partialSequence.push(jobId);

            // Check pruning condition
            if (checkPruningCondition(jobId, partialSequence, remainingJobs)) {
                // This branch should not be explored
                break; //TODO continue instead of break here?
            }

            // Explore this branch

            // Make preemption assumption and compute a lower bound
            schedule = computeLowerBound(new LinkedList<>(partialSequence));
            int lowerBound = schedule.getCompletionTimeSum();

            // If obtained schedule is preemptive, this is an upper bound, too
            if (!schedule.isPreemptive()) {
                if (lowerBound < upperBound) {
                    // The new upper bound is a better upper bound
                    // Update current best upper bound
                    upperBound = lowerBound;
                    optimalSchedule = schedule;
                }
            }

            if (lowerBound <= upperBound) {
                // If the found lower bound is less then best known upper bound, explore all children
                branchAndBound((LinkedList<Integer>) partialSequence.clone(), (LinkedList<Integer>) remainingJobs.clone());
            }

            // Go up the tree
            partialSequence.pop();
            //remainingJobs.push(jobId);
        }

        return optimalSchedule;
    }

    private Schedule computeUpperBound() {
        return computeFeasibleSchedule();
    }

    /**
     * Check if the subtree rooted in node j has to be pruned.
     *
     * If releaseTime of job j >= max{ t, releaseTime of job i } + processingTime of job i for each i in otherNodes,
     * then the job i can be done entirely before the job j is released.
     * Therefore the subtree rooted in j can be pruned because it does not lead to an optimal solution.
     * The value t is the time of completion of the sequence of jobs leading to job j.
     * @param jobId id of job j
     * @param sequence
     * @param remainingJobs
     * @return true if the input subtree has to be pruned
     */
    private boolean checkPruningCondition(int jobId, LinkedList<Integer> sequence, LinkedList<Integer> remainingJobs) {
        int releaseTime_j = 0, releaseTime_i = 0, processingTime_i = 0;

        for (Job k : this.jobs.values()) {
            if (k.getId() == jobId)
                releaseTime_j = k.getReleaseTime();
        }

        //TODO compute completion time t of sequence

        for (Integer i : remainingJobs) {
            for (Job k : this.jobs.values()) {
                if (k.getId() == i)
                    releaseTime_i = k.getReleaseTime();
                    processingTime_i = k.getProcessingTime();
            }

            if (releaseTime_j >= releaseTime_i + processingTime_i) // pruning condition
                return true;
        }

        return false;
    }

    /**
     * If jobs j1 and j2 have the same release time, order by processing time. Otherwise order by release time.
     */
    class JobComparator implements Comparator<Job> {

        public int compare(Job j1, Job j2) {
            if (j1.getReleaseTime() == j2.getReleaseTime()) {
                return Integer.compare(j1.getProcessingTime(), j2.getProcessingTime());
            } else {
                return Integer.compare(j1.getReleaseTime(), j2.getReleaseTime());
            }
        }
    }


    //private Schedule schedule = new Schedule();
    private int delta = 0;

    private Schedule computeOptimalPreemptiveSchedule(LinkedList<Integer> partialSequence) {

        Schedule schedule = new Schedule();

        Cloner cloner = new Cloner();
        HashMap<Integer, Job> jobs = cloner.deepClone(this.jobs);

        // Add partial sequence to schedule
        //for (int jobId : partialSequence) {
        while(partialSequence.size() > 0) {
            int jobId = partialSequence.removeLast();
            Job j = jobs.get(jobId);
            schedule.addJob(j, j.getReleaseTime());
            jobs.remove(jobId);
        }

        // Schedule other jobs with a preemptive policy

        int nextRelease = Integer.MAX_VALUE;

        Set<Job> releasedJobs = new HashSet<>();

        ArrayList<Job> sortedJobs = new ArrayList<>(jobs.values());

        // Sort jobs by release time and processing time
        sortedJobs.sort(new JobComparator());

        if (sortedJobs.size() == 0) {
            // No other jobs
            return schedule;
        }

        // Compute delta for time shift
        delta = schedule.getLastCompletionTime();
        Job nextReleasedJob = null;
        while (sortedJobs.size() > 0 && sortedJobs.get(0).getReleaseTime() <= delta) {
            // Check released jobs and insert in released jobs set
            Job j = sortedJobs.remove(0);
            j.setReleaseTime(schedule.getLastCompletionTime());
            releasedJobs.add(j);


            // Get minimum relase time
            if (j.getReleaseTime() < nextRelease) {
                nextReleasedJob = j;
                nextRelease = j.getReleaseTime();
            }

        }

        //t = schedule.getLastCompletionTime();

        //scheduleJobNonPreemptive(schedule, releasedJobs);

        // Schedule remaining jobs
        while (!sortedJobs.isEmpty() || !releasedJobs.isEmpty()) {

            // Get next event (release or completion)
            t = Math.min(nextRelease, nextCompletion);

            if (t == nextRelease) {
                // If next event is a release event,
                // add released job to released jobs set
                releasedJobs.add(nextReleasedJob);
                // Get next job to be released

                if (sortedJobs.size() > 0) {
                    // Remove job from jobs set and get release time
                    nextReleasedJob = sortedJobs.remove(0);
                    nextRelease = nextReleasedJob.getReleaseTime();
                } else {
                    // If no job remaining, set next release to INFINITE
                    nextRelease = Integer.MAX_VALUE;
                }
            } else {
                // Job completed, set remaining time to 0
                //currentJob.setRemainingTime(0);
                // Remove jobs from released job set
                releasedJobs.remove(currentJob);
            }

            scheduleJobPreemptive(schedule, releasedJobs);
        }

        return schedule;
    }

    private Job getMinRemainingTime(Set<Job> jobs) {
        int minRemainingTime = Integer.MAX_VALUE;
        Job minRemainingTimeJob = null;
        for (Job job : jobs) {
            if (job.getRemainingTime() < minRemainingTime) {
                minRemainingTime = job.getRemainingTime();
                minRemainingTimeJob = job;
            }
        }
        return minRemainingTimeJob;
    }

    private void scheduleJobPreemptive(Schedule schedule, Set<Job> jobs) {
        Job job = getMinRemainingTime(jobs);
        if (job != null) {
            if (!job.equals(currentJob)) {
                if (currentJob != null) {
                    // There is a job in execution
                    // Compute remaining time
                    int remTime = currentJob.getRemainingTime();
                    currentJob.setRemainingTime(nextCompletion - t);
                    schedule.addJob(currentJob, nextCompletion - remTime, t);
                }
                nextCompletion = t + job.getRemainingTime();
                currentJob = job;
            }
        } else {
            if (currentJob != null) {
                int remTime = currentJob.getRemainingTime();
                currentJob.setRemainingTime(nextCompletion - t);
                schedule.addJob(currentJob, nextCompletion - remTime, t);

            }
            nextCompletion = Integer.MAX_VALUE;
            currentJob = null;
        }

    }


    private Job currentJob = null;
    private int t = 0;

    // Next completion initialized to INFINITE (no job scheduled, no completion time)
    private int nextCompletion = Integer.MAX_VALUE;

    private Schedule computeFeasibleSchedule() {

        Set<Job> releasedJobs = new HashSet<>();
        Cloner cloner = new Cloner();
        HashMap<Integer, Job> jobs = cloner.deepClone(this.jobs);

        ArrayList<Job> sortedJobs = new ArrayList<>(jobs.values());

        // Sort jobs by release time and processing time
        sortedJobs.sort(new JobComparator());

        // Sort jobs by release time and processing time
        sortedJobs.sort(new JobComparator());

        // Get first released job
        Job nextReleasedJob = sortedJobs.remove(0);
        int nextRelease = nextReleasedJob.getReleaseTime();


        // Found schedule
        Schedule schedule = new Schedule();

        while (!sortedJobs.isEmpty() || !releasedJobs.isEmpty()) {

            // Get next event: it could be a release event or a completion event
            t = Math.min(nextRelease, nextCompletion);

            if (t == nextRelease) {
                // If next event is a release event,
                // add released job to released jobs set
                releasedJobs.add(nextReleasedJob);

                // Get next job to be released
                if (sortedJobs.size() > 0) {
                    // Remove job from jobs set and get release time
                    nextReleasedJob = sortedJobs.remove(0);
                    nextRelease = nextReleasedJob.getReleaseTime();
                } else {
                    // If no job remaining, set next release to INFINITE
                    nextRelease = Integer.MAX_VALUE;
                }
            } else {
                // Job completed, set remaining time to 0
                currentJob.setRemainingTime(0);
                // Remove jobs from released job set
                releasedJobs.remove(currentJob);
            }

            // Check if a new job must be scheduled
            scheduleJobNonPreemptive(schedule, releasedJobs);
        }

        // return results
        return schedule;
    }

    private void scheduleJobNonPreemptive(Schedule schedule, Set<Job> releasedJobs) {
        if (currentJob != null) {
            // There is a job in execution
            // Compute remaining time
            currentJob.setRemainingTime(nextCompletion - t);
            if (currentJob.getRemainingTime() == 0) {
                // if job completed, add it to the schedule and set nextCompletion to INFINITE
                schedule.addJob(currentJob, t - currentJob.getProcessingTime(), t);
                currentJob = null;
                nextCompletion = Integer.MAX_VALUE;

                // Schedule job with minimum remaining time
                // and update next completion
                Job job = getMinRemainingTime(releasedJobs);
                if (job != null) {
                    nextCompletion = t + job.getRemainingTime();
                    currentJob = job;
                }
            }
        } else {
            // No job in execution
            // Schedule job with minimum remaining time
            // and update next completion
            Job job = getMinRemainingTime(releasedJobs);
            if (job != null) {
                nextCompletion = t + job.getRemainingTime();
                currentJob = job;
            }
        }
    }
}