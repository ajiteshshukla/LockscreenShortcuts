package com.acubeapps.lockscreen.shortcuts.core.card;

import com.acubeapps.lockscreen.shortcuts.BuildConfig;
import com.acubeapps.lockscreen.shortcuts.Constants;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.analytics.Analytics;
import com.acubeapps.lockscreen.shortcuts.cards.DemoUtils;
import com.acubeapps.lockscreen.shortcuts.core.icon.IconController;
import com.acubeapps.lockscreen.shortcuts.player.DemoPlayer;
import com.acubeapps.lockscreen.shortcuts.player.NativePlayerTextureView;
import com.acubeapps.lockscreen.shortcuts.utils.Device;
import com.acubeapps.lockscreen.shortcuts.utils.Utils;
import com.acubeapps.lockscreen.shortcuts.utils.WebViewHijacker;
import com.acubeapps.lockscreen.shortcuts.video.VideoService;
import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TContent;
import com.inmobi.oem.thrift.ad.model.TMagazine;
import com.inmobi.oem.thrift.ad.model.TVideo;

import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.drm.UnsupportedDrmException;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by aasha.medhi on 20/06/16.
 */
public abstract class MagazineActivity extends CardActivity<MagazineAdCard> {

    private Handler handler;
    protected NativePlayerTextureView playerView = null;

    private String videoId;
    protected String tileId;
    private long videoDuration;
    private long videoClickedTime;

    private boolean muteState = true;
    private int deviceMediaVolume = 0;
    private ProgressBar progressBar;
    private RelativeLayout videoScreenLayout;
    protected ImageView thumbnailImageView;
    protected FrameLayout overlay;

    TMagazine magazine = null;

    @BindView(R.id.playerLayout)
    RelativeLayout layoutPlayer = null;

    @Inject
    protected Analytics analytics;

    @Inject
    EventBus eventBus;

    @Inject
    protected Picasso picasso;

    @Inject
    VideoService videoService;

    @Inject
    IconController iconController;

    @BindView(R.id.layoutVideoMetadata)
    protected LinearLayout layoutVideoMetadata = null;

    @BindView(R.id.textTitle)
    TextView textTitle = null;

    @BindView(R.id.textLikes)
    TextView textLikeCount = null;

    final Point screenSize = new Point();

    static WebViewHijacker hijacker = new WebViewHijacker();
    private static final int ANIM_DURATION = 200;
    private static final Interpolator ANIM_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private int backgroundColor;

    private ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();
        backgroundColor = getBackgroundColor();

        initialize(eventBus);

        handler = new Handler();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        thumbnailImageView = (ImageView) findViewById(R.id.imageThumbnail);
        closeButton = (ImageView) findViewById(R.id.closeButton);

        closeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Timber.d("overlay on click");
                        closeAnimation(videoScreenLayout, 700);
                    }
                });
        deviceMediaVolume = Device.getDeviceMediaVolume(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        //TODO revisit closing overlay logic
        videoScreenLayout = (RelativeLayout) findViewById(R.id.overlay);
        overlay = (FrameLayout) videoScreenLayout.findViewById(R.id.overlay_background);

        MagazineAdCard card = getCard();
        if (card == null) {
            if (BuildConfig.DEBUG) {
                magazine = getMockMagazine();
            } else {
                finish();
                return;
            }
        } else {
            magazine = card.getAd();
            Timber.d("Magazine not null : %s", magazine);
        }

        logMagazineStartEvent();

        bindData(magazine);
        setStatusAndBottomBarBackground();
        prefetchVideo();
    }

    protected abstract void initialize();

    private void closeVideo() {
        if (videoScreenLayout.getVisibility() == View.VISIBLE) {
            //Save the mute state
            muteState = playerView.isMuted();
            videoScreenLayout.setVisibility(View.GONE);
            layoutVideoMetadata.setVisibility(View.GONE);
            layoutPlayer.removeView(playerView.getVideoFrame());
            playerView.releasePlayer();
            analytics.stopVideoSession(videoId, MagazineActivity.this);
            setStatusAndBottomBarBackground();

            if (!muteState) {
                playerView.setPreviousDeviceVolume(Device.getDeviceMediaVolume(MagazineActivity.this));
            }
        }
    }

    private void prefetchVideo() {
        List<TVideo> videos = new ArrayList<>();
        for (TContent content : magazine.getHeader()) {
            if (content.isSetVideo()) {
                videos.add(content.getVideo());
            }
        }
        for (TContent content : magazine.getContents()) {
            if (content.isSetVideo()) {
                videos.add(content.getVideo());
            }
        }
        videoService.getVideoUrls(videos, new VideoService.Callback() {
            @Override
            public void onVideo(String videoId, String url) {
                // ignore
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(backgroundColor);
            getWindow().setNavigationBarColor(backgroundColor);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerView != null) {
            playerView.releasePlayer();
        }
        logMagazineStopEvent();
        Device.setDeviceMediaVolume(this, deviceMediaVolume);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
    }

    protected abstract void bindData(TMagazine magazine);

    private void setUpPlayer(final TVideo video) {
        playerView = new NativePlayerTextureView(this, analytics);
        boolean isStreaming = (video.getVideoMetadata().isSetHostedMetadata());
        playerView.setVideoMeta(videoId, tileId, videoDuration, videoClickedTime, isStreaming);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        playerView.getVideoFrame().setLayoutParams(params);
        layoutPlayer.addView(playerView.getVideoFrame());

        playerView.setPlayerListener(mOpinionPlayerListener);

        ArrayList<TVideo> videos = new ArrayList<>();
        videos.add(video);
        videoService.getVideoUrls(videos, new VideoService.Callback() {
            @Override
            public void onVideo(final String videoId, final String url) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Uri uri = Uri.parse(url);
                        playerView.setContentUri(uri);
                        playerView.setMute(muteState);
                        playerView.preparePlayer(true, playerView.isStreaming());
                        playerView.setDuration((int) video.getDuration());
                    }
                });
            }
        });
    }

    private void setAnimation(FrameLayout overlay, ImageView imageView,
                              int animDuration, Interpolator animInterpolator, Point screenSize) {

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(animDuration);
        alphaAnimation.setInterpolator(animInterpolator);
        overlay.setAnimation(alphaAnimation);

        final AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        //animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(700);
        animationSet.setInterpolator(animInterpolator);
        imageView.setAnimation(animationSet);

    }

    private void closeAnimation(RelativeLayout relativeLayout, long animDuration) {
        final AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setDuration(animDuration);
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                closeVideo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        relativeLayout.setAnimation(animationSet);
        onCloseOverlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerView != null) {
            playerView.releasePlayer();
        }
        handler.removeCallbacksAndMessages(null);
        if (hasWindowFocus()) {
            iconController.showCurrentIcon();
            Device.setDeviceMediaVolume(this, deviceMediaVolume);
        }
    }

    public void logMagazineStartEvent() {
        String magazineId = magazine.getId();
        this.analytics.startMagazineSession(magazineId);
    }

    public void logMagazineStopEvent() {
        String magazineId = magazine.getId();
        this.analytics.stopMagazineSession(magazineId, this);
    }

    @Override
    public void onBackPressed() {
        if (videoScreenLayout.getVisibility() == View.VISIBLE) {
            closeAnimation(videoScreenLayout, 700);
        } else {
            iconController.showCurrentIcon();
            super.onBackPressed();
        }
    }

    private void setStatusAndBottomBarBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (videoScreenLayout.getVisibility() == View.GONE) {
                getWindow().setStatusBarColor(backgroundColor);
                getWindow().setNavigationBarColor(backgroundColor);
            } else {
                getWindow().setStatusBarColor(Color.BLACK);
                getWindow().setNavigationBarColor(Color.BLACK);
            }
        }
    }

    private DemoPlayer.Listener mOpinionPlayerListener = new DemoPlayer.Listener() {
        @Override
        public void onStateChanged(boolean playWhenReady, int playbackState) {
            String text = "playWhenReady=" + playWhenReady + ", playbackState=";
            switch (playbackState) {
                case ExoPlayer.STATE_BUFFERING:
                    text += "buffering";
                    break;
                case ExoPlayer.STATE_ENDED:
                    analytics.stopVideoSession(videoId, MagazineActivity.this);
                    playerView.releasePlayer();
                    playerView.enableControls();
                    thumbnailImageView.setVisibility(View.VISIBLE);
                    closeAnimation(videoScreenLayout, 700);
                    text += "ended";
                    break;
                case ExoPlayer.STATE_IDLE:
                    text += "idle";
                    break;
                case ExoPlayer.STATE_PREPARING:
                    text += "preparing";
                    break;
                case ExoPlayer.STATE_READY:
                    text += "ready";
                    progressBar.setVisibility(View.GONE);
                    thumbnailImageView.setVisibility(View.GONE);
                    playerView.enableControls();
                    if (thumbnailImageView.getHeight() < overlay.getHeight()) {
                        layoutVideoMetadata.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    text += "unknown";
                    break;
            }
        }

        @Override
        public void onError(Exception exception) {
            if (exception instanceof UnsupportedDrmException) {
                // Special case DRM failures.
                exception.printStackTrace();
            }
            playerView.setPlayerNeedsPrepare(true);
        }


        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                       float pixelWidthAspectRatio) {
            //shutterView.setVisibility(View.GONE);
            playerView.getVideoFrame().setAspectRatio(
                    height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
        }
    };

    public void onClickVideo(TVideo video) {
        videoId = video.getId();
        videoDuration = video.getDuration();
        videoClickedTime = System.currentTimeMillis();
        textTitle.setText(video.getTitle());
        long likeCount = DemoUtils.getLikesCount(video);
        textLikeCount.setText(DemoUtils.format(likeCount) + " likes");

        if (videoScreenLayout.getVisibility() == View.GONE) {
            setUpPlayer(video);
            videoScreenLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            thumbnailImageView.setVisibility(View.VISIBLE);

            float aspectImageWidth = video.getAspectWidth();
            float aspectImageHeight = video.getAspectHeight();
            float aspectImageRatio = aspectImageWidth / aspectImageHeight;

            ViewGroup.LayoutParams imageViewParams = thumbnailImageView.getLayoutParams();
            imageViewParams.height = (int) (screenSize.x / aspectImageRatio);

            thumbnailImageView.setLayoutParams(imageViewParams);

            picasso.load(video.getThumbnailUrl())
                    .into(thumbnailImageView);
            setAnimation(overlay, thumbnailImageView, ANIM_DURATION,
                    ANIM_INTERPOLATOR, screenSize);
            setStatusAndBottomBarBackground();
        }
    }

    private TMagazine getMockMagazine() {
        String data = Utils.loadResource("/mock_magazine.json");
        TAd ad = Constants.THRIFT_GSON.fromJson(data, TAd.class);
        return ad.getMagazine();
    }

    protected abstract int getBackgroundColor();

    protected abstract void onCloseOverlay();
}
