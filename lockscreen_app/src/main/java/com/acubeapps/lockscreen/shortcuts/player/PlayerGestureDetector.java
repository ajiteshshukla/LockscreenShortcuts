package com.acubeapps.lockscreen.shortcuts.player;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by anshul.srivastava on 05/07/16.
 */
public class PlayerGestureDetector implements GestureDetector.OnGestureListener, View.OnTouchListener {

    private GestureDetector gestureDetector;
    private GestureEventListener gestureEventListener;
    private boolean detectScrollDirection = true;
    private boolean isScrollHorizontal;

    public PlayerGestureDetector(Context context, GestureEventListener gestureEventListener) {
        gestureDetector = new GestureDetector(context, this);
        this.gestureEventListener = gestureEventListener;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        gestureEventListener.onTap();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float v1, float v2) {
        if (detectScrollDirection) {
            isScrollHorizontal = Math.abs(v1) > Math.abs(v2);
            detectScrollDirection = false;

            if (isScrollHorizontal) {
                gestureEventListener.onHorizontalScrollStarted();
            } else {
                gestureEventListener.onVerticalScrollStarted();
            }
        }

        if (isScrollHorizontal) {
            if (Math.abs(v1) > 0) {
                gestureEventListener.onHorizontalScroll(e2, -v1);
            }
        } else {
            if (Math.abs(v2) > 0) {
                gestureEventListener.onVerticalScroll(e2, -v2);
            }
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1,
                           float v1, float v2) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            detectScrollDirection = true;
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (isScrollHorizontal) {
                gestureEventListener.onActionUpAfterHorizontalScroll();
            } else {
                gestureEventListener.onActionUpAfterVerticalScroll();
            }
        }
        return gestureDetector.onTouchEvent(motionEvent);
    }

}
