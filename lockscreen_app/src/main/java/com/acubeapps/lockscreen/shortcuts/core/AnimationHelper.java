package com.acubeapps.lockscreen.shortcuts.core;

import com.acubeapps.lockscreen.shortcuts.Constants;
import com.acubeapps.lockscreen.shortcuts.R;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

/**
 * Created by ajitesh.shukla on 7/7/16.
 */
public final class AnimationHelper {

    private AnimationHelper() {

    }

    public static void animateHideNudgeDetails(final View view, Animation.AnimationListener animationListener,
                                               SharedPreferences preferences) {
        final View nudgeView = view.findViewById(R.id.layoutText);
        ScaleAnimation scaleAnimation;
        if (nudgeView != null && nudgeView.getVisibility() == View.VISIBLE) {
            if (preferences.getBoolean(Constants.NUDGE_ALIGN_LEFT, true)) {
                scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f);
            } else {
                scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 1.0f);
            }
            scaleAnimation.setDuration(500);
            scaleAnimation.setInterpolator(new AccelerateInterpolator());
            if (animationListener == null) {
                animationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        nudgeView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
            }
            scaleAnimation.setAnimationListener(animationListener);
            nudgeView.startAnimation(scaleAnimation);
        }
    }

    public static void animateShowNudgeDetails(View view, SharedPreferences preferences) {
        ScaleAnimation scaleAnimation;
        if (preferences.getBoolean(Constants.NUDGE_ALIGN_LEFT, true)) {
            scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f);
        } else {
            scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f);
        }
        scaleAnimation.setDuration(500);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        View nudgeView = view.findViewById(R.id.layoutText);
        if (nudgeView != null) {
            nudgeView.startAnimation(scaleAnimation);
            nudgeView.setVisibility(View.VISIBLE);
        }
    }

    public static void animateOverlay(final View view, final WindowManager.LayoutParams layoutParams,
                                      MotionEvent motionEvent, final WindowManager windowManager,
                                      final SharedPreferences preferences) {
        final AnimatorSet animatorSet = new AnimatorSet();
        final int initialX = (int) motionEvent.getX();
        final int initialY = (int) motionEvent.getY();
        final int finalX = layoutParams.x;
        final int finalY = layoutParams.y;

        final ValueAnimator animatorY = ValueAnimator.ofInt(initialY, finalY);
        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                try {
                    layoutParams.x = initialX;
                    layoutParams.y = (Integer) valueAnimator.getAnimatedValue();
                    windowManager.updateViewLayout(view, layoutParams);
                } catch (Exception e) {
                    animatorY.removeAllUpdateListeners();
                }
            }
        });
        animatorY.setDuration(500);
        animatorY.setInterpolator(new AccelerateInterpolator());

        final ValueAnimator animatorX = ValueAnimator.ofInt(initialX, finalX);
        animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                try {
                    layoutParams.x = (Integer) valueAnimator.getAnimatedValue();
                    layoutParams.y = finalY;
                    windowManager.updateViewLayout(view, layoutParams);
                    if (layoutParams.x == finalX) {
                        long magazineViewedCount = preferences.getLong(Constants.MAGAZINE_VIEWED_COUNT, 0);
                        if (magazineViewedCount < Constants.MAX_MAGAZINE_VIEW_COUNT_TO_SHOW_FRESH_ICON_TEXT) {
                            animateShowNudgeDetails(view, preferences);
                        }
                    }
                } catch (Exception e) {
                    animatorX.removeAllUpdateListeners();
                }
            }
        });
        animatorX.setDuration(500);
        animatorX.setInterpolator(new DecelerateInterpolator());

        animatorSet.playSequentially(animatorY, animatorX);
        animatorSet.start();
    }
}
