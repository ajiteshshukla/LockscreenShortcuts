package com.acubeapps.lockscreen.shortcuts.video;

import android.support.annotation.NonNull;
import android.webkit.WebResourceResponse;

import java.io.ByteArrayInputStream;
import java.util.Map;

class InterceptedResponse {
    final byte[] data;
    final String mimeType;
    final Map<String, String> headers;

    public InterceptedResponse(@NonNull String mimeType, @NonNull byte[] data, Map<String, String> headers) {
        this.data = data;
        this.mimeType = mimeType;
        this.headers = headers;
    }

    public WebResourceResponse toWebResource() {
        return new WebResourceResponse(mimeType, "UTF-8", 200, "OK", headers, new ByteArrayInputStream(data));
    }
}
