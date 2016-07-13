package com.acubeapps.lockscreen.shortcuts.video;

/**
 * Created by ritwik on 29/06/16.
 */
public interface VideoStore {

    String getVideoUrl(String videoId);

    long getDownloadId(String videoId);

    String getVideoId(long downloadId);

    String getDownloadedPreviewUri(String videoId);

    void addVideoUrl(String videoId, String videoUrl, long expiresAt);

    void setDownloadId(String videoId, long downloadId);

    void setDownloadedPreviewUri(String videoId, String downloadedPreviewUri);

    void removeVideo(String videoId);

    void apply();
}
