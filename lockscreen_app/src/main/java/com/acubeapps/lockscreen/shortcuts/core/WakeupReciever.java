package com.acubeapps.lockscreen.shortcuts.core;

import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconController;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.telephony.TelephonyManager;
import timber.log.Timber;

import javax.inject.Inject;

/**
 * Created by ritwik on 30/05/16.
 */
public class WakeupReciever extends BroadcastReceiver {

    @Inject
    IconController iconController;

    public WakeupReciever() {
        Injectors.appComponent().injectWakeupReciever(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive(%s)", intent);
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state != null && state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            iconController.hideIcon();
        }
    }
}
