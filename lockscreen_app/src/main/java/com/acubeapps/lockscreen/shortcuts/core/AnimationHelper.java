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

    public static void animateNonDescriptiveNudge(final View view) {
        final View view1 = view.findViewById(R.id.imgFirst);
        final View view2 = view.findViewById(R.id.imgSecond);
        final View view3 = view.findViewById(R.id.imgThird);
        final View view4 = view.findViewById(R.id.imgFourth);

        final ObjectAnimator objectAnimatorUpFirst = ObjectAnimator.ofFloat(view1, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpFirst.setDuration(300);
        objectAnimatorUpFirst.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownFirst = ObjectAnimator.ofFloat(view1, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownFirst.setDuration(300);
        objectAnimatorDownFirst.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpSecond = ObjectAnimator.ofFloat(view2, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpSecond.setDuration(300);
        objectAnimatorUpSecond.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownSecond = ObjectAnimator.ofFloat(view2, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownSecond.setDuration(300);
        objectAnimatorDownSecond.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpThird = ObjectAnimator.ofFloat(view3, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpThird.setDuration(300);
        objectAnimatorUpThird.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownThird = ObjectAnimator.ofFloat(view3, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownThird.setDuration(300);
        objectAnimatorDownThird.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpFourth = ObjectAnimator.ofFloat(view4, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpFourth.setDuration(300);
        objectAnimatorUpFourth.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownFourth = ObjectAnimator.ofFloat(view4, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownFourth.setDuration(300);
        objectAnimatorDownFourth.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpFifth = ObjectAnimator.ofFloat(view4, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpFourth.setDuration(200);
        objectAnimatorUpFourth.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownFifth = ObjectAnimator.ofFloat(view4, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownFourth.setDuration(200);
        objectAnimatorDownFourth.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpSixth = ObjectAnimator.ofFloat(view3, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpFourth.setDuration(200);
        objectAnimatorUpFourth.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownSixth = ObjectAnimator.ofFloat(view3, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownFourth.setDuration(200);
        objectAnimatorDownFourth.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpSeventh = ObjectAnimator.ofFloat(view2, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpFourth.setDuration(200);
        objectAnimatorUpFourth.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownSeventh = ObjectAnimator.ofFloat(view2, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownFourth.setDuration(200);
        objectAnimatorDownFourth.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpEigth = ObjectAnimator.ofFloat(view1, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpFourth.setDuration(200);
        objectAnimatorUpFourth.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownEigth = ObjectAnimator.ofFloat(view1, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownFourth.setDuration(200);
        objectAnimatorDownFourth.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpNinth = ObjectAnimator.ofFloat(view1, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpFourth.setDuration(200);
        objectAnimatorUpFourth.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownNinth = ObjectAnimator.ofFloat(view1, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownFourth.setDuration(200);
        objectAnimatorDownFourth.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpTenth = ObjectAnimator.ofFloat(view2, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpFourth.setDuration(200);
        objectAnimatorUpFourth.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownTenth = ObjectAnimator.ofFloat(view2, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownFourth.setDuration(200);
        objectAnimatorDownFourth.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpEleven = ObjectAnimator.ofFloat(view3, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpFourth.setDuration(200);
        objectAnimatorUpFourth.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownEleven = ObjectAnimator.ofFloat(view3, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownFourth.setDuration(200);
        objectAnimatorDownFourth.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator objectAnimatorUpTwelve = ObjectAnimator.ofFloat(view4, View.SCALE_X.getName(), 7.0f);
        objectAnimatorUpFourth.setDuration(200);
        objectAnimatorUpFourth.setInterpolator(new AccelerateInterpolator());

        final ObjectAnimator objectAnimatorDownTwelve = ObjectAnimator.ofFloat(view4, View.SCALE_X.getName(), 1.0f);
        objectAnimatorDownFourth.setDuration(200);
        objectAnimatorDownFourth.setInterpolator(new DecelerateInterpolator());

        final AnimatorSet animatorSet = new AnimatorSet();
        final AnimatorSet animatorSet2 = new AnimatorSet();
        final AnimatorSet animatorSet3 = new AnimatorSet();
        final AnimatorSet animatorSet4 = new AnimatorSet();
        final AnimatorSet animatorSet5 = new AnimatorSet();
        final AnimatorSet animatorSet6 = new AnimatorSet();
        final AnimatorSet animatorSet7 = new AnimatorSet();
        final AnimatorSet animatorSet8 = new AnimatorSet();
        final AnimatorSet animatorSet9 = new AnimatorSet();
        final AnimatorSet animatorSet10 = new AnimatorSet();
        final AnimatorSet animatorSet11 = new AnimatorSet();
        final AnimatorSet animatorSet12 = new AnimatorSet();

        animatorSet.playSequentially(objectAnimatorUpFirst, objectAnimatorDownFirst);
        animatorSet2.playSequentially(objectAnimatorUpSecond, objectAnimatorDownSecond);
        animatorSet3.playSequentially(objectAnimatorUpThird, objectAnimatorDownThird);
        animatorSet4.playSequentially(objectAnimatorUpFourth, objectAnimatorDownFourth);

        animatorSet5.playSequentially(objectAnimatorUpFifth, objectAnimatorDownFifth);
        animatorSet6.playSequentially(objectAnimatorUpSixth, objectAnimatorDownSixth);
        animatorSet7.playSequentially(objectAnimatorUpSeventh, objectAnimatorDownSeventh);
        animatorSet8.playSequentially(objectAnimatorUpEigth, objectAnimatorDownEigth);

        animatorSet9.playSequentially(objectAnimatorUpNinth, objectAnimatorDownNinth);
        animatorSet10.playSequentially(objectAnimatorUpTenth, objectAnimatorDownTenth);
        animatorSet11.playSequentially(objectAnimatorUpEleven, objectAnimatorDownEleven);
        animatorSet12.playSequentially(objectAnimatorUpTwelve, objectAnimatorDownTwelve);

        animatorSet.setStartDelay(500 + 0);
        animatorSet2.setStartDelay(500 + 160);
        animatorSet3.setStartDelay(500 + 320);
        animatorSet4.setStartDelay(500 + 480);

        animatorSet5.setStartDelay(1500 + 0);
        animatorSet6.setStartDelay(1500 + 160);
        animatorSet7.setStartDelay(1500 + 320);
        animatorSet8.setStartDelay(1500 + 480);

        animatorSet9.setStartDelay(2500 + 0);
        animatorSet10.setStartDelay(2500 + 160);
        animatorSet11.setStartDelay(2500 + 320);
        animatorSet12.setStartDelay(2500 + 480);

        animatorSet.start();
        animatorSet2.start();
        animatorSet3.start();
        animatorSet4.start();
        animatorSet5.start();
        animatorSet6.start();
        animatorSet7.start();
        animatorSet8.start();
        animatorSet9.start();
        animatorSet10.start();
        animatorSet11.start();
        animatorSet12.start();
    }

    public static void alignIconViewToParent(View view, boolean isLeftAligned) {
        final View imageFirst = view.findViewById(R.id.imgFirst);
        final View imageSecond = view.findViewById(R.id.imgSecond);
        final View imageThird = view.findViewById(R.id.imgThird);
        final View imageFourth = view.findViewById(R.id.imgFourth);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                imageFirst.getLayoutParams();
        if (isLeftAligned) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        imageFirst.setLayoutParams(params);
        params = (RelativeLayout.LayoutParams)
                imageSecond.getLayoutParams();
        if (isLeftAligned) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        imageSecond.setLayoutParams(params);
        params = (RelativeLayout.LayoutParams)
                imageThird.getLayoutParams();
        if (isLeftAligned) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        imageThird.setLayoutParams(params);
        params = (RelativeLayout.LayoutParams)
                imageFourth.getLayoutParams();
        if (isLeftAligned) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        imageFourth.setLayoutParams(params);
    }
}
