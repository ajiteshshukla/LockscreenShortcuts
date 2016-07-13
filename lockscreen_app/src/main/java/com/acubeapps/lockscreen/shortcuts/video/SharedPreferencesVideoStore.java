package com.acubeapps.lockscreen.shortcuts.video;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ritwik on 29/06/16.
 */
public class SharedPreferencesVideoStore implements VideoStore {

    private static final Gson GSON = new GsonBuilder().create();
    private static final String VIDEO_STORE_KEY = "videoStore";
    private final SharedPreferences sharedPreferences;
    private VideoData videoData;
    private final Lock lock = new ReentrantLock();

    public SharedPreferencesVideoStore(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        String data = sharedPreferences.getString(VIDEO_STORE_KEY, null);
        if (data == null) {
            videoData = new VideoData();
        } else {
            videoData = GSON.fromJson(data, VideoData.class);
        }
    }

    @Override
    public String getVideoUrl(String videoId) {
        VideoEntry entry = videoData.videos.get(videoId);
        if (entry == null) {
            return null;
        }
        if (entry.expiresAt <= System.currentTimeMillis()) {
            return null;
        }
        return entry.url;
    }

    @Override
    public long getDownloadId(String videoId) {
        if (videoData.videos.get(videoId) != null) {
            return videoData.videos.get(videoId).downloadId;
        }
        return 0;
    }

    @Override
    public String getVideoId(long downloadId) {
        Map<String, VideoEntry> videoEntryMap = videoData.videos;
        for (String videoId : videoEntryMap.keySet()) {
            VideoEntry videoEntry = videoEntryMap.get(videoId);
            if (videoEntry.downloadId == downloadId) {
                return videoId;
            }
        }
        return null;
    }

    @Override
    public void setDownloadId(String videoId, long downloadId) {
        try {
            lock.lock();
            VideoEntry videoEntry = videoData.videos.get(videoId);
            if (videoEntry == null) {
                videoEntry = new VideoEntry(videoId);
            }
            videoEntry.setDownloadId(downloadId);
            videoData.videos.put(videoId, videoEntry);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addVideoUrl(String videoId, String videoUrl, long expiresAt) {
        try {
            lock.lock();
            VideoEntry videoEntry = videoData.videos.get(videoId);
            if (videoEntry == null) {
                videoEntry = new VideoEntry(videoId);
            }
            videoEntry.setUrl(videoUrl);
            videoEntry.setExpiresAt(expiresAt);
            videoData.videos.put(videoId, videoEntry);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setDownloadedPreviewUri(String videoId, String downloadedPreviewUri) {
        try {
            lock.lock();
            VideoEntry videoEntry = videoData.videos.get(videoId);
            if (videoEntry == null) {
                videoEntry = new VideoEntry(videoId);
            }
            videoEntry.setDownloadedPreviewUri(downloadedPreviewUri);
            videoData.videos.put(videoId, videoEntry);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public String getDownloadedPreviewUri(String videoId) {
        if (videoData.videos.get(videoId) != null) {
            return videoData.videos.get(videoId).downloadedPreviewUri;
        }
        return null;
    }

    @Override
    public void removeVideo(String videoId) {
        try {
            lock.lock();
            videoData.videos.remove(videoId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void apply() {
        try {
            lock.lock();
            String data = GSON.toJson(videoData);
            sharedPreferences.edit()
                    .putString(VIDEO_STORE_KEY, data)
                    .apply();
        } finally {
            lock.unlock();
        }
    }

    static class VideoData {

        private Map<String, VideoEntry> videos = new HashMap<>();

    }

    static class VideoEntry {

        final String id;

        String url;
        long expiresAt;
        long downloadId;
        String downloadedPreviewUri;

        public VideoEntry(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            VideoEntry that = (VideoEntry) obj;
            return expiresAt == that.expiresAt
                    && Objects.equals(id, that.id)
                    && Objects.equals(url, that.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, url, expiresAt);
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setExpiresAt(long expiresAt) {
            this.expiresAt = expiresAt;
        }


        public void setDownloadId(long downloadId) {
            this.downloadId = downloadId;
        }


        public void setDownloadedPreviewUri(String downloadedPreviewUri) {
            this.downloadedPreviewUri = downloadedPreviewUri;
        }
    }
}
