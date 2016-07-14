package com.acubeapps.lockscreen.shortcuts.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by aasha.medhi on 7/13/16.
 */
public class WaltToGraph {
    private static Typeface mFont = null;

    private static Typeface getFontTypeface(Context context) {
        if (mFont == null)
            mFont = Typeface.createFromAsset(context.getAssets(), "fonts/waltographUI.ttf");
        return mFont;
    }

    public static TextView applyFont(Context context, TextView textView) {
        if (textView != null)
            textView.setTypeface(getFontTypeface(context));
        return textView;
    }
}

