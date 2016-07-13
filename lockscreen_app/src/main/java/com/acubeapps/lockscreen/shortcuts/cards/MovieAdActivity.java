package com.acubeapps.lockscreen.shortcuts.cards;

import com.inmobi.oem.internal.AndroidUtils;
import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.cards.adapters.MovieAdapter;
import com.acubeapps.lockscreen.shortcuts.cards.listeners.RecyclerViewItemClickListener;
import com.acubeapps.lockscreen.shortcuts.core.ad.AdApi;
import com.acubeapps.lockscreen.shortcuts.core.card.CardActivity;
import com.acubeapps.lockscreen.shortcuts.core.card.MovieAdCard;
import com.acubeapps.lockscreen.shortcuts.utils.KeyguardAssist;
import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TDeepLink;
import com.inmobi.oem.thrift.ad.model.TMerchantInfo;
import com.inmobi.oem.thrift.ad.model.TMovie;
import com.inmobi.oem.thrift.ad.model.TMovieAd;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;


/**
 * Created by aasha.medhi on 29/05/16.
 */
public class MovieAdActivity extends CardActivity<MovieAdCard> {

    @BindView(R.id.merchantHeading)
    TextView txtMerchantHeading;

    @BindView(R.id.merchantIcon)
    ImageView imgMerchantIcon;

    @BindView(R.id.closeButton)
    ImageView btnClose;

    @BindView(R.id.movieList)
    RecyclerView recyclerMovieList;

    @BindView(R.id.layoutUnlockOverlay)
    RelativeLayout layoutUnlockOverlay;

    @BindView(R.id.textUnlock)
    TextView txtUnlock;

    @BindView(R.id.show_more)
    Button showMore;

    @Inject
    AdApi adApi;

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_ad);
        ButterKnife.bind(this);
        Injectors.appComponent().injectMovieAdActivity(this);
        initialize(eventBus);

        MovieAdCard card = getCard();
        if (card == null) {
            finish();
            return;
        }
        bindData(card);
    }

    public void bindData(final MovieAdCard card) {
        final TMovieAd ad = card.getAd();
        final TAd tAd = new TAd();
        tAd.setMovie(ad);

        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        TMerchantInfo merchantInfo = ad.getMerchantInfo();
        Log.d("NATIVE MOVIE AD", "Title is" + merchantInfo.getName());

        txtMerchantHeading.setText(merchantInfo.getHeadline());

        Picasso.with(this).load(merchantInfo.getIcon()).into(
                imgMerchantIcon);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        MovieAdapter mAdapter = new MovieAdapter(ad.getMovies(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerMovieList.setLayoutManager(mLayoutManager);
        recyclerMovieList.setHasFixedSize(true);
        recyclerMovieList.setAdapter(mAdapter);
        setOnItemTouchListener(card, ad);
        setShowMoreOnClick(card, ad);
    }

    private void setShowMoreOnClick(final MovieAdCard card, final TMovieAd ad) {
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final TDeepLink deeplink = new TDeepLink();
                deeplink.setPrimaryUrl(ad.getShowMore().getPrimaryUrl());
                deeplink.setPackageName(ad.getShowMore().getPackageName());

                boolean isInstalled = AndroidUtils.appExists(MovieAdActivity.this, ad.getShowMore().getPackageName());
                if (isInstalled) {
                    txtUnlock.setText("Unlock to Book");
                    KeyguardAssist.launchUnlockActivity(MovieAdActivity.this);
                } else {
                    txtUnlock.setText("Installing " + ad.getMerchantInfo().getName());
                }
                layoutUnlockOverlay.setVisibility(View.VISIBLE);

                android.os.Handler animationHandler = new android.os.Handler();

                final TAd tAd = new TAd();
                tAd.setMovie(ad);
                animationHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adApi.openDeeplink(tAd, deeplink);
                        finish();
                    }
                }, 2000);
            }
        });
    }

    private void setOnItemTouchListener(final MovieAdCard card, final TMovieAd ad) {
        recyclerMovieList.addOnItemTouchListener(new RecyclerViewItemClickListener(
                this, new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TMovie movie = ad.getMovies().get(position);
                final TDeepLink deeplink = new TDeepLink();
                deeplink.setPrimaryUrl(movie.getDeeplink().getPrimaryUrl());
                deeplink.setPackageName(movie.getDeeplink().getPackageName());

                boolean isInstalled = AndroidUtils.appExists(MovieAdActivity.this,
                        movie.getDeeplink().getPackageName());
                if (isInstalled) {
                    txtUnlock.setText("Unlock to Book");
                    KeyguardAssist.launchUnlockActivity(MovieAdActivity.this);
                } else {
                    txtUnlock.setText("Installing " + ad.getMerchantInfo().getName());
                }
                layoutUnlockOverlay.setVisibility(View.VISIBLE);

                android.os.Handler animationHandler = new android.os.Handler();
                final TAd tAd = new TAd();
                tAd.setMovie(ad);
                animationHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adApi.openDeeplink(tAd, deeplink);
                        finish();
                    }
                }, 2000);
            }
        }));
    }
}
