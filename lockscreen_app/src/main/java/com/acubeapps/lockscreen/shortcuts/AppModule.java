package com.acubeapps.lockscreen.shortcuts;

import com.inmobi.oem.moments.matcher.MomentApi;
import com.acubeapps.lockscreen.shortcuts.BuildConfig;
import com.acubeapps.lockscreen.shortcuts.ad.MomentAdApi;
import com.acubeapps.lockscreen.shortcuts.ad.MomentHandler;
import com.acubeapps.lockscreen.shortcuts.adstore.AdStore;
import com.acubeapps.lockscreen.shortcuts.adstore.MemAdStore;
import com.acubeapps.lockscreen.shortcuts.analytics.AmplitudeAnalyticsClient;
import com.acubeapps.lockscreen.shortcuts.analytics.Analytics;
import com.acubeapps.lockscreen.shortcuts.analytics.AnalyticsClient;
import com.acubeapps.lockscreen.shortcuts.analytics.AnalyticsImpl;
import com.acubeapps.lockscreen.shortcuts.analytics.MixPanelAnalyticsClient;
import com.acubeapps.lockscreen.shortcuts.core.ad.AdApi;
import com.acubeapps.lockscreen.shortcuts.core.card.ActivityCardController;
import com.acubeapps.lockscreen.shortcuts.core.card.CardController;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconController;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconControllerImpl;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconDisplayFactory;
import com.acubeapps.lockscreen.shortcuts.core.icon.OverlayIconDisplayFactory;
import com.acubeapps.lockscreen.shortcuts.utils.Utils;
import com.acubeapps.lockscreen.shortcuts.video.SharedPreferencesVideoStore;
import com.acubeapps.lockscreen.shortcuts.video.VideoService;
import com.acubeapps.lockscreen.shortcuts.video.VideoStore;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Singleton;

/**
 * Created by ritwik on 29/05/16.
 */
@Module
public class AppModule {

    private final Context context;
    private final IconController iconController;
    private final CardController cardController;
    private final EventBus eventBus;
    private final AdApi adApi;
    private final ExecutorService backgroundPool;
    private final AdStore adStore;
    private final MomentApi momentApi;
    private final Analytics analytics;
    private final Picasso picasso;
    private final SharedPreferences sharedPreferences;
    private final VideoStore videoStore;
    private final VideoService videoService;

    public AppModule(Application context) {
        this.context = context;
        this.eventBus = new EventBus();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .header("Cache-Control", "max-age=" + (Constants.PICASSO_CACHE_AGE)).build();
            }
        });
        okHttpClient.setCache(new Cache(this.context.getCacheDir(), Integer.MAX_VALUE));
        OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);

        picasso = new Picasso.Builder(this.context).downloader(okHttpDownloader).build();
        IconDisplayFactory iconFactory = new OverlayIconDisplayFactory(context, sharedPreferences);
        this.iconController = new IconControllerImpl(iconFactory);
        this.cardController = new ActivityCardController(context, eventBus);
        this.backgroundPool = Executors.newFixedThreadPool(5);
        videoStore = new SharedPreferencesVideoStore(sharedPreferences);
        videoService = new VideoService(context, videoStore, backgroundPool);

//        CoreSdkApi icsApi = new CoreSdkApi(context);
//        this.adApi = new IcsAdApi(icsApi, this.backgroundPool);

        adStore = new MemAdStore(context);
        MomentHandler momentHandler = new MomentHandler(context, adStore, videoStore, picasso, videoService);
        momentApi = new MomentApi(this.context, momentHandler);
        this.adApi = new MomentAdApi(context, adStore, momentApi);
        String userId = Utils.getUserId(context);
        String amplitudeKey = getAmplitudeKey(userId);
        AmplitudeAnalyticsClient amplitudeAnalyticsClient =
                new AmplitudeAnalyticsClient(context, amplitudeKey, userId);
        String mixPanelKey = getMixPanelKey(userId);
        MixPanelAnalyticsClient mixPanelAnalyticsClient =
                new MixPanelAnalyticsClient(context, mixPanelKey, userId);
        List<AnalyticsClient> analyticsClientsList = new ArrayList<>();
        analyticsClientsList.add(amplitudeAnalyticsClient);
        analyticsClientsList.add(mixPanelAnalyticsClient);
        this.analytics = new AnalyticsImpl(analyticsClientsList, sharedPreferences);
    }

    @Singleton
    @Provides
    public Context provideContext() {
        return context;
    }


    @Singleton
    @Provides
    public IconController provideIconController() {
        return iconController;
    }

    @Singleton
    @Provides
    public CardController provideCardController() {
        return cardController;
    }

    @Singleton
    @Provides
    public EventBus provideEventBus() {
        return eventBus;
    }

    @Singleton
    @Provides
    public AdApi provideAdApi() {
        return adApi;
    }

    @Singleton
    @Provides
    public VideoStore provideVideoStore() {
        return videoStore;
    }

    @Singleton
    @Provides
    public VideoService provideVideoService() {
        return videoService;
    }

    @Singleton
    @Provides
    public ExecutorService provideBackgroundPool() {
        return backgroundPool;
    }

    @Singleton
    @Provides
    public MomentApi provideMomentApi() {
        return momentApi;
    }

    @Singleton
    @Provides
    public Analytics provideAnalytics() {
        return analytics;
    }

    @Singleton
    @Provides
    public Picasso getPicasso() {
        return this.picasso;
    }

    @Singleton
    @Provides
    public SharedPreferences getSharedPreferences() {
        return this.sharedPreferences;
    }

    private String getAmplitudeKey(String userId) {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug") || isUserWhitelisted(userId)) {
            return Constants.AMPLITUDE_ANALYTICS_TEST_KEY;
        } else {
            return Constants.AMPLITUDE_ANALYTICS_PROD_KEY;
        }
    }

    private String getMixPanelKey(String userId) {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug") || isUserWhitelisted(userId)) {
            return Constants.MIXPANEL_ANALYTICS_TEST_KEY;
        } else {
            return Constants.MIXPANEL_ANALYTICS_PROD_KEY;
        }
    }

    private boolean isUserWhitelisted(String userId) {
        Boolean userWhiteListed = Constants.WHITELIST_DEVICE_MAP.get(userId);
        return (userWhiteListed != null) ? userWhiteListed : false;
    }

}
