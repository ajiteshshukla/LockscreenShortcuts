package com.acubeapps.lockscreen.shortcuts.video;

import com.inmobi.oem.thrift.ad.model.TVideo;

import android.content.Context;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ritwik on 29/06/16.
 */
public class VideoService {

    private final VideoStore videoStore;

    private final Context context;

    private final HtmlVideoExtractor htmlVideoExtractor;
    private final ExecutorService backgroundPool;

    public VideoService(Context context, VideoStore videoStore, ExecutorService backgroundPool) {
        this.videoStore = videoStore;
        this.context = context;
        htmlVideoExtractor = new HtmlVideoExtractor(context);
        this.backgroundPool = backgroundPool;
    }

    public void start() {
    }

    public void getVideoUrls(List<TVideo> videos, final Callback callback) {
        getVideoUrls(videos, callback, false);
    }

    public void getVideoUrls(List<TVideo> videos, final Callback callback, boolean force) {
        List<TVideo> pending = new ArrayList<>(videos.size());
        final Map<String, String> urlMap = new HashMap<>();

        if (force) {
            pending.addAll(videos);
        } else {
            for (final TVideo video : videos) {
                String url = videoStore.getVideoUrl(video.getId());
                if (url == null) {
                    if (video.getVideoMetadata().isSetHostedMetadata()) {
                        url = video.getVideoMetadata().getHostedMetadata().getUrl();
                    } else {
                        pending.add(video);
                        continue;
                    }
                }
                urlMap.put(video.getId(), url);
            }
        }

        if (!urlMap.isEmpty()) {
            backgroundPool.submit(new Runnable() {
                @Override
                public void run() {
                    for (Map.Entry<String, String> entry : urlMap.entrySet()) {
                        try {
                            callback.onVideo(entry.getKey(), entry.getValue());
                        } catch (Exception e) {
                            Timber.e(e, "Exception occurred while calling callback");
                        }
                    }
                }
            });
        }

        if (!pending.isEmpty()) {
            htmlVideoExtractor.loadVideos(pending, new HtmlVideoExtractor.Callback() {
                @Override
                public void onVideo(final String videoId, final String url) {
                    long expiresAt = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2);
                    videoStore.addVideoUrl(videoId, url, expiresAt);
                    videoStore.apply();

                    callback.onVideo(videoId, url);

                    backgroundPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                callback.onVideo(videoId, url);
                            } catch (Exception e) {
                                Timber.e(e, "Exception occurred while calling callback");
                            }
                        }
                    });
                }
            });
        }
    }

    public interface Callback {

        void onVideo(String videoId, String url);

    }
}
