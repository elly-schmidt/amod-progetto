package branch_and_bound;

/**
 * Enumeration for the state of the parser
 */
public enum ParserState {
    NONE,
    NUMBER_OF_JOBS,
    PROCESSING_TIME_MIN,
    PROCESSING_TIME_MAX,
    JOB_TEXT,
    PROCESSING_TIME_TEXT,
    RELEASE_TIME_TEXT,
    JOB_VALUE,
    PROCESSING_TIME_VALUE,
    RELEASE_TIME_VALUE
}
