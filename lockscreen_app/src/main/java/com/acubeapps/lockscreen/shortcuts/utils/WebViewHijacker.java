package com.acubeapps.lockscreen.shortcuts.utils;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ritwik on 27/06/16.
 */
public class WebViewHijacker {

    private OkHttpClient client;
    private String hijackedJs;
    private ConcurrentMap<Uri, CachedResponse> cachedJavascripts = new ConcurrentHashMap<>();
    private static final CachedResponse EMPTY_RESPONSE = new CachedResponse(new byte[0], Collections.EMPTY_MAP);

    public WebViewHijacker() {
        client = new OkHttpClient();
        hijackedJs = loadResource("/hijack_fb.js");
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (request.isForMainFrame()) {
            return null;
        }
        Uri uri = request.getUrl();
        if (uri.getPath().endsWith(".js")) {
            if (uri.getHost().equalsIgnoreCase("static.xx.fbcdn.net")) {
                return fetchJavascript(uri);
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private WebResourceResponse fetchJavascript(Uri uri) {
        CachedResponse cachedJs = cachedJavascripts.get(uri);
        if (cachedJs != null) {
            Timber.d("[" + Thread.currentThread().getName() + "] cache hit, ignoring url : " + uri);
            return new WebResourceResponse("application/javascript", "UTF-8", 200, "OK", cachedJs.headers,
                    new ByteArrayInputStream(cachedJs.data));
        }

        Timber.d("[" + Thread.currentThread().getName() + "] cache miss, fetching url : " + uri);
        Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        CachedResponse cachedResponse = EMPTY_RESPONSE;
        try {
            Response response = client.newCall(request).execute();
            String message = response.message();
            if (message == null || message.isEmpty()) {
                message = "Unknown Status";
            }

            if (response.code() != 200) {
                return new WebResourceResponse("application/unknown", "UTF-8", response.code(), message,
                        cachedResponse.headers,
                        new ByteArrayInputStream(cachedResponse.data));

            }


            Map<String, String> headers = new HashMap<>();
            for (Map.Entry<String, List<String>> e : response.headers().toMultimap().entrySet()) {
                for (String value : e.getValue()) {
                    headers.put(e.getKey(), value);
                }
            }

            String body = injectJsHijack(response.body().string());
            cachedResponse = new CachedResponse(body.getBytes("UTF-8"), headers);
            cachedJavascripts.put(uri, cachedResponse);

            return new WebResourceResponse("application/javascript", "UTF-8", response.code(), message,
                    cachedResponse.headers,
                    new ByteArrayInputStream(cachedResponse.data));
        } catch (IOException e) {
            Timber.e("Exception occurred while loading resource : " + uri, e);
            return new WebResourceResponse("application/javascript", "UTF-8", 500, "Internal Error",
                    cachedResponse.headers,
                    new ByteArrayInputStream(cachedResponse.data));
        }
    }

    private String injectJsHijack(String data) {
        data += hijackedJs;
        return data;
    }

    private String loadResource(String path) {
        InputStream inputStream = getClass().getResourceAsStream(path);
        byte[] buff = new byte[1024];
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int n;
        try {
            while ((n = inputStream.read(buff)) > 0) {
                result.write(buff, 0, n);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read resource : " + path, e);
        }
        return new String(result.toByteArray());
    }

    static class CachedResponse {
        byte[] data;
        Map<String, String> headers;

        CachedResponse(byte[] data, Map<String, String> headers) {
            this.data = data;
            this.headers = headers;
        }
    }

}
