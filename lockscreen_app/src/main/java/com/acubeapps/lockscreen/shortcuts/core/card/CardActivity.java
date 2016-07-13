package com.acubeapps.lockscreen.shortcuts.core.card;

import com.acubeapps.lockscreen.shortcuts.AbstractLockScreenActivity;
import com.acubeapps.lockscreen.shortcuts.core.events.HideCardActivityEvent;
import com.acubeapps.lockscreen.shortcuts.utils.CardControllerUtils;

import android.content.Intent;
import android.os.Bundle;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by ritwik on 29/05/16.
 */
public abstract class CardActivity<C extends Card> extends AbstractLockScreenActivity {

    private C card;
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        card = (C) extras.getParcelable(CardControllerUtils.CARD_EXTRA_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (eventBus == null) {
            throw new IllegalStateException("please call initialize() in onCreate()");
        }
    }

    protected final C getCard() {
        return card;
    }

    protected final void initialize(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    @Subscribe
    public void onHideCardActivityEvent(HideCardActivityEvent event) {
        if (event.getClazz() != getClass()) {
            return;
        }
        finish();
    }
}
