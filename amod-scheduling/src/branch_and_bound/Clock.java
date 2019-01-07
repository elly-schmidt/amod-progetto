package branch_and_bound;

/**
 * This class contains some methods which allow
 * to measure the performance of the B&B algorithm
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Clock {
    /**
     * The start instant of the measurement
     */
    private long start;

    public Clock() {
        start = 0;
    }

    /**
     * Getter for start
     * @return the start time
     */
    @SuppressWarnings("WeakerAccess")
    public long getStart() {
        return start;
    }

    /**
     * Setter for start
     * @param start the start time
     */
    public void setStart(long start) {
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
    public double getElapsedTime() {
        return System.currentTimeMillis() - getStart();
    }
}