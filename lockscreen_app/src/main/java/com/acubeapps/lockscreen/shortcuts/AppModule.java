package com.acubeapps.lockscreen.shortcuts;

import com.acubeapps.lockscreen.shortcuts.core.icon.IconController;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconControllerImpl;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconDisplayFactory;
import com.acubeapps.lockscreen.shortcuts.core.icon.OverlayIconDisplayFactory;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Singleton;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
@Module
public class AppModule {

    private final Context context;
    private final IconController iconController;
    private final EventBus eventBus;
    private final ExecutorService backgroundPool;
    private final Picasso picasso;
    private final SharedPreferences sharedPreferences;

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
        this.backgroundPool = Executors.newFixedThreadPool(5);
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
    public EventBus provideEventBus() {
        return eventBus;
    }

    @Singleton
    @Provides
    public ExecutorService provideBackgroundPool() {
        return backgroundPool;
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

}
