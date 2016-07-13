package com.acubeapps.lockscreen.shortcuts.cards;

import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.cards.adapters.MagazineContentAdapter;
import com.acubeapps.lockscreen.shortcuts.cards.model.MagazineContentItemLayout;
import com.acubeapps.lockscreen.shortcuts.cards.model.MagazineHeaderItemLayout;
import com.acubeapps.lockscreen.shortcuts.core.card.MagazineActivity;
import com.inmobi.oem.thrift.ad.model.TContent;
import com.inmobi.oem.thrift.ad.model.TMagazine;
import com.inmobi.oem.thrift.ad.model.TVideo;

import android.graphics.Point;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.felipecsl.asymmetricgridview.AsymmetricItem;
import com.felipecsl.asymmetricgridview.AsymmetricRecyclerView;
import com.felipecsl.asymmetricgridview.AsymmetricRecyclerViewAdapter;

import timber.log.Timber;

import java.util.List;

/**
 * Created by aasha.medhi on 20/06/16.
 */
public class StaggeredMagazineActivity extends MagazineActivity {

    private MagazineContentAdapter adapter;

    private List<AsymmetricItem> magazineItemList;

    @BindView(R.id.video_recycler_view)
    AsymmetricRecyclerView recyclerView = null;

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                initializeAndSetAdapter();
//                recyclerView.forceLayout();
//                if (playerView != null && playerView.getVideoFrame() != null) {
//                    if (playerView.getVideoFrame().getHeight() < overlay.getHeight()) {
//                        layoutVideoMetadata.setVisibility(View.VISIBLE);
//                    } else {
//                        layoutVideoMetadata.setVisibility(View.GONE);
//                    }
//                }
//            }
//        }, 200);
//    }

    @Override
    protected void initialize() {
        setContentView(R.layout.activity_magazine_ad_card);
        ButterKnife.bind(this);
        Injectors.appComponent().injectMagazineAdActivity(this);
    }

    protected void bindData(TMagazine magazine) {
        magazineItemList = DemoUtils.prepareMagazineItemsAndLayout(magazine, analytics);
        initializeAndSetAdapter();
    }

    @Override
    protected int getBackgroundColor() {
        return getResources().getColor(R.color.magazine_background);
    }

    @Override
    protected void onCloseOverlay() {

    }

    @Override
    protected void onDestroy() {
        stopMagazineHeaderAnimation();
        super.onDestroy();
    }

    private void pauseMagazineHeaderAnimation(long pauseDurationInMillis) {
        adapter.pauseMagazineHeaderAnimation(pauseDurationInMillis);
    }

    private void stopMagazineHeaderAnimation() {
        adapter.stopMagazineHeaderAnimationHandler();
    }

    private void initializeAndSetAdapter() {
        recyclerView.setRequestedColumnCount(3);
        recyclerView.setDebugging(true);
        final Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        adapter = new MagazineContentAdapter(magazineItemList, this,
                new MagazineContentAdapter.VideoClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Timber.d("onClick Entered");
                        AsymmetricItem asymmetricItem = magazineItemList.get(position);
                        TVideo video = getVideo(asymmetricItem);
                        setTileId(asymmetricItem, position);
                        if (video == null) {
                            return;
                        }
                        long videoDurationInMillis = video.getDuration() * 1000;
                        pauseMagazineHeaderAnimation(videoDurationInMillis);
                        onClickVideo(video);
                    }
                }, picasso, analytics);
        recyclerView.setAdapter(new AsymmetricRecyclerViewAdapter<>(this, recyclerView, adapter));
    }

    private void setTileId(AsymmetricItem asymmetricItem, int position) {
        if (asymmetricItem instanceof MagazineContentItemLayout) {
            tileId = String.format("L:%d", position);
        } else if (asymmetricItem instanceof MagazineHeaderItemLayout) {
            int currentHeaderItem = adapter.getCurrentHeaderItem();
            tileId = String.format("H:%d", currentHeaderItem);
        }
    }

    private TVideo getVideo(AsymmetricItem asymmetricItem) {
        TVideo video = null;
        if (asymmetricItem instanceof MagazineContentItemLayout) {
            MagazineContentItemLayout magazineContentItemLayout = (MagazineContentItemLayout) asymmetricItem;
            if (magazineContentItemLayout.getMagazineContent().isSetVideo()) {
                video = magazineContentItemLayout.getMagazineContent().getVideo();
            }
        } else if (asymmetricItem instanceof MagazineHeaderItemLayout) {
            MagazineHeaderItemLayout magazineHeaderItemLayout = (MagazineHeaderItemLayout) asymmetricItem;
            int currentHeaderItem = adapter.getCurrentHeaderItem();
            TContent content = magazineHeaderItemLayout.getHeaderContent().get(currentHeaderItem);
            if (content.isSetVideo()) {
                video = content.getVideo();
            }
        }
        return video;
    }
}
