package com.acubeapps.lockscreen.shortcuts;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ritwik on 29/05/16.
 */
public abstract class AbstractLockScreenActivity extends Activity {

    private int darkTranslucent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showOnLockScreen();
        setupTransparentSystemAndStatusBars();
        darkTranslucent = getResources().getColor(R.color.dark_translucent);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setupTransparentSystemAndStatusBars();
        //showOnLockScreen();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        showStatusBar(false);
    }


    private void showStatusBar(boolean visible) {
        if (visible) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


    /**
     * Sets up transparent navigation and status bars in LMP. This method is a no-op for other platform versions.
     */
    private void setupTransparentSystemAndStatusBars() {
        if (!isLollipopOrAbove()) {
            return;
        }

        try {
            Window window = getWindow();
            window.getAttributes().systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darkTranslucent);
            window.setNavigationBarColor(darkTranslucent);
        } catch (Exception e) {
            Log.d("Ajitesh : ", "Unable to hideIcon status bar");
        }
    }

    public boolean isLollipopOrAbove() {
        return Build.VERSION.SDK_INT >= 21;
    }


    private void showOnLockScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }
}
