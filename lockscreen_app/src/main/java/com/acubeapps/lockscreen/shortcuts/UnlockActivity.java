package com.acubeapps.lockscreen.shortcuts;

import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOffEvent;
import com.acubeapps.lockscreen.shortcuts.core.events.UserOnHomeScreenEvent;
import com.acubeapps.lockscreen.shortcuts.utils.Device;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.WindowManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

/**
 * Created by ajitesh.shukla on 5/29/16.
 */
public class UnlockActivity extends Activity {

    @Inject
    EventBus eventBus;

    public UnlockActivity() {
    }

    private void setWindowParams() {
        int layoutParams = WindowManager.LayoutParams.TYPE_APPLICATION;
        if (Device.hasKitKatApi()) {
            layoutParams = WindowManager.LayoutParams.TYPE_APPLICATION | 71303168;
        } else if (Device.hasHoneyCombMr2()) {
            layoutParams = WindowManager.LayoutParams.TYPE_APPLICATION | 4194304;
        }
        this.getWindow().setFlags(layoutParams, layoutParams);
    }

    public boolean dispatchKeyEvent(KeyEvent var1) {
        return super.dispatchKeyEvent(var1);
    }

    public void onUserInteraction() {
        super.onUserInteraction();
    }

    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(com.acubeapps.lockscreen.shortcuts.R.layout.layout_empty);
        Injectors.appComponent().injectUnlockActivity(this);
        eventBus.register(this);
        setWindowParams();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.MANUFACTURER.equalsIgnoreCase(Constants.KEY_XIAOMI)) {
            Handler handler = new Handler(getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, Constants.UNLOCK_ACTIVITY_TIMING_DELAY_XIAOMI);
        }
    }

    @Subscribe
    public void onUserPresentEvent(UserOnHomeScreenEvent event) {
        finish();
    }

    @Subscribe
    public void onScreenOff(ScreenOffEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
