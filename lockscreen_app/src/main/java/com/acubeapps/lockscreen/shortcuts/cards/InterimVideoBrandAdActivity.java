package com.acubeapps.lockscreen.shortcuts.cards;

import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.core.card.CardActivity;
import com.acubeapps.lockscreen.shortcuts.core.card.VideoBrandAdCard;
import com.acubeapps.lockscreen.shortcuts.utils.CardControllerUtils;
//import com.inmobi.oem.lockscreen.utils.FileUtils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.makeramen.roundedimageview.RoundedImageView;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by ajitesh.shukla on 6/11/16.
 */
public class InterimVideoBrandAdActivity extends CardActivity<VideoBrandAdCard> {

    @BindView(R.id.closeButton)
    ImageView closeButton;

    @BindView(R.id.imgThumbnail)
    RoundedImageView imageThumbnail;

    @BindView(R.id.imgBrandIcon)
    ImageView imageBrandIcon;

    @BindView(R.id.brandTitle)
    TextView brandTitle;

    @BindView(R.id.brandHeadline)
    TextView brandHeadline;

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_interim);
        ButterKnife.bind(this);
        Injectors.appComponent().injectInterimVideoBrandAdActivity(this);
        initialize(eventBus);
        VideoBrandAdCard card = getCard();
        if (card == null) {
            finish();
            return;
        }
        bindData(card);
    }

    private void bindData(final VideoBrandAdCard card) {
        Bitmap bitmap = null;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, card.getVideoUri());
            //retriever.setDataSource(FileUtils.getLocalFilePath(card.getVideoUri(), "brandVideo.mp4", this));
            bitmap = retriever.getFrameAtTime(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageThumbnail.setImageBitmap(bitmap);
        imageThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                CardControllerUtils.launchActivity(card, VideoBrandAdActivity.class, InterimVideoBrandAdActivity.this);
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        brandTitle.setText(card.getAd().getTitle());
        brandHeadline.setText(card.getAd().getHeadline());
        imageBrandIcon.setImageURI(card.getIconUri());
    }

}
