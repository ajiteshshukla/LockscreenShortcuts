package com.acubeapps.lockscreen.shortcuts.core.events;

/**
 * Created by indersingh on 7/6/16.
 */

public abstract class ScreenEvent {

    protected ScreenEventsType lastEventType;

    public abstract ScreenEventsType getLastEventType();

    abstract ScreenEventsType getType();

}
