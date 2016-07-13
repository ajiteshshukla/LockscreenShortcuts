package com.acubeapps.lockscreen.shortcuts.core.events;

/**
 * Created by ritwik on 29/05/16.
 */
public final class ScreenOffEvent extends ScreenEvent {

   // public static final ScreenOffEvent INSTANCE = new ScreenOffEvent();

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
