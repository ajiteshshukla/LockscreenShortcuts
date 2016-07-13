package com.acubeapps.lockscreen.shortcuts.cards;

import com.inmobi.oem.internal.AndroidUtils;
import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.cards.adapters.RestaurantAdapter;
import com.acubeapps.lockscreen.shortcuts.cards.listeners.RecyclerViewItemClickListener;
import com.acubeapps.lockscreen.shortcuts.core.ad.AdApi;
import com.acubeapps.lockscreen.shortcuts.core.card.CardActivity;
import com.acubeapps.lockscreen.shortcuts.core.card.RestaurantAdCard;
import com.acubeapps.lockscreen.shortcuts.utils.KeyguardAssist;
import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TDeepLink;
import com.inmobi.oem.thrift.ad.model.TMerchantInfo;
import com.inmobi.oem.thrift.ad.model.TRestaurant;
import com.inmobi.oem.thrift.ad.model.TRestaurantAd;

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
public class RestaurantAdActivity extends CardActivity<RestaurantAdCard> {

    @BindView(R.id.merchantHeading)
    TextView txtMerchantHeading;

    @BindView(R.id.merchantIcon)
    ImageView imgMerchantIcon;

    @BindView(R.id.closeButton)
    ImageView btnClose;

    @BindView(R.id.restaurantList)
    RecyclerView recyclerRestaurantList;

    @BindView(R.id.layoutUnlockOverlay)
    RelativeLayout layoutUnlockOverlay;

    @BindView(R.id.textUnlock)
    TextView txtUnlock;

    @Inject
    AdApi adApi;

    @BindView(R.id.show_more)
    Button showMore;

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_ad);
        ButterKnife.bind(this);
        Injectors.appComponent().injectRestaurantAdActivity(this);
        initialize(eventBus);

        RestaurantAdCard card = getCard();
        if (card == null) {
            finish();
            return;
        }
        bindData(card);
    }

    public void bindData(final RestaurantAdCard card) {
        final TRestaurantAd ad = card.getAd();
        final TAd tAd = new TAd();
        tAd.setRestaurant(ad);
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        TMerchantInfo merchantInfo = ad.getMerchantInfo();
        Log.d("NATIVE RESTAURANT AD", "Title is" + merchantInfo.getName());

        txtMerchantHeading.setText(merchantInfo.getHeadline());

        Picasso.with(this).load(merchantInfo.getIcon()).into(
                imgMerchantIcon);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RestaurantAdapter mAdapter = new RestaurantAdapter(ad.getRestaurants(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerRestaurantList.setLayoutManager(mLayoutManager);
        recyclerRestaurantList.setHasFixedSize(true);
        recyclerRestaurantList.setAdapter(mAdapter);
        setItemTouchListener(card, ad);
        setShowMoreClickListener(card, ad);
    }

    private void setShowMoreClickListener(final RestaurantAdCard card, final TRestaurantAd ad) {
        showMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final TDeepLink deeplink = new TDeepLink();
                deeplink.setPrimaryUrl(ad.getShowMore().getPrimaryUrl());
                deeplink.setPackageName(ad.getShowMore().getPackageName());

                boolean isInstalled = AndroidUtils.appExists(RestaurantAdActivity.this,
                        ad.getShowMore().getPackageName());
                if (isInstalled) {
                    txtUnlock.setText("Unlock to Book");
                    KeyguardAssist.launchUnlockActivity(RestaurantAdActivity.this);
                } else {
                    txtUnlock.setText("Installing " + ad.getMerchantInfo().getName());
                }
                layoutUnlockOverlay.setVisibility(View.VISIBLE);

                android.os.Handler animationHandler = new android.os.Handler();

                final TAd tAd = new TAd();
                tAd.setRestaurant(ad);
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

    private void setItemTouchListener(final RestaurantAdCard card, final TRestaurantAd ad) {
        recyclerRestaurantList.addOnItemTouchListener(new RecyclerViewItemClickListener(
                this, new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override

            public void onItemClick(View view, int position) {
                TRestaurant restaurant = ad.getRestaurants().get(position);

                final TDeepLink deeplink = new TDeepLink();

                deeplink.setPrimaryUrl(restaurant.getDeeplink().getPrimaryUrl());
                deeplink.setPackageName(restaurant.getDeeplink().getPackageName());

                boolean isInstalled = AndroidUtils.appExists(RestaurantAdActivity.this,
                        restaurant.getDeeplink().getPackageName());
                if (isInstalled) {
                    txtUnlock.setText("Unlock to Book");
                    KeyguardAssist.launchUnlockActivity(RestaurantAdActivity.this);
                } else {
                    txtUnlock.setText("Installing " + ad.getMerchantInfo().getName());
                }
                layoutUnlockOverlay.setVisibility(View.VISIBLE);

                android.os.Handler animationHandler = new android.os.Handler();

                final TAd tAd = new TAd();
                tAd.setRestaurant(ad);
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
