package com.acubeapps.lockscreen.shortcuts.player;

import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.analytics.Analytics;
import com.acubeapps.lockscreen.shortcuts.cards.DemoUtils;
import com.acubeapps.lockscreen.shortcuts.utils.Device;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.util.Util;

/**
 * Created by aasha.medhi on 29/06/16.
 */
public class NativePlayerTextureView implements TextureView.SurfaceTextureListener, View.OnClickListener {

    private static final String TAG = NativePlayerTextureView.class.getName();

    // Context of activity
    private Context mContext = null;

    private Analytics analytics;

    //Current Video Meta
    private String videoId;
    private String tileId;
    private long videoDuration;
    private long videoClickedTime;

    // Layout inflater
    private LayoutInflater mInflater = null;

    // Exo player
    private DemoPlayer mExoPlayer = null;
    private AspectRatioFrameLayout mVideoFrame = null;
    public TextureView mTextureView = null;

    private FrameLayout mControlsOverlay = null;
    private RelativeLayout controlsLayout = null;

    // Listener for clients
    private DemoPlayer.Listener mPlayerStateListener = null;

    // Player STATE flags
    private boolean mPlayerNeedsPrepare;
    private long mPlayerPosition = 0;
    private boolean mIsVideoPlaying = false;
    private int previousDeviceVolume;
    private boolean isMuted = true;
    boolean isControlsShown = false;
    boolean isControlsInteracted = false;
    int maxDuration = 0;

    private Button btnPlay = null;
    private ImageView btnVolumeControl = null;
    private SeekBar seekbarPlayer = null;
    private SeekBar volumeSeekbar = null;
    private TextView textProgress = null;

    // Content uri associated with this player
    private Uri mContentUri = null;
    private Handler handler;

    private boolean isStreaming = false;

    public NativePlayerTextureView(Context context, Analytics analytics) {
        mContext = context;

        this.analytics = analytics;

        handler = new Handler();

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mVideoFrame = (AspectRatioFrameLayout) mInflater.inflate(R.layout.player_view, null);

        mTextureView = (TextureView) mVideoFrame.findViewById(R.id.surface_view);

        //mTextureView.setSurfaceTextureListener(this);

        mControlsOverlay = (FrameLayout) mVideoFrame.findViewById(R.id.controlsOverlayFrame);
        controlsLayout = (RelativeLayout) mVideoFrame.findViewById(R.id.controlsOverlay);

        btnPlay = (Button) mVideoFrame.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnVolumeControl = (ImageView) mVideoFrame.findViewById(R.id.btnVolumeControl);
        btnVolumeControl.setOnClickListener(this);

        seekbarPlayer = (SeekBar) mVideoFrame.findViewById(R.id.playerSeek);
        seekbarPlayer.setProgress(0);

        volumeSeekbar = (SeekBar) mVideoFrame.findViewById(R.id.volumeSeek);
        volumeSeekbar.setMax(Device.getMaxDeviceMediaVolume(context));
        volumeSeekbar.setProgress(Device.getDeviceMediaVolume(context));

        trackSeekBar();
        textProgress = (TextView) mVideoFrame.findViewById(R.id.textProgress);

        mTextureView.setOnTouchListener(new PlayerGestureDetector(context,
                new PlayerGestureEventListener(context, this)));

        previousDeviceVolume = Device.getDeviceMediaVolume(mContext);
    }

    public void preparePlayer(boolean playWhenReady, boolean isStreaming) {
        if (mExoPlayer == null) {
            if (isStreaming) {
                mExoPlayer = new DemoPlayer(getStreamingRendererBuilder());
            } else {
                mExoPlayer = new DemoPlayer(getRendererBuilder());
            }
            if (null != mPlayerStateListener) {
                mExoPlayer.addListener(mPlayerStateListener);
            }
            mPlayerNeedsPrepare = true;
        }
        if (mTextureView.getSurfaceTexture() != null) {
            mExoPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
        }
        if (playWhenReady) {
            play();
        } else {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    public void play() {
        setPlayerPosition((int) mPlayerPosition);
        if (mPlayerNeedsPrepare) {
            mExoPlayer.prepare();
            mPlayerNeedsPrepare = false;
        }
        if (mPlayerPosition == 0) {
            long videoStartTime = System.currentTimeMillis();
            long videoLoadTime = videoStartTime - videoClickedTime;

            analytics.startVideoSession(videoId, tileId, videoDuration, videoLoadTime);
        } else {
            analytics.resumeVideoSession(videoId);
        }
        mExoPlayer.setPlayWhenReady(true);
        mIsVideoPlaying = true;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                seekbarPlayer.setProgress((int) getPlayerPosition() / 1000);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 1000);
    }

    public void pause() {
        if (mExoPlayer != null) {
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.setPlayWhenReady(false);
        }
        analytics.pauseVideoSession(videoId);
        mIsVideoPlaying = false;
    }

    public void setVideoMeta(String videoId, String tileId, long videoDuration, long videoClickedTime,
                             boolean isStreaming) {
        this.videoId = videoId;
        this.tileId = tileId;
        this.videoClickedTime = videoClickedTime;
        this.videoDuration = videoDuration;
        this.isStreaming = isStreaming;
    }

    public void setMute(boolean toMute) {
        if (toMute) {
            btnVolumeControl.setImageResource(R.drawable.mute);
            isMuted = true;
            previousDeviceVolume = Device.getDeviceMediaVolume(mContext);
            Device.setDeviceMediaVolume(mContext, 0);
        } else {
            btnVolumeControl.setImageResource(R.drawable.unmute);
            if (isMuted) {
                isMuted = false;
                Device.setDeviceMediaVolume(mContext, previousDeviceVolume);
            }
        }
    }

    public boolean isMuted() {
        return isMuted;
    }


    public void releasePlayer() {
        if (mExoPlayer != null) {
            mPlayerPosition = 0;
            mExoPlayer.release();
            seekbarPlayer.setProgress(0);
            btnPlay.setBackgroundResource(R.drawable.play_gray);
            mExoPlayer = null;
        }
        mIsVideoPlaying = false;
        handler.removeCallbacksAndMessages(null);

    }

    public long getPlayerPosition() {
        if (mExoPlayer != null) {
            return mExoPlayer.getCurrentPosition();
        }
        return mPlayerPosition;
    }

    public boolean isPlaying() {
        return mIsVideoPlaying;
    }

    protected DemoPlayer.RendererBuilder getStreamingRendererBuilder() {
        String userAgent = Util.getUserAgent(mContext, "ExoPlayerDemo");
        return new HlsRendererBuilder(mContext, userAgent, mContentUri.toString());
    }

    protected DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(mContext, "ExoPlayerDemo");
        return new ExtractorRendererBuilder(mContext, userAgent, mContentUri);
    }

