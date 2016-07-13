package com.acubeapps.lockscreen.shortcuts.cards;

import com.inmobi.oem.internal.AndroidUtils;
import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.core.card.CardActivity;
import com.acubeapps.lockscreen.shortcuts.core.card.VideoBrandAdCard;
import com.acubeapps.lockscreen.shortcuts.utils.KeyguardAssist;
import com.inmobi.oem.thrift.ad.model.TBrandAd;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import butterknife.BindView;
import butterknife.ButterKnife;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;


/**
 * Created by ajitesh.shukla on 6/9/16.
 */
public class VideoBrandAdActivity extends CardActivity<VideoBrandAdCard> implements View.OnClickListener,
        View.OnTouchListener {

    @BindView(R.id.VideoView)
    VideoView videoView;

    @BindView(R.id.layout_cta)
    RelativeLayout layoutCta;

    @BindView(R.id.close)
    ImageView close;

    @BindView(R.id.cta_text)
    Button ctaText;

    @BindView(R.id.resumeReplay)
    RelativeLayout resumePlayLayout;

    @BindView(R.id.resumeReplayIconText)
    LinearLayout resumePlayIconText;

    @BindView(R.id.resumeReplayText)
    TextView resumeReplayText;

    @BindView(R.id.closeBottom)
    TextView closeBottom;

    @BindView(R.id.knowMore)
    LinearLayout knowMore;

    @Inject
    EventBus eventBus;

    private MediaController mediaController;

    private VideoBrandAdCard videoBrandAdCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_ad);
        ButterKnife.bind(this);
        Injectors.appComponent().injectVideoAdActivity(this);
        initialize(eventBus);
        videoBrandAdCard = getCard();
        if (videoBrandAdCard == null) {
            finish();
            return;
        }
        bindData();
    }

    private void bindData() {
        final TBrandAd ad = videoBrandAdCard.getAd();

        ctaText.setText("Know More");
        ctaText.setOnClickListener(this);
        knowMore.setOnClickListener(this);
        videoView.setVideoURI(videoBrandAdCard.getVideoUri());
        //videoView.setVideoPath(FileUtils.getLocalFilePath(videoBrandAdCard.getVideoUri(), "brandVideo.mp4", this));
        close.setOnClickListener(this);
        closeBottom.setOnClickListener(this);
        mediaController = new MediaController(this);

        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();
        if (mediaController.isShowing()) {
            mediaController.hide();
        }
        videoView.setOnTouchListener(this);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                resumeReplayText.setText("Replay");
                resumePlayLayout.setVisibility(View.VISIBLE);
            }
        });
        resumePlayIconText.setOnClickListener(this);
    }

    private void playVideo() {
        if (!videoView.isPlaying()) {
            videoView.start();
            resumePlayLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void pauseVideo() {
        if (videoView.isPlaying()) {
            videoView.pause();
            resumeReplayText.setText("Resume");
            resumePlayLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == close.getId() || view.getId() == closeBottom.getId()) {
            finish();
        } else if (view.getId() == resumePlayIconText.getId()) {
            playVideo();
        } else if (view.getId() == ctaText.getId() || view.getId() == knowMore.getId()) {
            TBrandAd brandAd = videoBrandAdCard.getAd();
            if (brandAd != null && brandAd.getLandingUrl() != null) {
                AndroidUtils.openUrl(this, brandAd.getLandingUrl());
            }
            KeyguardAssist.launchUnlockActivity(this);
            finish();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (view.getId() == videoView.getId()) {
            if (!mediaController.isShowing()) {
                pauseVideo();
            }
        }
        return true;
    }
}
