package com.acubeapps.lockscreen.shortcuts.core.icon;

import android.net.Uri;

/**
 * Created by ritwik on 29/05/16.
 */
public interface Icon {

    Uri getIconUri();

    String getTitle();

    String getTagline();

    Object getAd();

}
