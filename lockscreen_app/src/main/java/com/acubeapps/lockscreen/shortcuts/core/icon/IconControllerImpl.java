package com.acubeapps.lockscreen.shortcuts.core.icon;

import android.support.annotation.NonNull;

import timber.log.Timber;

import javax.inject.Inject;

/**
 * Created by ritwik on 29/05/16.
 */
public class IconControllerImpl implements IconController {

    private final IconDisplayFactory iconFactory;

    private Icon currentIcon;
    private IconDisplay currentDisplay;

    @Inject
    public IconControllerImpl(IconDisplayFactory iconFactory) {
        this.iconFactory = iconFactory;
    }

    @Override
    public void showIcon(@NonNull Icon icon, final IconEventListener iconEventListener,
                         boolean isFreshIcon) {
        if (currentIcon != null) {
            hideIcon();
        }
        currentIcon = icon;
        currentDisplay = iconFactory.getIconDisplay(currentIcon, new IconEventListener() {
            @Override
            public void onTap(Icon icon) {
                hideIconDisplay();
                iconEventListener.onTap(icon);
            }

            @Override
            public void onOutsideFling(Icon icon) {
                hideIcon();
                iconEventListener.onOutsideFling(icon);
            }

            @Override
            public void onOutSideTouch(Icon icon) {
                hideIcon();
                iconEventListener.onOutSideTouch(icon);
            }
        });

        currentDisplay.show(isFreshIcon);
    }

    @Override
    public void showCurrentIcon() {
        Timber.i("showCurrentIcon");
        if (currentIcon != null && currentDisplay != null) {
            currentDisplay.show(false);
        }
    }

    @Override
    public void hideIcon() {
        Timber.d("hideIcon");
        if (currentDisplay == null) {
            return;
        }
        currentDisplay.hide();
        currentDisplay = null;
        currentIcon = null;
    }

    public void hideIconDisplay() {
        Timber.d("hideIconDisplay");
        if (currentDisplay == null) {
            return;
        }
        currentDisplay.hide();
    }

}
