package com.acubeapps.lockscreen.shortcuts.utils;

import com.inmobi.oem.internal.CommonUtils;

import android.content.Context;
import android.provider.Settings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by netra.shetty on 6/22/16.
 */
public final class Utils {
    private Utils() {
    }

    public static String getUserId(Context context) {
        String type = null;
        String userId = null;

        try {
            userId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception var4) {
            ;
        }
        return CommonUtils.computeOdin1(userId);
    }

    public static String loadResource(String path) {
        InputStream inputStream = Utils.class.getResourceAsStream(path);
        byte[] buff = new byte[1024];
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int n;
        try {
            while ((n = inputStream.read(buff)) > 0) {
                result.write(buff, 0, n);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read resource : " + path, e);
        }
        return new String(result.toByteArray());
    }

}
