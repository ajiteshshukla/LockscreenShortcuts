package com.acubeapps.lockscreen.shortcuts.core;

import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconController;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.telephony.TelephonyManager;

import javax.inject.Inject;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
public class WakeupReciever extends BroadcastReceiver {

    @Inject
    IconController iconController;

    public WakeupReciever() {
        Injectors.appComponent().injectWakeupReciever(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state != null && state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            iconController.hideIcon();
        }
    }
}
