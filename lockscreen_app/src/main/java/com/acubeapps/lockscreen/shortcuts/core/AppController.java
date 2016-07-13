package com.acubeapps.lockscreen.shortcuts.core;

import com.acubeapps.lockscreen.shortcuts.core.events.ScreenEventsType;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOffEvent;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOnEvent;
import com.acubeapps.lockscreen.shortcuts.core.events.UserOnHomeScreenEvent;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconController;

import android.content.SharedPreferences;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.inject.Inject;

/**
 * Created by aasha.medhi on 7/12/16.
 */
public class AppController {

    private IconController iconController;
    private EventBus eventBus;

    private final Lock currentIconLock = new ReentrantLock();
    private SharedPreferences preferences;

    @Inject
    public AppController(IconController iconController, EventBus eventBus,
                         SharedPreferences preferences) {
        this.iconController = iconController;
        this.eventBus = eventBus;
        this.preferences = preferences;
        this.eventBus.register(this);
    }

    public void showIcon() {
        iconController.showIcon();
    }

    @Subscribe
    public void onScreenOnEvent(ScreenOnEvent event) {
        Timber.d("onScreenOnEvent()");
        if (isUserOnHomeScreenFingerPrintUnlock(event)
                || isScreenOnWithoutScreenOff(event)) {
            return;
        }
        showIcon();
    }

    private boolean isScreenOnWithoutScreenOff(ScreenOnEvent screenOnEvent) {
      if (screenOnEvent.getLastEventType() != ScreenEventsType.SCREEN_OFF) {
          return true;
      }
        return false;
    }

    private boolean isUserOnHomeScreenFingerPrintUnlock(ScreenOnEvent screenOnEvent) {
        if (screenOnEvent.getLastEventType() == ScreenEventsType.USER_PRESENT_HOME_SCREEN) {
           return true;
        }
        return false;
    }

    @Subscribe
    public void onScreenOffEvent(ScreenOffEvent event) {
        Timber.d("onScreenOffEvent()");
        Log.e("AASHA", "Off");
        hide();
    }

    @Subscribe
    public void onUserOnHomeScreenEvent(UserOnHomeScreenEvent event) {
        Timber.d("onUserOnHomeScreenEvent()");
        hide();
    }

    private void hide() {
        Timber.d("hide()");
        iconController.hideIcon();
    }

    public void start() {
        Timber.d("start()");
    }

    public void stop() {
        Timber.d("stop()");
    }
}
