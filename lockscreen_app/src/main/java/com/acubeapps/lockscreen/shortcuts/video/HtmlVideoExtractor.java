package com.acubeapps.lockscreen.shortcuts.video;

import com.inmobi.oem.thrift.ad.model.TVideo;
import com.inmobi.oem.thrift.ad.model.TVideoMetadata;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by ritwik on 27/06/16.
 */
public class HtmlVideoExtractor {

    private final Context context;
    private final int deviceWidth;
    private OkHttpClient client;
    private static final InterceptedResponse EMPTY_RESPONSE = new InterceptedResponse("application/unknown",
            new byte[0], Collections.EMPTY_MAP);
    private List<HtmlInterceptor> interceptors = new LinkedList<>();
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private Callback callback;

    private final Semaphore numberOfWebViews = new Semaphore(5);
    private static final ExecutorService POOL = Executors.newFixedThreadPool(3);
    private static final Timer CLEANUP_TIMER = new Timer();

    public HtmlVideoExtractor(Context context) {
        this.context = context;
        client = new OkHttpClient();
        interceptors.add(new FacebookHtmlInterceptor());
        deviceWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    private WebViewEntry createWebView(final Callback callback, final List<TVideo> videos) {
        try {
            numberOfWebViews.acquire();
            Timber.d("Creating webview, permits remaining = %d", numberOfWebViews.availablePermits());
            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicReference<WebViewEntry> webView = new AtomicReference<>();
            HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    WebView result = new WebView(context);
                    final WebViewEntry entry = new WebViewEntry(result);
                    initializeWebView(entry, callback, videos);
                    webView.set(entry);
                    latch.countDown();
                }
            });
            latch.await();
            CLEANUP_TIMER.schedule(new TimerTask() {
                @Override
                public void run() {
                    webView.get().release();
                }
            }, TimeUnit.MINUTES.toMillis(10));

            return webView.get();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private void initializeWebView(final WebViewEntry webViewEntry, final Callback callback, List<TVideo> videos) {
        WebView webView = webViewEntry.webView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                WebResourceResponse response = HtmlVideoExtractor.this.shouldInterceptRequest(view, request);
                if (response == null) {
                    response = super.shouldInterceptRequest(view, request);
                }
                return response;
            }

        });
        webView.addJavascriptInterface(new VideoInterceptApi(webViewEntry, callback, videos), "__intercept_videoApi");
    }

    public void loadVideos(final List<TVideo> videos, final Callback callback) {
        Timber.i("Load videos : %s", videos);
        POOL.submit(new Runnable() {
            @Override
            public void run() {
                final WebViewEntry webView = createWebView(callback, videos);

                final StringBuilder html = new StringBuilder();
                for (TVideo video : videos) {
                    TVideoMetadata metadata = video.getVideoMetadata();
                    if (metadata == null) {
                        continue;
                    }

                    int width = deviceWidth;
                    int height = (width * video.getAspectHeight()) / video.getAspectWidth();

                    Timber.i("loading video : %dx%d", width, height);

                    for (HtmlInterceptor interceptor : interceptors) {
                        if (!interceptor.canHandleVideo(metadata)) {
                            continue;
                        }
                        html.append(interceptor.getEmbedTag(video.getId(), metadata, width, height));
                    }
                }
                Timber.i("Html : %s", html);
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        if (webView.isDestroyed()) {
                            return;
                        }
                        Timber.i("Loading html : %s", html);
                        try {
                            webView.webView.loadData(html.toString(), "text/html", "UTF-8");
                        } catch (Exception e) {
                            Timber.e(e, "Exception occurred while loading html");
                        }
                    }
                });
            }
        });
    }

    private WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (request.isForMainFrame()) {
            return null;
        }
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        Uri uri = request.getUrl();
        for (HtmlInterceptor interceptor : interceptors) {
            if (!interceptor.canIntercept(uri)) {
                continue;
            }

            Timber.i("intercepting request : %s", uri);

            return fetchRequest(interceptor, uri);
        }
        return null;
    }

    private WebResourceResponse fetchRequest(HtmlInterceptor interceptor, Uri uri) {
        Timber.i("cache miss, fetching url : %s", uri);
        Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        InterceptedResponse cachedResponse = EMPTY_RESPONSE;
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

            InterceptedResponse interceptedResponse = interceptor.intercept(uri, response.body().bytes());
            cachedResponse = new InterceptedResponse(interceptedResponse.mimeType, interceptedResponse.data, headers);

            return cachedResponse.toWebResource();
        } catch (IOException e) {
            Timber.i("Exception occurred while loading resource : " + uri, e);
            return new WebResourceResponse("application/unknown", "UTF-8", 500, "Internal Error",
                    cachedResponse.headers,
                    new ByteArrayInputStream(cachedResponse.data));
        }
    }

    class VideoInterceptApi {

        final WebViewEntry webView;
        final Callback callback;
        final ConcurrentMap<String, Boolean> videoIds = new ConcurrentHashMap<>();

        public VideoInterceptApi(WebViewEntry webView, Callback callback, List<TVideo> videos) {
            this.webView = webView;
            this.callback = callback;
            for (TVideo v : videos) {
                videoIds.put(v.getId(), true);
            }
        }

        @JavascriptInterface
        public void onVideo(final String videoId, final String src) {
            Timber.i("[%s] found src = %s", videoId, src);
            videoIds.remove(videoId);
            if (videoIds.isEmpty()) {
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.release();
                    }
                });
            }
            POOL.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        callback.onVideo(videoId, src);
                    } catch (Exception e) {
                        Timber.e(e, "Error occurred while calling callback");
                    }
                }
            });
        }
    }

    public interface Callback {

        void onVideo(String videoId, String url);

    }

    class WebViewEntry {

        final WebView webView;
        final AtomicBoolean destroyed = new AtomicBoolean();

        public WebViewEntry(WebView webView) {
            this.webView = webView;
        }

        boolean isDestroyed() {
            return destroyed.get();
        }

        void release() {
            boolean result = destroyed.getAndSet(true);
            if (result) {
                return;
            }

            try {
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.destroy();
                    }
                });
            } catch (Exception e) {
                Timber.e(e, "Error occurred while destroying webview");
            } finally {
                numberOfWebViews.release();
                Timber.d("Releasing webview, permits remaining = %d", numberOfWebViews.availablePermits());
            }
        }
    }
}
