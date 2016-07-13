package com.acubeapps.lockscreen.shortcuts.core.events;

import com.acubeapps.lockscreen.shortcuts.core.card.Card;

/**
 * Created by ritwik on 30/05/16.
 */
public class HideCardActivityEvent {

    final Card card;
    final Class<?> clazz;

    public HideCardActivityEvent(Card card, Class<?> clazz) {
        this.card = card;
        this.clazz = clazz;
    }

    public Card getCard() {
        return card;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
