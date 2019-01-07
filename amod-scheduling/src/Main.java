import branch_and_bound.BranchAndBound;
import branch_and_bound.Dataset;
import branch_and_bound.Instance;

public class Main {

    /**
     * Entry point for Java Application
     */
    public static void main(String[] args) {
        System.out.println("Application started");

        // Read input file and build dataset
        Dataset dataset = new Dataset();
        //dataset.buildDatasetFromXlsxFile("dataset\\instances.xlsx");
        dataset.buildDatasetFromXlsxFile("dataset - Copia\\instances.xlsx");
        //dataset.buildDatasetFromXlsxFile("ampl\\i.xlsx");

        // Run the scheduling algorithm on dataset
        // Print results
        for (Instance instance : dataset.getInstances()) {
            System.out.println("****************************** Instance " + instance.getName() + " ******************************");
            BranchAndBound bb = new BranchAndBound(instance);
            bb.execute();
            System.out.println("Soluzione del problema");
            System.out.println(bb.getBestSolution());
            System.out.println("*************************************************************************************************");
            System.out.println();
            System.out.println();
        }
    }
}
