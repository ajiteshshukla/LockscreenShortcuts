package com.acubeapps.lockscreen.shortcuts.utils;

import com.acubeapps.lockscreen.shortcuts.core.card.Card;

import android.content.Context;
import android.content.Intent;

/**
 * Created by ajitesh.shukla on 6/12/16.
 */
public final class CardControllerUtils {

    public static final String CARD_EXTRA_KEY = "card";

    private CardControllerUtils() {

    }

    public static void launchActivity(Card card, Class<?> clazz, Context context) {
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(CARD_EXTRA_KEY, card);
        context.startActivity(intent);
    }
}