    public AspectRatioFrameLayout getVideoFrame() {
        return mVideoFrame;
    }

    public SeekBar getVolumeSeekbar() {
        return volumeSeekbar;
    }

    public SeekBar getVideoPositionSeekbar() {
        return seekbarPlayer;
    }

    public TextView getTextProgress() {
        return textProgress;
    }

    public void setPlayerPosition(int seekPosition) {
        mPlayerPosition = seekPosition;
        if (mExoPlayer == null) {
            preparePlayer(true, isStreaming);
        } else {
            mExoPlayer.seekTo(mPlayerPosition);
        }
    }

    public void setContentUri(Uri uri) {
        mContentUri = uri;
    }

    public void setPlayerListener(DemoPlayer.Listener listener) {
        mPlayerStateListener = listener;
    }

    public void setPlayerNeedsPrepare(boolean prepare) {
        mPlayerNeedsPrepare = prepare;
    }

    public void enableControls() {
        showControlsAndDismiss();
        mControlsOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isControlsInteracted = true;
                showControlsAndDismiss();
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mExoPlayer != null) {
            mExoPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void showControlsAndDismiss() {
        //Set timer to hide controls
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isControlsInteracted) {
                    isControlsInteracted = false;
                    handler.postDelayed(this, 3000);
                    return;
                }
                if (isControlsShown) {
                    hideControls();
                }
            }
        };
        if (!isControlsShown) {
            showControls();
        } else {
            handler.removeCallbacks(runnable);
        }
        handler.postDelayed(runnable, 3000);
    }

    private void showControls() {
        final Animation animationFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        animationFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                controlsLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        controlsLayout.startAnimation(animationFadeIn);
        isControlsShown = true;
    }

    private void hideControls() {
        final Animation animationFadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        controlsLayout.startAnimation(animationFadeOut);
        animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                controlsLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        isControlsShown = false;
    }

    private void trackSeekBar() {
        seekbarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekbarPlayer.getVisibility() == View.VISIBLE) {
                    textProgress.setText(DemoUtils.getFormattedTimeRemaining(progress, videoDuration));
                }
                mPlayerPosition = progress * 1000;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isControlsInteracted = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setPlayerPosition(seekBar.getProgress() * 1000);
            }
        });

        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean userTouch) {
                Device.setDeviceMediaVolume(mContext, progress);
                if (volumeSeekbar.getVisibility() == View.VISIBLE) {
                    textProgress.setText(progress * 100 / volumeSeekbar.getMax() + "%");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /*
    Video url duration in secs
     */
    public void setDuration(int duration) {
        maxDuration = duration;
        seekbarPlayer.setMax(maxDuration);
    }

    public void updateControlsInteractedFlag(boolean controlsInteracted) {
        this.isControlsInteracted = controlsInteracted;
    }

    public void pressPlayPauseButton() {
        btnPlay.performClick();
    }

    public void setPreviousDeviceVolume(int previousDeviceVolume) {
        this.previousDeviceVolume = previousDeviceVolume;
    }

    public boolean isStreaming() {
        return isStreaming;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:
                isControlsInteracted = true;
                if (mIsVideoPlaying) {
                    btnPlay.setBackgroundResource(R.drawable.play_gray);
                    pause();
                } else {
                    btnPlay.setBackgroundResource(R.drawable.pause_gray);
                    play();
                }
                break;
            case R.id.btnVolumeControl:
                isControlsInteracted = true;
                if (isMuted) {
                    //Video is muted
                    setMute(false);
                } else {
                    setMute(true);
                }
                break;
            default:
                break;
        }
    }
}
