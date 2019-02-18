import branch_and_bound.BranchAndBound;
import branch_and_bound.Dataset;
import branch_and_bound.Instance;
import branch_and_bound.Stopwatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    /**
     * Entry point for Java Application
     */
    public static void main(String[] args) {
        System.out.println("Application started");

        if (args == null || args.length != 3) {
            System.err.println("Usage: ...");
            System.exit(-1);
        }

        int numThreads = Integer.parseInt(args[0]);
        if (numThreads < 0) {
            System.err.println("Invalid argument numThreads");
            System.exit(-1);
        }
        String datasetPath = args[1];
        int timeout = Integer.parseInt(args[2]);

        // Build dataset from an input file
        Dataset dataset = new Dataset();
        dataset.buildDatasetFromXlsxFile(datasetPath);
        //dataset.buildDatasetFromXlsxFile("dataset - Copia\\instances.xlsx");
        //dataset.buildDatasetFromXlsxFile("C:\\Users\\carmi\\IdeaProjects\\amod-progetto\\ampl\\i - java.xlsx");
        //dataset.buildDatasetFromXlsxFile("dataset - Copia\\test.xlsx");

        // Create a new stopwatch
        Stopwatch stopwatch = new Stopwatch();

        // For each instance, run the branch and bound algorithm
        for (Instance instance : dataset.getInstances()) {
            System.out.println("****************************** Instance " + instance.getName() + " ******************************");
            // Start the stopwatch
            stopwatch.start();

            ExecutorService executor;

            if (numThreads == 0) {
                executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            } else {
                executor = Executors.newFixedThreadPool(numThreads);;
            }


            // Run the branch and bound algorithm on the instance
            BranchAndBound bb = new BranchAndBound(instance, executor);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    bb.execute();
                }
            });
            executor.execute(t);


            executor.shutdown();
            boolean terminated;
            try {
                terminated = executor.awaitTermination(timeout, TimeUnit.SECONDS);
                if (!terminated) {
                    System.out.println("Interrupted");
                    System.out.println("Best lower bound");
                    System.out.println(bb.getBestLowerBound());
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.out.println("Interruped while awaiting completion of callback threads");
            }
            // All tasks completed, terminate the algorithm
            System.out.println("Finished all threads");

            System.out.printf("Pruned: %d\n", bb.getCountPruned());
            System.out.printf("Found preemptive: %d\n\n", bb.getCountFoundPreemptive());

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
