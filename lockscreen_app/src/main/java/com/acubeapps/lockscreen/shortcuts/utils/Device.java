//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acubeapps.lockscreen.shortcuts.utils;

import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.ViewConfiguration;
import android.view.WindowManager;


import java.lang.reflect.Method;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
public final class Device {
    public static final int API_VERSION;
    public static final String API_VERSION_NAME_SHORT;

    static {
        API_VERSION = VERSION.SDK_INT;
        switch (API_VERSION) {
            case 14:
                API_VERSION_NAME_SHORT = "ICS";
                break;
            case 15:
                API_VERSION_NAME_SHORT = "ICS1";
                break;
            case 16:
                API_VERSION_NAME_SHORT = "JB";
                break;
            case 17:
                API_VERSION_NAME_SHORT = "JB1";
                break;
            case 18:
                API_VERSION_NAME_SHORT = "JB2";
                break;
            case 19:
                API_VERSION_NAME_SHORT = "KK";
                break;
            case 20:
                API_VERSION_NAME_SHORT = "KKW";
                break;
            case 21:
                API_VERSION_NAME_SHORT = "LP";
                break;
            case 22:
                API_VERSION_NAME_SHORT = "LP1";
                break;
            case 23:
                API_VERSION_NAME_SHORT = "M";
                break;
            default:
                API_VERSION_NAME_SHORT = "WTF";
        }
    }

    private Device() {
    }

    private static String capitalize(String var0) {
        if (TextUtils.isEmpty(var0)) {
            return var0;
        } else {
            char[] var5 = var0.toCharArray();
            boolean var2 = true;
            var0 = "";
            int var4 = var5.length;

            for (int var3 = 0; var3 < var4; ++var3) {
                char var1 = var5[var3];
                if (var2 && Character.isLetter(var1)) {
                    var0 = var0 + Character.toUpperCase(var1);
                    var2 = false;
                } else {
                    if (Character.isWhitespace(var1)) {
                        var2 = true;
                    }

                    var0 = var0 + var1;
                }
            }

            return var0;
        }
    }

    public static Point getScreenSize(@NonNull Context var0) {
        Display var4 = ((WindowManager) var0.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point var1 = new Point();
        if (hasJellyBeanApi()) {
            DisplayMetrics var5 = new DisplayMetrics();
            var4.getRealMetrics(var5);
            var1.x = var5.widthPixels;
            var1.y = var5.heightPixels;
            return var1;
        } else if (hasIcsApi()) {
            try {
                Method var2 = Display.class.getMethod("getRawHeight", new Class[0]);
                var1.x = ((Integer) Display.class.getMethod("getRawWidth", new Class[0])
                        .invoke(var4, new Object[0])).intValue();
                var1.y = ((Integer) var2.invoke(var4, new Object[0]))
                        .intValue();
                return var1;
            } catch (Exception var3) {
                var1.x = var4.getWidth();
                var1.y = var4.getHeight();
                return var1;
            }
        } else {
            var1.x = var4.getWidth();
            var1.y = var4.getHeight();
            return var1;
        }
    }

//    public static String getUDID(Context var0) {
//        String var1 = java.lang.System.getString(var0.getContentResolver(), "android_id");
//        return var1;
//    }

    public static boolean hasGingerbread() {
        return hasTargetApi(9);
    }

    public static boolean hasHoneyCombMr2() {
        return hasTargetApi(13);
    }

    public static boolean hasIcsApi() {
        return hasTargetApi(14);
    }

    public static boolean hasJellyBeanApi() {
        return hasTargetApi(17);
    }

    public static boolean hasJellyBeanMR1Api() {
        return hasTargetApi(17);
    }

    public static boolean hasJellyBeanMR2Api() {
        return hasTargetApi(18);
    }

    public static boolean hasKitKatApi() {
        return hasTargetApi(19);
    }

    public static boolean hasKitKatWatchApi() {
        return hasTargetApi(20);
    }

    public static boolean hasLollipopApi() {
        return hasTargetApi(21);
    }

    public static boolean hasLollipopMR1Api() {
        return hasTargetApi(22);
    }

    public static boolean hasMarshmallowApi() {
        return hasTargetApi(23);
    }

    public static boolean hasNavBar(Context var0) {
        Resources var4 = var0.getResources();
        int var1 = var4.getIdentifier("config_showNavigationBar", "bool", "android");
        if (var1 > 0) {
            return var4.getBoolean(var1);
        } else {
            boolean var2 = ViewConfiguration.get(var0).hasPermanentMenuKey();
            boolean var3 = KeyCharacterMap.deviceHasKey(4);
            return !var2 && !var3;
        }
    }

    public static boolean hasNotifiesAccess(Context var0, Class var1) {
        if (hasJellyBeanMR2Api()) {
            ComponentName var3 = new ComponentName(var0, var1);
            String var2 = Secure.getString(var0.getContentResolver(), "enabled_notification_listeners");
            if (var2 != null && var2.contains(var3.flattenToString())) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasTargetApi(int var0) {
        return API_VERSION >= var0;
    }

    public static boolean isCallOngoing(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return !(tm.getCallState() == TelephonyManager.CALL_STATE_IDLE);
    }

    public static int getDeviceMediaVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static void setDeviceMediaVolume(Context context, int volume) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (volume == 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_ALLOW_RINGER_MODES);
        }

    }

    public static boolean isDeviceLocked(Context context) {
        KeyguardManager kgMgr = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return kgMgr.inKeyguardRestrictedInputMode();
    }

    public static int getMaxDeviceMediaVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }
}
