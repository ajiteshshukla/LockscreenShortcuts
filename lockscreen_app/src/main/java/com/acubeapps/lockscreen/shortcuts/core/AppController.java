package com.acubeapps.lockscreen.shortcuts.core;

import com.acubeapps.lockscreen.shortcuts.Constants;
import com.acubeapps.lockscreen.shortcuts.core.ad.AdApi;
import com.acubeapps.lockscreen.shortcuts.core.card.CardController;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenEventsType;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOffEvent;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOnEvent;
import com.acubeapps.lockscreen.shortcuts.core.events.UserOnHomeScreenEvent;
import com.acubeapps.lockscreen.shortcuts.core.icon.Icon;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconController;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconEventListener;
import com.inmobi.oem.thrift.ad.model.TMagazine;

import android.content.SharedPreferences;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.inject.Inject;

/**
 * Created by ritwik on 29/05/16.
 */
public class AppController {

    public static final int MAX_SHOW_COUNT = 3;
    private static final int THROTTLE_COUNT = 10;
    private static final int FIRST_CALL_COUNT = 0;
    private boolean isFreshIcon = true;
    private IconController iconController;
    private CardController cardController;
    private AdApi adApi;
    private EventBus eventBus;
    private AtomicInteger adCallCount = new AtomicInteger(0);

    private final Lock currentIconLock = new ReentrantLock();
    private volatile IconAndCard currentIconAndCard = null;
    private volatile int currentIconShownCounter = 0;
    private SharedPreferences preferences;

    @Inject
    public AppController(IconController iconController, CardController cardController, AdApi adApi,
                         EventBus eventBus, SharedPreferences preferences) {
        this.iconController = iconController;
        this.cardController = cardController;
        this.adApi = adApi;
        this.eventBus = eventBus;
        this.preferences = preferences;
        this.eventBus.register(this);
    }

    public void showIcon(final IconAndCard iconAndCard) {
        Timber.d("showIcon(%s)", iconAndCard);
        IconEventListener iconEventListener = new IconEventListener() {
            @Override
            public void onTap(Icon icon) {
                cardController.showCard(iconAndCard.card);
            }

            @Override
            public void onOutsideFling(Icon icon) {

            }

            @Override
            public void onOutSideTouch(Icon icon) {

            }
        };
        iconController.showIcon(iconAndCard.icon, iconEventListener, isFreshIcon);
    }

    @Subscribe
    public void onScreenOnEvent(ScreenOnEvent event) {
        Timber.d("onScreenOnEvent()");
        final IconAndCard iconAndCard;
        if (isUserOnHomeScreenFingerPrintUnlock(event)
                || isScreenOnWithoutScreenOff(event)) {
            return;
        }
        currentIconLock.lock();
        try {
            iconAndCard = currentIconAndCard;
            if (iconAndCard == null) {
                reset();
                return;
            }

            if (currentIconShownCounter > MAX_SHOW_COUNT) {
                reset();
                return;
            }
        } finally {
            currentIconLock.unlock();
        }
        adApi.canShow(iconAndCard, new AdApi.CanShowCallback() {
            @Override
            public void onCanShowResult(IconAndCard icon, boolean canShow) {
                currentIconLock.lock();
                try {
                    if (canShow) {
                        showIcon(iconAndCard);
                        currentIconShownCounter++;
                    } else {
                        reset();
                    }
                } finally {
                    currentIconLock.unlock();
                }
            }
        });
    }

    private boolean isScreenOnWithoutScreenOff(ScreenOnEvent screenOnEvent) {
      if (screenOnEvent.getLastEventType() != ScreenEventsType.SCREEN_OFF) {
          return true;
      }
        return false;
    }

    private boolean isUserOnHomeScreenFingerPrintUnlock(ScreenOnEvent screenOnEvent) {
        if (screenOnEvent.getLastEventType() == ScreenEventsType.USER_PRESENT_HOME_SCREEN) {
           return true;
        }
        return false;
    }

    private void reset() {
        Timber.d("reset()");
        currentIconShownCounter = 0;
        currentIconAndCard = null;
    }

    @Subscribe
    public void onScreenOffEvent(ScreenOffEvent event) {
        Timber.d("onScreenOffEvent()");
        hide();
        fetchIconIfStale();
    }

    private void fetchIconIfStale() {
        Timber.d("fetchIconIfStale()");
        currentIconLock.lock();
        try {
            IconAndCard iconAndCard = currentIconAndCard;
            if (iconAndCard == null) {
                if (adCallCount.get() == FIRST_CALL_COUNT || adCallCount.get() > THROTTLE_COUNT) {
                    if (adCallCount.get() > THROTTLE_COUNT) {
                        adCallCount.set(1);
                    }
                    Timber.d("fetching ads");
                    adCallCount.addAndGet(1);
                    adApi.fetchAds(new AdApi.AdCallback() {
                        @Override
                        public void onAdReceived(IconAndCard iconAndCard) {
                            Timber.d("onAdReceived(%s)", iconAndCard);
                            currentIconLock.lock();
                            try {
                                currentIconAndCard = iconAndCard;
                            } finally {
                                currentIconLock.unlock();
                            }
                            adCallCount.set(0);
                            //Reset videos viewed count and magazine view count as its a new magazine
                            if (currentIconAndCard.ad.isSetMagazine()) {
                                TMagazine tMagazine = currentIconAndCard.ad.getMagazine();
                                String magazineId = tMagazine.getId();
                                String previousMagazineId = preferences.getString(
                                        Constants.MAGAZINE_ID, null);
                                if (previousMagazineId == null
                                        || !previousMagazineId.equals(magazineId)) {
                                    isFreshIcon = true;
                                    preferences.edit().putLong(Constants.VIDEO_VIEWED_COUNT, 0)
                                            .apply();
                                    preferences.edit().putLong(Constants.MAGAZINE_VIEWED_COUNT, 0)
                                            .apply();
                                    preferences.edit().putString(Constants.MAGAZINE_ID, magazineId)
                                            .apply();
                                }
                            }
                        }

                        @Override
                        public void onAdFetchFailed() {
                            Timber.d("onAdFetchFailed()");
                            adCallCount.set(0);
                        }

                        @Override
                        public void onAdFetchStarted() {
                            Timber.d("onAdFetchStarted()");
                        }

                        @Override
                        public void onAdFetchFinished() {
                            Timber.d("onAdFetchFinished()");
                            adCallCount.set(0);
                        }
                    });
                }
            } else {
                isFreshIcon = false;
            }
        } finally {
            currentIconLock.unlock();
        }
    }


    @Subscribe
    public void onUserOnHomeScreenEvent(UserOnHomeScreenEvent event) {
        Timber.d("onUserOnHomeScreenEvent()");
        hide();
    }

    private void hide() {
        Timber.d("hide()");
        iconController.hideIcon();
        cardController.hideCard();
    }

    public void start() {
        Timber.d("start()");
    }

    public void stop() {
        Timber.d("stop()");
    }
}
