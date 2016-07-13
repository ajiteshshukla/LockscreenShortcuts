package com.acubeapps.lockscreen.shortcuts.utils;

import com.acubeapps.lockscreen.shortcuts.UnlockActivity;

import android.content.Context;
import android.content.Intent;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
public final class KeyguardAssist {

    private KeyguardAssist() {
    }

    public static void launchUnlockActivity(Context context) {
        Intent intent = new Intent(context, UnlockActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
