/* Apache POI dependencies */
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* Java dependencies */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class Dataset {

    private int numberOfInstances;  // Number of instances
    private ArrayList<Instance> instances;  // Collection of instances

    public Dataset() {
        this.numberOfInstances = 0;
        this.instances = new ArrayList<>();
    }

    // Build a dataset from xlsx file: check the documentation for a detailed explaination of instance file format
    public void buildDatasetFromXlsxFile(String filename) {

        FileInputStream excelFile = null;
        Workbook workbook = null;
        try {
            // Get excel file containing dataset
            excelFile = new FileInputStream(new File(filename));
            workbook = new XSSFWorkbook(excelFile);

            // Get number of sheets
            this.numberOfInstances = workbook.getNumberOfSheets();

            // Parse xlsx file and build dataset
            ParserState currentState = ParserState.NONE;
            for (int i = 0; i < numberOfInstances; i++) {
                Instance instance = new Instance();
                this.instances.add(instance);
                Sheet sheet = workbook.getSheetAt(i);
                instance.setName(sheet.getSheetName());

                Iterator<Row> iterator = sheet.iterator();

                while (iterator.hasNext()) {

                    Row currentRow = iterator.next();
                    Iterator<Cell> cellIterator = currentRow.iterator();

                    Job job = new Job();
                    while (cellIterator.hasNext()) {

                        Cell currentCell = cellIterator.next();

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
                                    instance.setNumberOfJobs((int) currentCell.getNumericCellValue());
                                    break;
                                case PROCESSING_TIME_MIN:
                                    instance.setProcessingTimeMin((int) currentCell.getNumericCellValue());
                                    break;
                                case PROCESSING_TIME_MAX:
                                    instance.setProcessingTimeMax((int) currentCell.getNumericCellValue());
                                    break;
                                case JOB_TEXT:
                                    System.out.println("Inonsistent file");
                                    break;
                                case PROCESSING_TIME_TEXT:
                                    System.out.println("Inonsistent file");
                                    break;
                                case RELEASE_TIME_TEXT:
                                    System.out.println("Inonsistent file");
                                    break;
                                case JOB_VALUE:
                                    job = new Job();
                                    job.setId((int) currentCell.getNumericCellValue());
                                    currentState = ParserState.PROCESSING_TIME_VALUE;
                                    break;
                                case PROCESSING_TIME_VALUE:
                                    job.setProcessingTime((int) currentCell.getNumericCellValue());
                                    job.setRemainingTime((int) currentCell.getNumericCellValue());
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

                    assert(instance.getNumberOfJobs() == instance.getJobs().size());
                }

                // Print on console
                instance.printInstance();

                // Print on a file
                String outFilename = "dataset\\instances-" + instance.getName() + ".txt";
                instance.printInstance(outFilename);

                assert(this.numberOfInstances == instances.size());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Instance> getInstances() {
        return instances;
    }
}
