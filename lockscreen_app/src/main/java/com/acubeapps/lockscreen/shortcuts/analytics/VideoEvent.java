package com.acubeapps.lockscreen.shortcuts.analytics;

/**
 * Created by netra.shetty on 6/21/16.
 */
class VideoEvent {
    String videoId;
    String tileId;
    long videoDuration;
    long videoLoadTime;
    Session videoSession;


    VideoEvent(String videoId, String tileId, long videoDuration, long videoLoadTime) {
        this.videoId = videoId;
        this.tileId = tileId;
        this.videoDuration = videoDuration;
        this.videoLoadTime = videoLoadTime;
        this.videoSession = new Session();
    }

    public String getTileId() {
        return tileId;
    }

    public long getVideoLoadTime() {
        return videoLoadTime;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public long getVideoViewDuration() {
        return videoSession.getDuration();
    }

    public void stopVideoSession() {
        videoSession.stopSession();
    }

    public void startVideoSession() {
        videoSession.startSession();
    }
}
