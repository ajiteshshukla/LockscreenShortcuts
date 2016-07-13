package com.acubeapps.lockscreen.shortcuts;

import android.app.Application;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
public final class Injectors {

    private Injectors() {
        throw new AssertionError();
    }

    private static volatile AppComponent appComponent;

    public static AppComponent appComponent() {
        if (appComponent == null) {
            throw new AssertionError("Injector not initialized");
        }
        return appComponent;
    }

    public static void initialize(Application application) {
        if (appComponent == null) {
            synchronized (Injectors.class) {
                if (appComponent == null) {
                    AppModule module = new AppModule(application);
                    appComponent = DaggerAppComponent.builder()
                            .appModule(module)
                            .build();
                }
            }
        }
    }

}
