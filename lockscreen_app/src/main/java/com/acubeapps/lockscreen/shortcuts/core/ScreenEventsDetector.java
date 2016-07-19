package com.acubeapps.lockscreen.shortcuts.core;

import com.acubeapps.lockscreen.shortcuts.core.events.ScreenEventsType;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOffEvent;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOnEvent;
import com.acubeapps.lockscreen.shortcuts.core.events.UserOnHomeScreenEvent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
public class ScreenEventsDetector extends BroadcastReceiver {

    private final Context context;
    private final EventBus eventBus;
    private ScreenEventsType lastEventType;

    @Inject
    public ScreenEventsDetector(Context context, EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
        register();
    }

    private void register() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }

        switch (action) {
            case Intent.ACTION_SCREEN_ON:
                eventBus.post(new ScreenOnEvent(lastEventType));
                lastEventType = ScreenEventsType.SCREEN_ON;
                break;

            case Intent.ACTION_SCREEN_OFF:
                eventBus.post(new ScreenOffEvent(lastEventType));
                lastEventType = ScreenEventsType.SCREEN_OFF;
                break;

            case Intent.ACTION_USER_PRESENT:
                eventBus.post(new UserOnHomeScreenEvent(lastEventType));
                lastEventType = ScreenEventsType.USER_PRESENT_HOME_SCREEN;
                break;

            default:
                break;
        }
    }
}
