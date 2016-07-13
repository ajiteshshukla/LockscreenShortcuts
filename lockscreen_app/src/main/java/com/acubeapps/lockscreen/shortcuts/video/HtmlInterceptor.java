package com.acubeapps.lockscreen.shortcuts.video;

import com.inmobi.oem.thrift.ad.model.TVideoMetadata;

import android.net.Uri;

/**
 * Created by ritwik on 29/06/16.
 */
interface HtmlInterceptor {

    boolean canIntercept(Uri uri);

    boolean canHandleVideo(TVideoMetadata metadata);

    InterceptedResponse intercept(Uri request, byte[] data);

    String getEmbedTag(String videoId, TVideoMetadata metadata, int width, int height);

}
