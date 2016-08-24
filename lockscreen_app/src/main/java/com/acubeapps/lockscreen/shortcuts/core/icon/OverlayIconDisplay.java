package com.acubeapps.lockscreen.shortcuts.core.icon;

import com.acubeapps.lockscreen.shortcuts.Constants;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.core.AnimationHelper;

import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
public class OverlayIconDisplay implements IconDisplay {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static final int DEFAULT_X = 0;
    private static final int DEFAULT_Y = 60;

    public int positionX;
    public int positionY;
    private final ViewGroup viewGroup;
    private final WindowManager wm;
    private boolean isPresent = false;
    private SharedPreferences preferences;

    public OverlayIconDisplay(int positionX, int positionY, ViewGroup viewGroup, WindowManager wm,
                              SharedPreferences preferences) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.viewGroup = viewGroup;
        this.wm = wm;
        this.preferences = preferences;
    }

    private WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams params;
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        int format = PixelFormat.TRANSLUCENT;
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        int windowType = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params = new WindowManager.LayoutParams(width, height, windowType, flags, format);
        View lsIcon = viewGroup.findViewById(R.id.lsIcon);
        View lsIconRight = viewGroup.findViewById(R.id.lsIconRight);
        if (preferences.getString("pref_display_location", "Top-Left").equals("Top-Left")) {
            preferences.edit().putBoolean(Constants.NUDGE_ALIGN_LEFT, true).apply();
            params.gravity = Gravity.LEFT | Gravity.TOP;
            if (lsIcon != null && lsIconRight != null) {
                lsIcon.setVisibility(View.VISIBLE);
                lsIconRight.setVisibility(View.GONE);
            }
        } else if (preferences.getString("pref_display_location", "Top-Left").equals("Top-Right")) {
            preferences.edit().putBoolean(Constants.NUDGE_ALIGN_LEFT, false).apply();
            params.gravity = Gravity.RIGHT | Gravity.TOP;
            if (lsIcon != null && lsIconRight != null) {
                lsIcon.setVisibility(View.GONE);
                lsIconRight.setVisibility(View.VISIBLE);
            }
        } else if (preferences.getString("pref_display_location", "Top-Left").equals("Bottom-Left")) {
            preferences.edit().putBoolean(Constants.NUDGE_ALIGN_LEFT, true).apply();
            params.gravity = Gravity.LEFT | Gravity.BOTTOM;
            if (lsIcon != null && lsIconRight != null) {
                lsIcon.setVisibility(View.VISIBLE);
                lsIconRight.setVisibility(View.GONE);
            }
        } else {
            preferences.edit().putBoolean(Constants.NUDGE_ALIGN_LEFT, false).apply();
            params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            if (lsIcon != null && lsIconRight != null) {
                lsIcon.setVisibility(View.GONE);
                lsIconRight.setVisibility(View.VISIBLE);
            }
        }
        params.x = DEFAULT_X;
        params.y = DEFAULT_Y;
        return params;
    }

    public static WindowManager.LayoutParams getGeneralLayoutParams() {
        WindowManager.LayoutParams params;
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        int format = PixelFormat.TRANSLUCENT;
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        int windowType = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params = new WindowManager.LayoutParams(width, height, windowType, flags, format);
        return params;
    }

    @Override
    public void show() {
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (isPresent) {
                    return;
                }
                try {
                    wm.addView(viewGroup, getLayoutParams());
                    if (preferences.getBoolean("pref_key_auto_expand", true)) {
                        AnimationHelper.animateShowNudgeDetails(viewGroup, preferences);
                    } else {
                        final View nudgeView = viewGroup.findViewById(R.id.layoutText);
                        if (preferences.getBoolean(Constants.NUDGE_ALIGN_LEFT, true)) {
                            final View dummyView = viewGroup.findViewById(R.id.dummyTouchViewLeft);
                            dummyView.setVisibility(View.INVISIBLE);
                        } else {
                            final View dummyView = viewGroup.findViewById(R.id.dummyTouchViewRight);
                            dummyView.setVisibility(View.INVISIBLE);
                        }
                        nudgeView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isPresent = false;
                }
                isPresent = true;
            }
        });
    }


    @Override
    public void hide() {
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (!isPresent) {
                    return;
                }

                try {
                    wm.removeViewImmediate(viewGroup);
                } catch (Exception e) {
                    isPresent = false;
                }
                isPresent = false;
            }
        });
    }

    private void setBackgroundColor(ViewGroup viewGroup) {

    }
}
