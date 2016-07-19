package com.acubeapps.lockscreen.shortcuts;

import com.acubeapps.lockscreen.shortcuts.core.NudgeService;
import com.acubeapps.lockscreen.shortcuts.core.WakeupReciever;

import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void injectMainApplication(MainApplication application);

    void injectNudgeService(NudgeService obj);

    void injectUnlockActivity(UnlockActivity obj);

    void injectWakeupReciever(WakeupReciever obj);

    void injectAppSelectActivity(ActivityAppSelect obj);
}
