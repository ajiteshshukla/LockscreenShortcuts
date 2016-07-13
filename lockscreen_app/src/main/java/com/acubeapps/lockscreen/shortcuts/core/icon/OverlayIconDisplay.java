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
import timber.log.Timber;

/**
 * Created by indersingh on 5/29/16.
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
        View touchAreaLeft = viewGroup.findViewById(R.id.touchAreaLeft);
        View touchAreaRight = viewGroup.findViewById(R.id.touchAreaRight);
        if (preferences.getBoolean(Constants.NUDGE_ALIGN_LEFT, true)) {
            params.gravity = Gravity.LEFT | Gravity.TOP;
            if (lsIcon != null && lsIconRight != null) {
                lsIcon.setVisibility(View.VISIBLE);
                lsIconRight.setVisibility(View.GONE);
            }
            if (touchAreaLeft != null && touchAreaRight != null) {
                touchAreaRight.setVisibility(View.VISIBLE);
                touchAreaLeft.setVisibility(View.GONE);
                AnimationHelper.alignIconViewToParent(viewGroup, true);
            }
        } else {
            params.gravity = Gravity.RIGHT | Gravity.TOP;
            if (lsIcon != null && lsIconRight != null) {
                lsIcon.setVisibility(View.GONE);
                lsIconRight.setVisibility(View.VISIBLE);
            }
            if (touchAreaLeft != null && touchAreaRight != null) {
                touchAreaRight.setVisibility(View.GONE);
                touchAreaLeft.setVisibility(View.VISIBLE);
                AnimationHelper.alignIconViewToParent(viewGroup, false);
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
    public void show(boolean shouldAnimate) {
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (isPresent) {
                    return;
                }
                try {
                    wm.addView(viewGroup, getLayoutParams());
                    long magazineViewedCount = preferences.getLong(Constants.MAGAZINE_VIEWED_COUNT, 0);
                    long videoViewedCount = preferences.getLong(Constants.VIDEO_VIEWED_COUNT, 0);
                    long totalVideoCount = preferences.getLong(Constants.TOTAL_VIDEO_COUNT, 1);
                    long videoViewedPerc = 0;
                    if (totalVideoCount != 0) {
                        videoViewedPerc = videoViewedCount / totalVideoCount * 100;
                    }
                    if (magazineViewedCount < Constants.MAX_MAGAZINE_VIEW_COUNT_TO_SHOW_FRESH_ICON_TEXT) {
                        AnimationHelper.animateShowNudgeDetails(viewGroup, preferences);
                    } else if (magazineViewedCount >= Constants.MAX_MAGAZINE_VIEW_COUNT_TO_SHOW_STALE_ICON
                            || videoViewedPerc >= Constants.MAX_VIDEO_VIEW_PERC_TO_SHOW_STALE_ICON) {
                        Timber.d("OverlayIconDisplay : ", "drawing stale icon");
                    } else {
                        AnimationHelper.animateNonDescriptiveNudge(viewGroup);
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
}