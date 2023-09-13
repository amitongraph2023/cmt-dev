package com.panera.cmt.util;

import org.slf4j.Logger;

import java.util.UUID;

public class StopWatch {

    private long startTime;
    private Logger logger = null;
    private String executionName = null;
    private final String executionId = UUID.randomUUID().toString();

    public StopWatch() {
        startTime = System.currentTimeMillis();
    }

    public StopWatch(Logger logger, String executionName, String message) {
        startTime = System.currentTimeMillis();
        this.logger = logger;
        this.executionName = executionName;
        this.logger.info("Beginning StopWatch: executionId={},executionName={}. {}", this.executionId, this.executionName, message);
    }

    /**
     * Gets the elapsed time (in seconds) since the time the object of StopWatch was initialized.
     *
     * @return Elapsed time in seconds.
     */
    public long getElapsedTime() {
        long endTime = System.currentTimeMillis();
        return  endTime - startTime;
    }

    public void checkPoint(String message) {
        this.logger.info("Checkpoint StopWatch: elapsedTime={},executionId={},executionName={}. {}", getElapsedTime(), this.executionId, this.executionName, message);

    }
}
