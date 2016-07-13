package com.acubeapps.lockscreen.shortcuts;

import com.acubeapps.lockscreen.shortcuts.DaggerAppComponent;

import android.app.Application;

/**
 * Created by ritwik on 29/05/16.
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
