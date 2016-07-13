package com.acubeapps.lockscreen.shortcuts;

import com.acubeapps.lockscreen.shortcuts.video.VideoService;
import com.acubeapps.lockscreen.shortcuts.video.VideoStore;
import com.inmobi.oem.thrift.ad.model.TContentSource;
import com.inmobi.oem.thrift.ad.model.TFacebookMetadata;
import com.inmobi.oem.thrift.ad.model.TVideo;
import com.inmobi.oem.thrift.ad.model.TVideoMetadata;

import android.app.Activity;
import android.os.Bundle;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by ritwik on 29/05/16.
 */
public class TestActivity extends Activity {

    @Inject
    VideoService service;

    @Inject
    VideoStore videoStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.acubeapps.lockscreen.shortcuts.R.layout.activity_main);
        Injectors.appComponent().injectTestActivity(this);

        List<TVideo> videos = new ArrayList<>();
        TVideo video = new TVideo("someVideoId123")
                .setTitle("Some Video Title")
                .setAspectHeight(100)
                .setAspectWidth(100)
                .setDuration(2000)
                .setSource(new TContentSource("someSourceId")
                        .setName("Some Source")
                        .setIcon("http://some/icon/url"))
                .setThumbnailUrl("http://some/thumbnail/url");

        TFacebookMetadata fbMeta = new TFacebookMetadata()
                .setId("10152454700553553")
                .setLikeCount(1000);
        TVideoMetadata meta = new TVideoMetadata();
        meta.setFacebookMetadata(fbMeta);
        video.setVideoMetadata(meta);

        videos.add(video);

        video = video.deepCopy();
        video.setId("someVideoId456");
        video.getVideoMetadata().getFacebookMetadata().setId("1763987703853871");
        videos.add(video);


        service.getVideoUrls(videos, new VideoService.Callback() {
            @Override
            public void onVideo(String videoId, String url) {
                Timber.i("[%s] Extracted url : %s, %s", Thread.currentThread().getName(), videoId, url);
            }
        }, true);
    }
}
