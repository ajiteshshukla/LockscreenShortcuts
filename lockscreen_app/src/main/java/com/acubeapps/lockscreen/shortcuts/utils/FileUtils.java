package com.acubeapps.lockscreen.shortcuts.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import timber.log.Timber;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ajitesh.shukla on 6/13/16.
 */
public final class FileUtils {

    private FileUtils() {

    }

    public static String getLocalFilePath(Uri uri, String filename, Context context) {
        String path = null;
        try {
            ContentResolver cR = context.getContentResolver();
            byte[] byteArray = readBytes(cR.openInputStream(uri));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(context.getFilesDir()
                    + filename));
            bos.write(byteArray);
            bos.flush();
            bos.close();
            path = context.getFilesDir() + filename;
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
        return path;
    }

    private static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
