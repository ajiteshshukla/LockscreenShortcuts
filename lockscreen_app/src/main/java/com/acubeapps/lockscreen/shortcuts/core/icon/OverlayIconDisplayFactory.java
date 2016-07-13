package com.acubeapps.lockscreen.shortcuts.core.icon;

import com.acubeapps.lockscreen.shortcuts.AppInfo;
import com.acubeapps.lockscreen.shortcuts.Constants;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.core.AnimationHelper;
import com.acubeapps.lockscreen.shortcuts.utils.KeyguardAssist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritwik on 29/05/16.
 */
public class OverlayIconDisplayFactory implements IconDisplayFactory {

    private static final String LOG_TAG = OverlayIconDisplayFactory.class.getName();

    private final WindowManager windowManager;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private SharedPreferences preferences;
    GestureDetector gestureDetector;
    private boolean isLongPressed = false;
    private boolean flag_handled = false;
    private RelativeLayout layoutText;

    public OverlayIconDisplayFactory(Context context, SharedPreferences preferences) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.preferences = preferences;
    }

    @Override
    public IconDisplay getIconDisplay(final Icon icon, final IconEventListener eventListener) {
        ViewGroup view;
        view = (ViewGroup) layoutInflater.inflate(R.layout.icon_magazine_button_text, null);
        List<ImageView> imageViewList = new ArrayList<>();
        ImageView appIcon1 = (ImageView) view.findViewById(R.id.appicon1);
        ImageView appIcon2 = (ImageView) view.findViewById(R.id.appicon2);
        ImageView appIcon3 = (ImageView) view.findViewById(R.id.appicon3);
        ImageView appIcon4 = (ImageView) view.findViewById(R.id.appicon4);
        ImageView appIcon5 = (ImageView) view.findViewById(R.id.appicon5);
        ImageView appIcon6 = (ImageView) view.findViewById(R.id.appicon6);
        imageViewList.add(appIcon1);
        imageViewList.add(appIcon2);
        imageViewList.add(appIcon3);
        imageViewList.add(appIcon4);
        imageViewList.add(appIcon5);
        imageViewList.add(appIcon6);
        bindAppIcon(imageViewList);
        final IconDisplay display = new OverlayIconDisplay(0, 0, view, windowManager, preferences);
        view.setOnTouchListener(new OverlayIconTouchListener(icon, eventListener));
        gestureDetector = new GestureDetector(context, new Gesture(view));
        layoutText = (RelativeLayout) view.findViewById(R.id.layoutText);
        layoutText.setVisibility(View.VISIBLE);
        return display;
    }

    private AppInfo getAppInfo(ResolveInfo resolveInfo) {
        ActivityInfo activity = resolveInfo.activityInfo;
        ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
        final Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        launchIntent.setComponent(name);
        return new AppInfo(resolveInfo.activityInfo.packageName, launchIntent);
    }

    private void addTouchListeners(final List<ImageView> imageViewList, final List<AppInfo> appInfoList) {
        for (int i = 0; i < imageViewList.size() && i < appInfoList.size(); i++) {
            try {
                final int j = i;
                imageViewList.get(i).setImageDrawable(context.getPackageManager()
                        .getApplicationIcon(appInfoList.get(i).getPackageName()));
                imageViewList.get(i).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (appInfoList.get(j).getLaunchIntent() != null) {
                            context.startActivity(appInfoList.get(j).getLaunchIntent());
                        } else {
                            Log.d("Ajitesh : ", "launch intent is null");
                        }
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bindAppIcon(List<ImageView> imageViewList) {
        PackageManager packageManager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //read package names from store
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        final List<AppInfo> appInfoList = new ArrayList<>();
        for (ResolveInfo resolveInfo : apps) {
            if (appInfoList.size() > imageViewList.size()) {
                break;
            }
            try {
                AppInfo appInfo = getAppInfo(resolveInfo);
                if (appInfo.getLaunchIntent() != null) {
                    Log.d("Ajitesh : ", "launch intent null for " + resolveInfo.toString());
                    appInfoList.add(appInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        addTouchListeners(imageViewList, appInfoList);
    }

    private void animateAndMoveToNewLocation(View view, MotionEvent motionEvent,
                                             WindowManager.LayoutParams layoutParams) {
        View lsIcon = view.findViewById(R.id.lsIcon);
        View lsIconRight = view.findViewById(R.id.lsIconRight);
        View touchAreaLeft = view.findViewById(R.id.touchAreaLeft);
        View touchAreaRight = view.findViewById(R.id.touchAreaRight);
        if (motionEvent.getRawX() > getCenterX()) {
            layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
            preferences.edit().putBoolean(Constants.NUDGE_ALIGN_LEFT, false).apply();
            if (lsIcon != null && lsIconRight != null) {
                lsIcon.setVisibility(View.GONE);
                lsIconRight.setVisibility(View.VISIBLE);
            }
            if (touchAreaLeft != null && touchAreaRight != null) {
                touchAreaRight.setVisibility(View.GONE);
                touchAreaLeft.setVisibility(View.VISIBLE);
                AnimationHelper.alignIconViewToParent(view, false);
            }
            Timber.d(LOG_TAG, "Align Nudge Right");
        } else {
            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            preferences.edit().putBoolean(Constants.NUDGE_ALIGN_LEFT, true).apply();
            if (lsIcon != null && lsIconRight != null) {
                lsIcon.setVisibility(View.VISIBLE);
                lsIconRight.setVisibility(View.GONE);
            }
            if (touchAreaLeft != null && touchAreaRight != null) {
                touchAreaRight.setVisibility(View.VISIBLE);
                touchAreaLeft.setVisibility(View.GONE);
                AnimationHelper.alignIconViewToParent(view, true);
            }
            Timber.d(LOG_TAG, "Align Nudge Left");
        }
        layoutParams.x = 0;
        layoutParams.y = 60;
        AnimationHelper.animateOverlay(view, layoutParams, motionEvent, windowManager, preferences);
    }

    private class OverlayIconTouchListener implements View.OnTouchListener {
        private final IconEventListener iconEventListener;
        private final Icon icon;
        private int initialY = 0;
        private float initialTouchY = 0;
        WindowManager.LayoutParams layoutParams;

        public OverlayIconTouchListener(Icon icon, IconEventListener iconEventListener) {
            this.icon = icon;
            this.iconEventListener = iconEventListener;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getActionMasked();
            gestureDetector.onTouchEvent(motionEvent);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    layoutParams = OverlayIconDisplay.getGeneralLayoutParams();
                    initialY = (int) view.getY();
                    initialTouchY = motionEvent.getRawY();
                    flag_handled = false;
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    AnimationHelper.animateHideNudgeDetails(view, null, preferences);
                    break;
                case MotionEvent.ACTION_UP:
                    if (isLongPressed) {
                        animateAndMoveToNewLocation(view, motionEvent, layoutParams);
                    }
                    isLongPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isLongPressed) {
                        Log.d("Ajitesh : ", "long pressed and moved");
                        moveIconToPointer(view, motionEvent, initialY, initialTouchY);
                    } else if ((layoutText.getVisibility() == View.INVISIBLE
                            || layoutText.getVisibility() == View.GONE) && flag_handled == false) {
                        Log.d("Ajitesh : ", "animate and show nudge details");
                        flag_handled = true;
                        AnimationHelper.animateShowNudgeDetails(view, preferences);
                    } else if (layoutText.getVisibility() == View.VISIBLE && flag_handled == false) {
                        Log.d("Ajitesh : ", "animate and hide nudge details");
                        flag_handled = true;
                        AnimationHelper.animateHideNudgeDetails(view, null, preferences);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private void moveIconToPointer(View view, MotionEvent motionEvent, int initialY,
                                   float initialTouchY) {
        View touchAreaLeft = view.findViewById(R.id.touchAreaLeft);
        View touchAreaRight = view.findViewById(R.id.touchAreaRight);
        WindowManager.LayoutParams layoutParams = OverlayIconDisplay.getGeneralLayoutParams();
        if (preferences.getBoolean(Constants.NUDGE_ALIGN_LEFT, true)) {
            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            layoutParams.x = (int) (motionEvent.getRawX());
            layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
        } else {
            layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
            layoutParams.x = (int) (getCenterX() * 2 - motionEvent.getRawX());
            layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
        }
        if (touchAreaLeft != null && touchAreaRight != null) {
            touchAreaLeft.setVisibility(View.GONE);
            touchAreaRight.setVisibility(View.GONE);
        }
        windowManager.updateViewLayout(view, layoutParams);
    }

    class Gesture extends GestureDetector.SimpleOnGestureListener {
        private final View view;
        private int initialTouchY = 0;

        public Gesture(View view) {
            this.view = view;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            initialTouchY = (int) event.getRawY();
            return true;
        }

        public boolean onSingleTapUp(MotionEvent ev) {
            KeyguardAssist.launchUnlockActivity(context);
            windowManager.removeView(view);
            return true;
        }

        public void onLongPress(final MotionEvent motionEvent) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
            long magazineViewedCount = preferences.getLong(Constants.MAGAZINE_VIEWED_COUNT, 0);
            if (magazineViewedCount < Constants.MAX_MAGAZINE_VIEW_COUNT_TO_SHOW_FRESH_ICON_TEXT) {
                AnimationHelper.animateHideNudgeDetails(view, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        layoutText.setVisibility(View.GONE);
                        moveSmallIconToTouchArea(motionEvent, initialTouchY, view);
                        isLongPressed = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                }, preferences);
            }
        }
    }

    private float getCenterX() {
        return context.getResources().getDisplayMetrics().widthPixels / 2;
    }

    private void moveSmallIconToTouchArea(MotionEvent motionEvent, int initialTouchY, View view) {
        WindowManager.LayoutParams layoutParams = OverlayIconDisplay.getGeneralLayoutParams();
        if (preferences.getBoolean(Constants.NUDGE_ALIGN_LEFT, true)) {
            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            layoutParams.x = (int) (motionEvent.getRawX());
            layoutParams.y = (int) (motionEvent.getRawY() - initialTouchY);
        } else {
            layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
            layoutParams.x = (int) (getCenterX() * 2 - motionEvent.getRawX());
            layoutParams.y = (int) (motionEvent.getRawY() - initialTouchY);
        }
        windowManager.updateViewLayout(view, layoutParams);
    }
}