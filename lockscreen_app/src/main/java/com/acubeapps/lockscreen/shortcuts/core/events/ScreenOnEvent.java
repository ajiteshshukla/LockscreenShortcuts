package com.acubeapps.lockscreen.shortcuts.core.events;

/**
 * Created by ritwik on 29/05/16.
 */
public final class ScreenOnEvent extends ScreenEvent {

   // public static final ScreenOnEvent INSTANCE = new ScreenOnEvent();

    public ScreenOnEvent(ScreenEventsType lastEventType) {
       this.lastEventType = lastEventType;
    }

    @Override
    public ScreenEventsType getLastEventType() {
        return lastEventType;
    }

    @Override
    ScreenEventsType getType() {
        return ScreenEventsType.SCREEN_ON;
    }
}
