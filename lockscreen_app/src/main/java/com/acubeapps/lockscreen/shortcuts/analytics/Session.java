package com.acubeapps.lockscreen.shortcuts.analytics;

import java.util.concurrent.TimeUnit;

/**
 * Created by netra.shetty on 6/21/16.
 */
class Session {
    private long startTime;
    private long endTime;
    private long duration;

    Session() {
        this.duration = 0L;
    }

    public void startSession() {
        this.startTime = System.currentTimeMillis();
    }

    public void stopSession() {
        this.endTime = System.currentTimeMillis();
        long curDuration = this.endTime - this.startTime;
        this.duration += TimeUnit.MILLISECONDS.toSeconds(curDuration);
    }

    public long getDuration() {
        return this.duration;
    }
}
