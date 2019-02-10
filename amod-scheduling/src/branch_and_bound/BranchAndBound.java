package branch_and_bound;

import java.util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BranchAndBound {
    /**
     * The reference to the instance to be solved
     */
    public static Instance instance;

    /**
     * The ID of the main thread
     */
    private long mainThreadId;

    /**
     * The best known upper bound
     * This value is updated during the execution of the B&B
     * algorithm whenever a better value (lower upper bound) is found
     */
    private int upperBound;

    /**
     * The best known solution
     * (i.e. a schedule which minimizes the sum of the completion times)
     * This value is updated during the execution of the B&B
     * algorithm whenever a better schedule
     * (which has a lower sum of completion times) is found
     */
    private Solution bestSolution;

    /**
     * Allow to split the computation between several threads
     * in order to reduce the execution time on multi-core processors
     */
    private ExecutorService executor;

    /**
     * Initialize a Branch and Bound algorithm
     */
    public BranchAndBound(Instance i) {
        // The instance to be solved
        instance = i;

        // Calculate upper bounds
        Solution lowerIndexFirstSolution = calculateLowerIndexFirstSchedule();
        Solution lowerReleaseTimeFirstSolution = calculateLowerReleaseTimeFirstSchedule();
        Solution lowerProcessingTimeFirstSolution = calculateLowerProcessingTimeFirstSchedule();
        Solution lowerProcessingTimeReleaseTimeFirstSolution = calculateLowerProcessingTimeLowerReleaseTimeFirstSchedule();

        // Update the best upper bound
        this.bestSolution = null;
        if (updateSolution(lowerIndexFirstSolution)) {
            setUpperBound(lowerIndexFirstSolution.sumOfCompletionTimesForScheduledJobs());
        }
        if (updateSolution(lowerReleaseTimeFirstSolution)) {
            setUpperBound(lowerReleaseTimeFirstSolution.sumOfCompletionTimesForScheduledJobs());
        }
        if (updateSolution(lowerProcessingTimeFirstSolution)) {
            setUpperBound(lowerProcessingTimeFirstSolution.sumOfCompletionTimesForScheduledJobs());
        }
        if (updateSolution(lowerProcessingTimeReleaseTimeFirstSolution)) {
            setUpperBound(lowerProcessingTimeReleaseTimeFirstSolution.sumOfCompletionTimesForScheduledJobs());
        }

        // Get main thread ID
        mainThreadId = Thread.currentThread().getId();

        // Initialize the executor
        // Split the computation between several threads
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Compare two partial solutions considering the lower bound
     */
    class MinLowerBoundFirst implements Comparator<TreeNode> {

        @Override
        public int compare(TreeNode j1, TreeNode j2) {
            return Integer.compare(
                    j1.getLowerBound(),
                    j2.getLowerBound()
            );
        }
    }

    private int scheduleJob(int currentTime, Job j, Solution sol) {
        // Get the release time and the processing time of the job
        int releaseTime = j.getReleaseTime();
        int processingTime = j.getProcessingTime();

        // Compute the completion time for the job
        int completionTime;
        if (currentTime <= releaseTime) {
            // The job has not yet been released at the current time
            // will be scheduled at its release time
            // and will be completed after processing time
            completionTime = releaseTime + processingTime;
            sol.processAndScheduleJob(j.getId(), releaseTime);
        } else {
            // The job has been released at the current time,
            // will be scheduled at the current time
            // and will be completed after processing time
            completionTime = currentTime + processingTime;
            sol.processAndScheduleJob(j.getId(), currentTime);
        }
        return completionTime;
    }

    /**
     * Schedule the jobs in the order given by the instance without preemption
     * The sum of the completion times is an upper bound for the instance
     * @return the computed upper bound
     */
    private Solution calculateLowerIndexFirstSchedule() {
        // The current instant
        int currentTime = 0;

        Solution solution = new Solution(instance);
        // Schedule the jobs
        for (Job j : instance.getJobs()) {
            // Schedule the job and update the current instant
            currentTime = scheduleJob(currentTime, j, solution);
        }
        // Return the result
        return solution;
    }

    /**
     * Schedule the jobs in the order given by the release time without preemption
     * The sum of the completion times is an upper bound for the instance
     * @return the computed upper bound
     */
    private Solution calculateLowerReleaseTimeFirstSchedule() {
        // The current instant
        int currentTime = 0;

        Solution solution = new Solution(instance);
        // Schedule the jobs
        PriorityQueue<Integer> sortedJobs = instance.getJobsSortedByReleaseTime();
        while (!sortedJobs.isEmpty()) {
            // Get the job with the lowest release time
            int jobId = sortedJobs.poll();
            Job j = instance.getJob(jobId);

            // Schedule the job and update the current instant
            currentTime = scheduleJob(currentTime, j, solution);
        }
        // Return the result
        return solution;
    }

    /**
     * Schedule the jobs in the order given by the processing time without preemption
     * The sum of the completion times is an upper bound for the instance
     * @return the computed upper bound
     */
    private Solution calculateLowerProcessingTimeFirstSchedule() {
        // The current instant
        int currentTime = 0;

        Solution solution = new Solution(instance);
        // Schedule the jobs
        PriorityQueue<Integer> sortedJobs = instance.getJobsSortedByProcessingTime();
        while (!sortedJobs.isEmpty()) {
            // Get the job with the lowest processing time
            int jobId = sortedJobs.poll();
            Job j = instance.getJob(jobId);

            // Schedule the job and update the current instant
            currentTime = scheduleJob(currentTime, j, solution);
        }
        // Return the result
        return solution;
    }

    /**
     * Compare two jobs considering the release time
     */
    class MinProcessingTimeFirst implements Comparator<Integer> {

        @Override
        public int compare(Integer j1, Integer j2) {
            return Integer.compare(instance.getJob(j1).getProcessingTime(), instance.getJob(j2).getProcessingTime());
        }
    }


    /**
     * Schedule the jobs in the order given by the processing time without preemption
     * The sum of the completion times is an upper bound for the instance
     * @return the computed upper bound
     */
    private Solution calculateLowerProcessingTimeLowerReleaseTimeFirstSchedule() {
        // The current instant
        int currentTime = 0;

        Solution solution = new Solution(instance);
        // Schedule the jobs
        PriorityQueue<Integer> sortedJobs = instance.getJobsSortedByReleaseTime();
        PriorityQueue<Integer> releasedJobs = new PriorityQueue<>(new MinProcessingTimeFirst());
        do {
            if (!sortedJobs.isEmpty()) {
                int jobId = sortedJobs.peek();
                Job j = instance.getJob(jobId);
                currentTime = Math.max(j.getReleaseTime(), currentTime);
            }
            while (!sortedJobs.isEmpty()) {
                // Get the job with the lowest release time
                int jobId = sortedJobs.peek();
                Job j = instance.getJob(jobId);
                if (j.getReleaseTime() <= currentTime) {
                    // Job released
                    releasedJobs.add(jobId);
                    sortedJobs.poll();
                } else {
                    break;
                }
            }

            while (!releasedJobs.isEmpty()) {
                // Get the job with the lowest release time
                int jobId = releasedJobs.poll();
                Job j = instance.getJob(jobId);

                // Schedule the job and update the current instant
                currentTime = scheduleJob(currentTime, j, solution);
            }
        } while(!sortedJobs.isEmpty());

        // Return the result
        return solution;
    }

    /**
     * Execute the Branch and Bound algorithm
     * We don't build the whole enumeration tree, which is an expensive process
     * We build the nodes of the tree dynamically when needed in order to safe memory
     * In other words we build only the nodes that we have to explore
     */
    public void execute() {
        // Create the root of the B&B tree
        // The root is at level 0 of the tree (k=0)
        int k = 0;
        TreeNode root = new TreeNode(instance, k);

        // The root needs to be explored: branch
        branch(root);

        // Main thread has completed its work
        // Before termination we have to wait for other threads termination
        executor.shutdown();
        boolean isWait = true;
        while (isWait) {
            try {
                isWait = !executor.awaitTermination(30, TimeUnit.MINUTES);
                if (isWait) {
                    System.out.println("Awaiting completion of bulk callback threads.");
                }
            } catch (InterruptedException e) {
                System.out.println("Interruped while awaiting completion of callback threads - trying again...");
            }
        }
        // All tasks completed, terminate the algorithm
        System.out.println("Finished all threads");
    }

    /**
     * Branch the node
     * and mark the promising node as active in order
     * to explore them in the next step of the algorithm
     * Update the current upper bound and the current solution if better values are found
     * @param node the node
     */
    private void branch(TreeNode node) {

        // Priority queue containing the active nodes
        // During the execution we discard the nodes which
        // don't minimize the sum of the completion times
        // and we mark as active the promising nodes
        // which has to be explored by the next steps
        // of the B&B algorithm
        @SuppressWarnings("UnstableApiUsage")
        com.google.common.collect.MinMaxPriorityQueue<TreeNode> activeNodes = com.google.common.collect.MinMaxPriorityQueue
                .orderedBy(new MinLowerBoundFirst())
                .expectedSize(instance.getNumberOfJobs() - node.getK())
                .create();

        int startInstant;
        // Mark as active all the promising nodes
        for (int jobId = 1; jobId <= node.getPartialSolution().numberOfJobs(); jobId++) {
            if (node.getPartialSolution().isScheduled(jobId)) {
                // The job is already scheduled in the partial solution
                continue;
            }

            if (instance.getJob(jobId).getReleaseTime() > node.getPartialSolution().makeSpan()) {
                // The job has not yet been released
                // The start instant is the release time
                startInstant = instance.getJob(jobId).getReleaseTime();
            } else {
                // The job has been released
                // The start instant is the completion time of the current schedule
                startInstant = node.getPartialSolution().makeSpan();
            }
            // Create a new tree node which consider the current partial schedule + the new job
            // Increase k because the node is located at a lower level in the enumeration tree
            int k = node.getK() + 1;
            TreeNode child = new TreeNode(instance, node.getPartialSolution(), k);

            // Schedule the new job
            child.getPartialSolution().processAndScheduleJob(jobId, startInstant);

            // Compute a lower bound for the new node
            child.calculateLowerBound();

            // If the computed schedule is not preemptive
            // the computed lower bound is also an upper bound for the instance
            if (!child.isPreemptive()) {
                // Get the schedule...
                child.getNotPreemptiveSchedule();
                // ...and update upper bound
                if (updateSolution(child.getPartialSolution())) {
                    setUpperBound(child.getLowerBound());
                }
                continue;
            }

            // If the lower bound is greater then the best known upper bound,
            // it does not make sense to explore the new job
            // If the lower bound is less then or equals to the best known upper bound,
            // we mark the node as an active node and we'll explore the node in the future
            // Prune those nodes with higher lower bound than the current upper bound
            if (child.getLowerBound() <= getUpperBound()) {
                // Check pruning condition
                boolean prune = false;
                for (int jobId2 = 1; jobId2 <= node.getPartialSolution().numberOfJobs(); jobId2++) {
                    if (jobId == jobId2)
                        // Skip the job itself
                        continue;
                    if (!node.getPartialSolution().isScheduled(jobId) &&
                            checkPruningCondition(jobId, jobId2, node.getPartialSolution().makeSpan())) {
                        prune = true;
                        break;
                    }
                }

                if (prune) {
                    // If the condition is satisfied for some job
                    // the optimal solution is not on this branch
                    continue;
                }

                // The lower bound is lower than the best known upper bound
                // and the pruning condition is not satisfied
                // The node needs to be explored in the next steps,
                // mark as active
                activeNodes.add(child);
                if (child.getK() == instance.getNumberOfJobs()) {
                    // The node is a leaf of the tree,
                    // the found solution is a non-preemptive solution
                    // and the found lower bound is also an upper bound for the instance
                    if (updateSolution(child.getPartialSolution())) {
                        // Mark the new solution as best known solution,
                        // if it minimizes the sum of the completion times
                        // and update the best known upper bound
                        setUpperBound(child.getLowerBound());
                    }
                    // We explored this leaf node, so we can unmark it
                    activeNodes.remove(child);
                }
            }
        }
        // Explore all the active nodes starting from the most promising,
        // the one with the smallest lower bound
        while (!activeNodes.isEmpty()) {
            // Search for the node with the smallest lower bound...
            TreeNode nodeWithSmallestLB;
            nodeWithSmallestLB = activeNodes.pollFirst();

            // ...and branch it
            if (nodeWithSmallestLB.getLowerBound() < getUpperBound()) {
                if (Thread.currentThread().getId() == mainThreadId && node.getK() == instance.getNumberOfJobs()/4) {
                    // Assign the job to a secondary thread
                    Runnable worker = new WorkerThread(nodeWithSmallestLB);
                    executor.execute(worker);
                } else {
                    // Branch
                    branch(nodeWithSmallestLB);
                }
            }
        }
    }

    /**
     * Update the best solution
     * @param sol the new solution
     * @return true if the solution is updated, false otherwise
     */
    private boolean updateSolution(Solution sol) {
        if (getBestSolution() == null) {
            // We have not yet any solution
            // Actually the new solution is the best solution
            setBestSolution(sol);
            return true;
        } else if (sol.sumOfCompletionTimesForScheduledJobs() < getBestSolution().sumOfCompletionTimesForScheduledJobs()) {
            // The new solution is better then the known solution,
            // update the best solution
            setBestSolution(sol);
            return true;
        }
        // The known solution is better then the new solution
        return false;
    }

    /**
     * Check if the subtree rooted in node j has to be pruned.
     *
     * If releaseTime of job j >= max{ t, releaseTime of job i } + processingTime of job i for each i in otherNodes,
     * then the job i can be done entirely before the job j is released.
     * Therefore the subtree rooted in j can be pruned because it does not lead to an optimal solution.
     * The value t is the time of completion of the sequence of jobs leading to job j.
     * @param jobId first job
     * @param jobId2 second job
     * @param currentInstant currentInstant
     * @return true if the input subtree has to be pruned
     */
    private boolean checkPruningCondition(int jobId, int jobId2, int currentInstant) {
        int releaseTimeJ = instance.getJob(jobId).getReleaseTime();

        int releaseTimeI = instance.getJob(jobId2).getReleaseTime();
        int processingTimeI = instance.getJob(jobId2).getProcessingTime();
        // Pruning condition
        return releaseTimeJ >= Math.max(currentInstant, releaseTimeI) + processingTimeI;
    }

    /* Getters and Setters */

    /**
     * Get the best known upper bound
     * @return the upper bound
     */
    private int getUpperBound() {
        return upperBound;
    }

    /**
     * Update the best known upper bound
     * @param upperBound the upper bound
     */
    private synchronized void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    /**
     * Get the best known solution
     * @return the solution
     */
    public Solution getBestSolution() {
        return bestSolution;
    }

    /**
     * Set the best known solution
     * @param bestSolution the solution
     */
    private synchronized void setBestSolution(Solution bestSolution) {
        this.bestSolution = bestSolution;
    }

    /**
     * Define a worker thread
     */
    public class WorkerThread implements Runnable {

        private TreeNode node;

        private WorkerThread(TreeNode node){
            this.node=node;
        }

        @Override
        public void run() {
            branch(node);
        }
    }

}