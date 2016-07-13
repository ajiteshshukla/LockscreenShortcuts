package com.acubeapps.lockscreen.shortcuts.core.card;

import com.acubeapps.lockscreen.shortcuts.cards.AppAdActivity;
import com.acubeapps.lockscreen.shortcuts.cards.DodActivity;
import com.acubeapps.lockscreen.shortcuts.cards.InterimVideoBrandAdActivity;
import com.acubeapps.lockscreen.shortcuts.cards.MovieAdActivity;
import com.acubeapps.lockscreen.shortcuts.cards.RestaurantAdActivity;
import com.acubeapps.lockscreen.shortcuts.cards.StaggeredMagazineActivity;
import com.acubeapps.lockscreen.shortcuts.cards.TaxiAdActivity;
import com.acubeapps.lockscreen.shortcuts.cards.VideoBrandAdActivity;
import com.acubeapps.lockscreen.shortcuts.core.events.HideCardActivityEvent;
import com.acubeapps.lockscreen.shortcuts.utils.CardControllerUtils;

import android.content.Context;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

/**
 * Created by ritwik on 29/05/16.
 */
public class ActivityCardController implements CardController {

    private final Context context;
    private final EventBus eventBus;
    private Card currentCard;

    public ActivityCardController(Context context, EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
    }

    @Override
    public void showCard(Card card) {
        hideCard();
        currentCard = card;
        if (card instanceof AppAdCard) {
            CardControllerUtils.launchActivity(card, AppAdActivity.class, context);
        } else if (card instanceof DodAdCard) {
            CardControllerUtils.launchActivity(card, DodActivity.class, context);
        } else if (card instanceof MovieAdCard) {
            CardControllerUtils.launchActivity(card, MovieAdActivity.class, context);
        } else if (card instanceof TaxiAdCard) {
            CardControllerUtils.launchActivity(card, TaxiAdActivity.class, context);
        } else if (card instanceof RestaurantAdCard) {
            CardControllerUtils.launchActivity(card, RestaurantAdActivity.class, context);
        } else if (card instanceof VideoBrandAdCard) {
            CardControllerUtils.launchActivity(card, InterimVideoBrandAdActivity.class, context);
        } else if (card instanceof ImageBrandAdCard) {
            //TODO implement ImageBrandAdActivity
        } else if (card instanceof MagazineAdCard) {
            CardControllerUtils.launchActivity(card, StaggeredMagazineActivity.class, context);
        } else {
            Timber.e("Unknown card type : %s", card);
        }
    }

    @Override
    public void hideCard() {
        if (currentCard == null) {
            return;
        }
        if (currentCard instanceof AppAdCard) {
            hideActivity(currentCard, AppAdActivity.class);
        } else if (currentCard instanceof DodAdCard) {
            hideActivity(currentCard, DodActivity.class);
        } else if (currentCard instanceof MovieAdCard) {
            hideActivity(currentCard, MovieAdActivity.class);
        } else if (currentCard instanceof TaxiAdCard) {
            hideActivity(currentCard, TaxiAdActivity.class);
        } else if (currentCard instanceof RestaurantAdCard) {
            hideActivity(currentCard, RestaurantAdActivity.class);
        } else if (currentCard instanceof VideoBrandAdCard) {
            hideActivity(currentCard, VideoBrandAdActivity.class);
            hideActivity(currentCard, InterimVideoBrandAdActivity.class);
        } else if (currentCard instanceof MagazineAdCard) {
            hideActivity(currentCard, StaggeredMagazineActivity.class);
        } else {
            Timber.e("Unknown card type : %s", currentCard);
        }
    }

    private void hideActivity(Card card, Class<?> clazz) {
        eventBus.post(new HideCardActivityEvent(card, clazz));
    }

}
