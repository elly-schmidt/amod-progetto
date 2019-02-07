package branch_and_bound;

/* Apache POI dependencies */
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* Java dependencies */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class represents a dataset, which is a collection of instances
 */
public class Dataset {
    /**
     * The number of the instances
     */
    private int numberOfInstances;

    /**
     * Collection of instances
     */
    private Instance[] instances;

    /**
     * Default constructor
     */
    public Dataset() {
        this.numberOfInstances = 0;
    }

    /**
     * Build a dataset from xlsx file: check the documentation
     * for a detailed explanation of instance file format
     */
    public void buildDatasetFromXlsxFile(String filename) {
        FileInputStream excelFile;
        Workbook workbook;
        try {
            // Get excel file containing dataset
            excelFile = new FileInputStream(new File(filename));
            workbook = new XSSFWorkbook(excelFile);

            // Get number of sheets
            this.numberOfInstances = workbook.getNumberOfSheets();
            this.instances = new Instance[this.numberOfInstances];

            // Parse xlsx file and build dataset
            for (int i = 0; i < numberOfInstances; i++) {
                // Create a new instance and add it to the dataset
                Instance instance = null;
                // Get the sheet associated to the instance
                Sheet sheet = workbook.getSheetAt(i);
                String name = sheet.getSheetName();

                ParserState currentState = ParserState.NONE;
                // Iterate on the rows of the sheet
                for (Row currentRow : sheet) {
                    // Create a new job and fill its fields
                    Job job = new Job();

                    // Iterate on each cell of the selected row
                    for (Cell currentCell : currentRow) {
                        if (currentCell.getCellType() == CellType.STRING) {
                            switch (currentCell.getStringCellValue()) {
                                case "n =":
                                    currentState = ParserState.NUMBER_OF_JOBS;
                                    break;
                                case "pmin":
                                    currentState = ParserState.PROCESSING_TIME_MIN;
                                    break;
                                case "pmax":
                                    currentState = ParserState.PROCESSING_TIME_MAX;
                                    break;
                                case "job":
                                    currentState = ParserState.PROCESSING_TIME_TEXT;
                                    break;
                                case "processing t":
                                    currentState = ParserState.RELEASE_TIME_TEXT;
                                    break;
                                case "release t":
                                    currentState = ParserState.JOB_VALUE;
                                    break;
                                default:
                                    currentState = ParserState.NONE;
                                    break;
                            }
                        } else if (currentCell.getCellType() == CellType.NUMERIC) {
                            switch (currentState) {
                                case NUMBER_OF_JOBS:
                                    instance = new Instance();
                                    instance.setName(name);
                                    this.instances[i] = instance;
                                    instance.setNumberOfJobs((int) currentCell.getNumericCellValue());
                                    break;
                                case PROCESSING_TIME_MIN:
                                    instance.setProcessingTimeMin((int) currentCell.getNumericCellValue());
                                    break;
                                case PROCESSING_TIME_MAX:
                                    instance.setProcessingTimeMax((int) currentCell.getNumericCellValue());
                                    break;
                                case PROCESSING_TIME_TEXT:
                                    System.out.println("Inconsistent file");
                                    break;
                                case RELEASE_TIME_TEXT:
                                    System.out.println("Inconsistent file");
                                    break;
                                case JOB_VALUE:
                                    job = new Job();
                                    job.setId((int) currentCell.getNumericCellValue());
                                    currentState = ParserState.PROCESSING_TIME_VALUE;
                                    break;
                                case PROCESSING_TIME_VALUE:
                                    job.setProcessingTime((int) currentCell.getNumericCellValue());
                                    currentState = ParserState.RELEASE_TIME_VALUE;
                                    break;
                                case RELEASE_TIME_VALUE:
                                    job.setReleaseTime((int) currentCell.getNumericCellValue());
                                    currentState = ParserState.JOB_VALUE;
                                    instance.addJob(job);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    // Consistency check
                    assert(instance.getNumberOfJobs() == instance.getJobs().length);
                }

                // Print on console
                instance.printInstance();

                // Print on a file
                String outFilename = "dataset\\instances-" + instance.getName() + ".txt";
                instance.printInstance(outFilename);

                // Consistency check
                assert(this.numberOfInstances == instances.length);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the instances contained in the dataset
     * @return the list of the instances
     */
    public Instance[] getInstances() {
        return instances;
    }

}
