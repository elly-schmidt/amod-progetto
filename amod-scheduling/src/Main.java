public class Main {

    /* Entry point for Java Application */
    public static void main(String[] args) {
        System.out.println("Application started");

        /* Read input file and build dataset */
        Dataset dataset = new Dataset();
        dataset.buildDatasetFromXlsxFile("dataset\\instances.xlsx");

        /* Run the scheduling algorithm on dataset*/


        /* Print results */
    }
}
