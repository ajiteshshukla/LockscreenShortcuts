package com.acubeapps.lockscreen.shortcuts.core.icon;

import javax.inject.Inject;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
public class IconControllerImpl implements IconController {

    private final IconDisplayFactory iconFactory;

    private IconDisplay currentDisplay;

    @Inject
    public IconControllerImpl(IconDisplayFactory iconFactory) {
        this.iconFactory = iconFactory;
    }

    @Override
    public void showIcon() {
        currentDisplay = iconFactory.getIconDisplay();
        currentDisplay.show();
    }

    @Override
    public void hideIcon() {
        if (currentDisplay == null) {
            return;
        }
        currentDisplay.hide();
        currentDisplay = null;
    }

}
