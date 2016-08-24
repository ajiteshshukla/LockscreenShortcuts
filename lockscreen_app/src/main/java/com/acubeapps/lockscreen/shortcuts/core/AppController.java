package com.acubeapps.lockscreen.shortcuts.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.acubeapps.lockscreen.shortcuts.core.events.ScreenEventsType;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOffEvent;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOnEvent;
import com.acubeapps.lockscreen.shortcuts.core.events.UserOnHomeScreenEvent;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconController;

import com.acubeapps.lockscreen.shortcuts.utils.Device;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

/**
 * Created by aasha.medhi on 7/12/16.
 */
public class AppController {

    private IconController iconController;
    private EventBus eventBus;
    private SharedPreferences preferences;
    private Context context;

    @Inject
    public AppController(IconController iconController, EventBus eventBus, Context context,
                         SharedPreferences preferences) {
        this.iconController = iconController;
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.context = context;
        this.preferences = preferences;
    }

    public void showIcon() {
        iconController.showIcon();
    }

    @Subscribe
    public void onScreenOnEvent(ScreenOnEvent event) {
        if ((isUserOnHomeScreenFingerPrintUnlock(event)
                || isScreenOnWithoutScreenOff(event))
                && preferences.getBoolean("pref_key_lockscreen_only", true)) {
            return;
        }
        if ((Device.isDeviceLocked(context) || !preferences.getBoolean("pref_key_lockscreen_only", true))
                && !Device.isCallOngoing(context)) {
            showIcon();
        }
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
        hide();
    }

    @Subscribe
    public void onUserOnHomeScreenEvent(UserOnHomeScreenEvent event) {
        if (preferences.getBoolean("pref_key_lockscreen_only", true)) {
            hide();
        } else {
            //Do nothing
        }
    }

    private void hide() {
        iconController.hideIcon();
    }

    public void start() {
    }

    public void stop() {
    }
}
