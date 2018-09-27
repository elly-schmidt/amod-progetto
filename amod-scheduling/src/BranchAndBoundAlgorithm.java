import java.util.HashSet;
import java.util.Set;

public class BranchAndBoundAlgorithm {

    private float upperBound;
    private Set<Integer>[] partialSequence;
    private Set<Integer>[] otherNodes;
    private Set<Integer> nodes;

    public BranchAndBoundAlgorithm(Instance instance) {
        init(instance);
    }


    private void init(Instance instance) {
        // Compute upper bound
        this.upperBound = computeUpperBound();

        for (Job j : instance.getJobs()) {
            this.partialSequence[j.getId()] = new HashSet<>();
            this.otherNodes[j.getId()] = new HashSet<>();
            nodes.add(j.getId());
        }
    }

    public void branchAndBound(Set<Integer> partialSequence, Set<Integer> otherNodes) {
        for (Integer node : nodes) {
            // Check pruning condition
            if (checkPruningCondition()) {
                return;
            }

            partialSequence.add(node);
            otherNodes = nodes;
            otherNodes.removeAll(partialSequence);

            // Compute a lower bound
            float lowerBound = computeLowerBound();

            if (lowerBound < upperBound) {
                // Foreach child, call branch and bound algorithm
                branchAndBound(partialSequence, otherNodes);
            } else {
                // Pruning
                return;
            }

        }
    }

    private float computeLowerBound() {
        // TODO Compute Lower Bound
        return 0;
    }

    private float computeUpperBound() {
        // TODO Compute Upper Bound
        return Float.POSITIVE_INFINITY;
    }

    private boolean checkPruningCondition() {
        // TODO Check pruning condition
        return false;
    }

}
