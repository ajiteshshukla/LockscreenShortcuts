package com.acubeapps.lockscreen.shortcuts.player;

import android.view.MotionEvent;

/**
 * Created by anshul.srivastava on 05/07/16.
 */
public interface GestureEventListener {

    void onTap();

    void onHorizontalScroll(MotionEvent event, float delta);

    void onHorizontalScrollStarted();

    void onActionUpAfterHorizontalScroll();

    void onVerticalScroll(MotionEvent event, float delta);

    void onVerticalScrollStarted();

    void onActionUpAfterVerticalScroll();


}
