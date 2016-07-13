package com.acubeapps.lockscreen.shortcuts.core.icon;

import android.support.annotation.NonNull;

/**
 * Created by ritwik on 29/05/16.
 */
public interface IconController {

    void showIcon(@NonNull Icon icon, @NonNull IconEventListener iconEventListener,
                  boolean isFreshIcon);

    void showCurrentIcon();

    void hideIcon();
}
