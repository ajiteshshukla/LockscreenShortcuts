package com.acubeapps.lockscreen.shortcuts.video;

import com.acubeapps.lockscreen.shortcuts.utils.Utils;
import com.inmobi.oem.thrift.ad.model.TFacebookMetadata;
import com.inmobi.oem.thrift.ad.model.TVideoMetadata;

import android.net.Uri;
import timber.log.Timber;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ritwik on 29/06/16.
 */
public class FacebookHtmlInterceptor implements HtmlInterceptor {

    private static volatile String interceptJs;
    private ConcurrentMap<String, String> urlToVideoId = new ConcurrentHashMap<>();

    @Override
    public boolean canIntercept(Uri uri) {
        try {
            if (!uri.getPath().startsWith("/video/embed")) {
                return false;
            }

            if (!uri.getHost().equalsIgnoreCase("www.facebook.com")) {
                return false;
            }

            return true;

        } catch (Exception e) {
            Timber.e("Exception encountered while intercepting uri");
            return false;
        }
    }

    @Override
    public boolean canHandleVideo(TVideoMetadata metadata) {
        return metadata.isSetFacebookMetadata();
    }

    @Override
    public InterceptedResponse intercept(Uri request, byte[] data) {
        String videoId = urlToVideoId.get(request.toString());
        if (videoId == null) {
            return null;
        }
        String html = new String(data);
        html += "<script>var __intercept_videoId = '" + videoId + "';</script>";
        html += getInterceptJs();
        return new InterceptedResponse("text/html", html.getBytes(), Collections.EMPTY_MAP);
    }

    @Override
    public String getEmbedTag(String videoId, TVideoMetadata metadata, int width, int height) {
        TFacebookMetadata fbMeta = metadata.getFacebookMetadata();
        String url = "https://www.facebook.com/video/embed?video_id=" + fbMeta.getId();
        urlToVideoId.put(url, videoId);
        return "<iframe src=\"" + url + "\" width=\"" + width
                + "\" height=\"" + height + "\" frameborder=\"0\"></iframe>";
    }

    public String getInterceptJs() {
        if (interceptJs == null) {
            synchronized (this) {
                if (interceptJs == null) {
                    interceptJs = "<script>" + Utils.loadResource("/intercept_fb_embed.js") + "</script>";
                }
            }
        }
        return interceptJs;
    }
}
