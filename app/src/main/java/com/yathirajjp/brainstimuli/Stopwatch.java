package com.yathirajjp.brainstimuli;

import java.util.Locale;

/**
 * Created by yathirajjp on 26/12/17.
 */

public class Stopwatch {
    private long startTime = 0;
    private boolean running = false;
    private long currentTime = 0;

    public void start() {
        running = true;
        this.startTime = System.currentTimeMillis();
    }

    //Method to start with earlier time
    public void start(long timeDiff) {
        running = true;
        this.startTime = System.currentTimeMillis() - timeDiff;
    }

    public void stop() {
        this.running = false;
    }

    public void pause() {
        this.running = false;
        currentTime = System.currentTimeMillis() - startTime;
    }
    public void resume() {
        this.running = true;
        this.startTime = System.currentTimeMillis() - currentTime;
    }

    //elaspsed time in milliseconds
    public long getElapsedTimeMili() {
        long elapsed = 0;
        if (running) {
            elapsed =((System.currentTimeMillis() - startTime)/100) % 1000 ;
        }
        return elapsed;
    }

    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
        long elapsed = 0;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        }
        return elapsed;
    }

    //elaspsed time in minutes
    public long getElapsedTimeMin() {
        long elapsed = 0;
        if (running) {
            elapsed = (((System.currentTimeMillis() - startTime) / 1000) / 60 ) % 60;
        }
        return elapsed;
    }

    //elaspsed time in hours
    public long getElapsedTimeHour() {
        long elapsed = 0;
        if (running) {
            elapsed = ((((System.currentTimeMillis() - startTime) / 1000) / 60 ) / 60);
        }
        return elapsed;
    }

    // Text format(hh:mm:ss.sss) of elapsed time
    public String toString() {

        return String.format(Locale.getDefault(), "%02d", getElapsedTimeHour()) +
                ":" + String.format(Locale.getDefault(), "%02d", getElapsedTimeMin()) +
                ":" + String.format(Locale.getDefault(), "%02d", getElapsedTimeSecs()) +
                "." + String.format(Locale.getDefault(), "%03d", getElapsedTimeMili());

    }

    // Text format(hh:mm:ss) of elapsed time
    public String toStringHMS() {

        return String.format(Locale.getDefault(), "%02d", getElapsedTimeHour()) +
                ":" + String.format(Locale.getDefault(), "%02d", getElapsedTimeMin()) +
                ":" + String.format(Locale.getDefault(), "%02d", getElapsedTimeSecs());

    }
}
