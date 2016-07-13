package com.acubeapps.lockscreen.shortcuts.core.icon;

/**
 * Created by ritwik on 29/05/16.
 */
public interface IconEventListener {

    void onTap(Icon icon);

    void onOutsideFling(Icon icon);

    void onOutSideTouch(Icon icon);

}
