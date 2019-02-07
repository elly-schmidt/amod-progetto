package branch_and_bound;

/**
 * This class contains some methods which allow
 * to measure the performance of the B&B algorithm
 */
public class Stopwatch {
    /**
     * The start instant of the measurement
     */
    private long start;

    public Stopwatch() {
        start = 0;
    }

    /**
     * Getter for start
     * @return the start time
     */
    private long getStart() {
        return start;
    }

    /**
     * Setter for start
     * @param start the start time
     */
    private void setStart(long start) {
        this.start = start;
    }

    /**
     * Start the stopwatch
     */
    public void start() {
        setStart(System.currentTimeMillis());
    }

    /** Get the elapsed time
     * @return the elapsed time
     */
    private double getElapsedTime() {
        return System.currentTimeMillis() - getStart();
    }

    public String prettyPrintElapsedTime() {
        double elapsed = getElapsedTime();
        int seconds = (int) Math.floor(elapsed / 1000);
        int milliseconds = (int) elapsed % 1000;
        int minutes = (int) Math.floor((double)seconds / 60);
        seconds = seconds % 60;
        int hours = (int) Math.floor((double)minutes / 60);
        minutes = minutes % 60;

        return "Time in milliseconds: " + elapsed + "\nElapsed: " + hours + ":" + minutes + ":" + seconds + ":" + milliseconds;
    }
}