package branch_and_bound;

import java.util.ArrayList;
import java.util.function.Predicate;

public class BranchAndBound {
    /**
     * The instance to be solved
     */
    private final Instance instance;

    /**
     * ArrayList containing the active nodes
     * During the execution we discard the nodes which
     * don't minimize the sum of the completion times
     * and we mark as active the promising nodes
     * which has to be explored by the next steps
     * of the B&B algorithm
     */
    private ArrayList<TreeNode> activeNodes;

    /**
     * The best known upper bound
     * This value is updated during the execution of the B&B
     * algorithm whenever a better value (lower) is found
     */
    private int upperBound;						// Upper bound

    /**
     * The best known solution
     * (i.e. a schedule which minimizes the sum of the completion times)
     * This value is updated during the execution of the B&B
     * algorithm whenever a better schedule
     * (which has a lower sum of completion times) is found
     */
    private Solution bestSolution;				// Best solution


    /**
     * Initialize a Branch and Bound algorithm
     * @param instance the instance to be solved
     */
    public BranchAndBound(Instance instance) {
        this.instance = instance;
        this.activeNodes = new ArrayList<>();
        this.upperBound = calculateUpperBound();
        this.bestSolution = null;
    }

    /**
     * Compute an upper bound
     * Schedule the jobs in the order given by the instance without preemption
     * The sum of the completion times is an upper bound for the instance
     * @return the computed upper bound
     */
    private int calculateUpperBound() {
        int upperBound = 0;     // The computed upper bound
        int currentTime = 0;    // The current instant

        // Schedule each job and update the sum of the completion times
        for (Job j : getJobs()) {
            // For each job
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
            } else {
                // The job has been released at the current time,
                // will be scheduled at the current time
                // and will be completed after processing time
                completionTime = currentTime + processingTime;
            }

            // Update the sum of the completion times (i.e. the upper bound)
            upperBound += completionTime;
            // Update the current instant
            currentTime = completionTime;
        }
        // Return the result
        return upperBound;
    }

    /**
     * Execute the Branch and Bound algorithm
     * We don't build the whole enumeration tree, which is an expensive process
     * We build the nodes of the tree dynamically when needed in order to safe memory
     * In other words we build only the nodes that we have to explore
     */
    public void execute() {
        // Create the root of the B&B tree
        TreeNode root = new TreeNode(getInstance());
        // The root is at level 0 of the tree
        root.setK(0);
        // The root has to be explored: mark as active
        getActiveNodes().add(root);

        // Explore all the active nodes starting from the most promising,
        // the one with the smallest lower bound
        while (getActiveNodes().size() != 0) {
            // Search for the node with the smallest lower bound...
            int minBound = Constants.INFINITY;
            TreeNode nodeWithSmallestLB = null;
            for (int i = 0; i < getActiveNodes().size(); i++) {
                if (getActiveNodes().get(i).getLowerBound() < minBound) {
                    minBound = getActiveNodes().get(i).getLowerBound();
                    nodeWithSmallestLB = getActiveNodes().get(i);
                }
            }

            // ...and branch it
            if (nodeWithSmallestLB != null) {
                branch(nodeWithSmallestLB);
            } else {
                // There is at least one active node
                // but we are unable to find a node with the smallest lower bound
                // This sounds like a programming error
                System.err.println("Cannot find the node with the smallest lower bound");
                System.err.println("This is a programming error");
                System.exit(-1);
            }
        }
    }

    /**
     * Branch the node
     * and mark the promising node as active in order
     * to explore them in the next step of the algorithm
     * Update the current upper bound and the current solution if better values are found
     * @param node the node
     */
    private void branch(TreeNode node) {
        // Get the list of the jobs not yet scheduled...
        ArrayList<RunningJob> notScheduledJobs = node.getNotScheduledJobs();
        //...and reset the remaining time and the completion time
        for (RunningJob job : notScheduledJobs) {
            // The job has not yet started,
            // set the remaining time to the processing time
            job.setRemainingTime(job.getProcessingTime());
            // The job is not completed, set completion time to 0
            job.setCompletionTime(0);
        }

        int startInstant;
        // Mark as active all the promising nodes
        for (RunningJob job : notScheduledJobs) {
            if (job.getReleaseTime() > node.getPartialSolution().makeSpan()) {
                // The job has not yet been released
                // Set the start instant to the release time
                startInstant = job.getReleaseTime();
            } else {
                // The job has been released
                // Set the start instant to the completion time of the current schedule
                startInstant = node.getPartialSolution().makeSpan();
            }
            // Create a new tree node which consider the current schedule + the new job
            TreeNode child = new TreeNode(this.instance, node.getPartialSolution());
            // The node is located at a lower level in the enumeration tree
            child.setK(node.getK() + 1);
            // Schedule the new job
            child.getPartialSolution().scheduleJob(job.getId(), startInstant, job.getProcessingTime());
            // Compute a lower bound for the new node
            child.calculateLowerBound();

            // If the lower bound is greater then the best known upper bound,
            // it does not make sense to explore the new job
            // If the lower bound is less then or equals to the best known upper bound,
            // we mark the node as an active node and we'll explore the node in the future
            if (child.getLowerBound() <= getUpperBound() && !checkPruningCondition(job, notScheduledJobs, node.getPartialSolution().makeSpan())) {
                // Mark the node as active
                getActiveNodes().add(child);
                if (child.getK() == getInstance().getNumberOfJobs() - 1) {
                    // The node is a leaf of the tree,
                    // the found solution is a non-preemptive solution
                    // and the found lower bound is also an upper bound for the instance
                    if (updateSolution(child.getPartialSolution())) {
                        // Mark the new solution as best known solution,
                        // if it minimizes the sum of the completion times
                        // and update the best known upper bound
                        setUpperBound(child.getLowerBound());
                        // Prune those nodes with higher lower bound than the current upper bound
                        for (int j = 0; j < getActiveNodes().size(); j++) {
                            Predicate<TreeNode> nodePredicate = n-> n.getLowerBound() > getUpperBound();
                            getActiveNodes().removeIf(nodePredicate);
                        }
                    }
                    // We explored this leaf node, so we can unmark it
                    getActiveNodes().remove(child);
                }
            }
        }

        // We explored this node, so we can unmark it
        getActiveNodes().remove(node);
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
        } else if (sol.sumOfCompletionTimes() < getBestSolution().sumOfCompletionTimes()) {
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
     * @param job job j
     * @param notScheduledJobs not scheduled jobs
     * @param currentInstant currentInstant
     * @return true if the input subtree has to be pruned
     */
    private boolean checkPruningCondition(RunningJob job, ArrayList<RunningJob> notScheduledJobs, int currentInstant) {
        int releaseTimeJ = job.getReleaseTime();

        for (RunningJob i : notScheduledJobs) {
            if (i.getId() == job.getId()) {
                continue;
            }
            int releaseTimeI = i.getReleaseTime();
            int processingTimeI = i.getProcessingTime();
            // Pruning condition
            if (releaseTimeJ >= Math.max(currentInstant, releaseTimeI) + processingTimeI) {
                //System.out.println("pruned");
                return true;
            }
        }
        return false;
    }

    /* Getters and Setters */

    /**
     * Get the instance we are working on
     * @return the instance
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * Get the active nodes list
     * @return the active nodes list
     */
    private ArrayList<TreeNode> getActiveNodes() {
        return activeNodes;
    }

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
    private void setUpperBound(int upperBound) {
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
    private void setBestSolution(Solution bestSolution) {
        this.bestSolution = bestSolution;
    }

    /**
     * Get the jobs of the instance
     * @return the list of the jobs
     */
    public ArrayList<Job> getJobs() {
        return new ArrayList<>(getInstance().getJobs().values());
    }
}