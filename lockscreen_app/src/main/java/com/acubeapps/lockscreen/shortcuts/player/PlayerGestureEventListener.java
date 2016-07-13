package com.acubeapps.lockscreen.shortcuts.player;

import com.acubeapps.lockscreen.shortcuts.cards.DemoUtils;
import com.acubeapps.lockscreen.shortcuts.utils.Device;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by anshul.srivastava on 05/07/16.
 */
public class PlayerGestureEventListener implements GestureEventListener {

    private Context context;
    private NativePlayerTextureView nativePlayerTextureView;
    private SeekBar videoPositionSeekBar;
    private SeekBar volumeSeekBar;
    private TextView textProgress;
    private Handler handler;
    private AtomicInteger numHandlerJobs = new AtomicInteger();

    public PlayerGestureEventListener(Context context, NativePlayerTextureView nativePlayerTextureView) {
        this.context = context;
        this.nativePlayerTextureView = nativePlayerTextureView;
        this.videoPositionSeekBar = nativePlayerTextureView.getVideoPositionSeekbar();
        this.volumeSeekBar = nativePlayerTextureView.getVolumeSeekbar();
        this.textProgress = nativePlayerTextureView.getTextProgress();
        handler = new Handler();
        numHandlerJobs.set(0);
    }

    @Override
    public void onTap() {
        nativePlayerTextureView.updateControlsInteractedFlag(true);
        nativePlayerTextureView.showControlsAndDismiss();
        nativePlayerTextureView.pressPlayPauseButton();
    }

    @Override
    public void onHorizontalScroll(MotionEvent event, float delta) {
        int currentProgress = videoPositionSeekBar.getProgress();
        int newProgress = scaleDeltaAndCalculateNewValue(nativePlayerTextureView.getVideoFrame().getWidth(),
                delta, videoPositionSeekBar.getMax(), currentProgress);

        if (currentProgress != newProgress) {
            videoPositionSeekBar.setProgress(newProgress);
            nativePlayerTextureView.setPlayerPosition(newProgress * 1000);
        }
    }

    @Override
    public void onHorizontalScrollStarted() {

    }

    @Override
    public void onActionUpAfterHorizontalScroll() {

    }

    @Override
    public void onVerticalScroll(MotionEvent event, float delta) {
        int maxVolume = Device.getMaxDeviceMediaVolume(context);
        int currentVolume = Device.getDeviceMediaVolume(context);
        int newVolume = scaleDeltaAndCalculateNewValue(nativePlayerTextureView.getVideoFrame().getHeight(), -delta,
                maxVolume, currentVolume);
        if (newVolume != currentVolume) {
            volumeSeekBar.setProgress(newVolume);
        }
    }

    @Override
    public void onVerticalScrollStarted() {
        if (videoPositionSeekBar.getVisibility() == View.VISIBLE) {
            videoPositionSeekBar.setVisibility(View.INVISIBLE);
            volumeSeekBar.setVisibility(View.VISIBLE);
            textProgress.setText(volumeSeekBar.getProgress() * 100 / volumeSeekBar.getMax() + "%");
        }
    }

    @Override
    public void onActionUpAfterVerticalScroll() {
        if (volumeSeekBar.getVisibility() == View.VISIBLE) {
            numHandlerJobs.incrementAndGet();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int val = numHandlerJobs.decrementAndGet();
                    if (val <= 0) {
                        volumeSeekBar.setVisibility(View.GONE);
                        videoPositionSeekBar.setVisibility(View.VISIBLE);
                        textProgress.setText(DemoUtils.getFormattedTimeRemaining(
                                videoPositionSeekBar.getProgress(), videoPositionSeekBar.getMax()));
                    }
                }
            }, 3000);
        }
    }

    private int scaleDeltaAndCalculateNewValue(int availableSpace, float delta, int maxValue, int currentValue) {

        float scale;
        float newValue = currentValue;

        scale = delta / (float) availableSpace;
        newValue += scale * maxValue;

        if (newValue < 0) {
            newValue = 0;
        } else if (newValue > maxValue) {
            newValue = maxValue;
        }

        return Math.round(newValue);
    }

}
