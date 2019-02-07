import branch_and_bound.BranchAndBound;
import branch_and_bound.Dataset;
import branch_and_bound.Instance;
import branch_and_bound.Stopwatch;

public class Main {

    /**
     * Entry point for Java Application
     */
    public static void main(String[] args) {
        System.out.println("Application started");

        // Build dataset from an input file
        Dataset dataset = new Dataset();
        dataset.buildDatasetFromXlsxFile("dataset - Copia\\instances.xlsx");

        // Create a new stopwatch
        Stopwatch stopwatch = new Stopwatch();

        // For each instance, run the branch and bound algorithm
        for (Instance instance : dataset.getInstances()) {
            System.out.println("****************************** Instance " + instance.getName() + " ******************************");
            // Start the stopwatch
            stopwatch.start();
            // Run the branch and bound algorithm on the instance
            BranchAndBound bb = new BranchAndBound(instance);
            bb.execute();
            // Get the elapsed time
            String elapsedTime = stopwatch.prettyPrintElapsedTime();
            // Print the results
            System.out.println("Soluzione del problema");
            System.out.println(bb.getBestSolution());
            System.out.println(elapsedTime);
            System.out.println("*************************************************************************************************");
            System.out.println();
            System.out.println();
        }
    }
}
