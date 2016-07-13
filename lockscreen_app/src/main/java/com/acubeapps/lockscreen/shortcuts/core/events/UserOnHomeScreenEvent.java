package com.acubeapps.lockscreen.shortcuts.core.events;

/**
 * Created by ritwik on 29/05/16.
 */
public final class UserOnHomeScreenEvent extends ScreenEvent {

  //  public static final UserOnHomeScreenEvent INSTANCE = new UserOnHomeScreenEvent();

    public UserOnHomeScreenEvent(ScreenEventsType lastEventType) {
        this.lastEventType = lastEventType;
    }

    @Override
    public ScreenEventsType getLastEventType() {
        return lastEventType;
    }

    @Override
    ScreenEventsType getType() {
        return ScreenEventsType.USER_PRESENT_HOME_SCREEN;
    }
}
