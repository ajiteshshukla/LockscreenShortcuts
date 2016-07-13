package com.acubeapps.lockscreen.shortcuts.analytics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by netra.shetty on 6/21/16.
 */

class MagazineEvent {
    String magazineId;
    List<String> videoIdList;
    HashMap<String, VideoEvent> videoEvents;
    //TileId -> TileViewCount
    HashMap<String, Integer> tileViewCountMap;
    Session magazineSession;

    MagazineEvent(String magazineId) {
        this.magazineId = magazineId;
        this.videoIdList = new ArrayList<>();
        this.videoEvents = new HashMap<String, VideoEvent>();
        this.tileViewCountMap = new HashMap<>();
        this.magazineSession = new Session();
    }


    public void startMagazineSession() {
        magazineSession.startSession();
    }

    public void stopMagazineSession() {
        /*for (VideoEvent videoEvent : videoEvents.values()) {
            videoEvent.stopVideoSession();
        }*/
        magazineSession.stopSession();
    }

    public String getMagazineId() {
        return this.magazineId;
    }

    public long getMagazineViewDuration() {
        return magazineSession.getDuration();
    }

    public void logVideoPresent(String videoId) {
        videoIdList.add(videoId);
    }

    public void addVideoViewedEvent(String videoId, VideoEvent videoEvent) {
        videoEvents.put(videoId, videoEvent);
    }

    public void removeVideoEvent(String videoId) {
        videoEvents.remove(videoId);
    }

    public VideoEvent getVideoEvent(String videoId) {
        return videoEvents.get(videoId);
    }


    public long getVideosViewedCount() {
        return this.videoEvents.size();
    }

    public Collection<String> getVideosViewed() {
        return this.videoEvents.keySet();
    }

    public long getVideosPresentCount() {
        return this.videoIdList.size();
    }

    public Collection<String> getVideosPresent() {
        return this.videoIdList;
    }

    public long getTilesViewedCount() {
        return this.tileViewCountMap.size();
    }

    public Collection<String> getTileIds() {
        return this.tileViewCountMap.keySet();
    }

    public void registerTileView(String tileId) {
        Integer tileViews = tileViewCountMap.get(tileId);
        if (tileViews != null) {
            tileViewCountMap.put(tileId, tileViews + 1);
        } else {
            tileViewCountMap.put(tileId, 1);
        }
    }

}
