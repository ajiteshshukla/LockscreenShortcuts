package com.acubeapps.lockscreen.shortcuts.core.events;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */

public abstract class ScreenEvent {

    protected ScreenEventsType lastEventType;

    public abstract ScreenEventsType getLastEventType();

    abstract ScreenEventsType getType();

}
