package com.acubeapps.lockscreen.shortcuts.core.events;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
public final class ScreenOffEvent extends ScreenEvent {

    public ScreenOffEvent(ScreenEventsType lastEventType) {
        this.lastEventType = lastEventType;
    }

    @Override
    public ScreenEventsType getLastEventType() {
        return lastEventType;
    }

    @Override
    ScreenEventsType getType() {
        return ScreenEventsType.SCREEN_OFF;
    }
}
