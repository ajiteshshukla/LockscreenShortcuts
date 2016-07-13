package com.acubeapps.lockscreen.shortcuts.cards;

import com.inmobi.oem.internal.AndroidUtils;
import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.cards.adapters.TaxiAdapter;
import com.acubeapps.lockscreen.shortcuts.cards.listeners.RecyclerViewItemClickListener;
import com.acubeapps.lockscreen.shortcuts.core.ad.AdApi;
import com.acubeapps.lockscreen.shortcuts.core.card.CardActivity;
import com.acubeapps.lockscreen.shortcuts.core.card.TaxiAdCard;
import com.acubeapps.lockscreen.shortcuts.utils.KeyguardAssist;
import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TDeepLink;
import com.inmobi.oem.thrift.ad.model.TMerchantInfo;
import com.inmobi.oem.thrift.ad.model.TTaxi;
import com.inmobi.oem.thrift.ad.model.TTaxiAd;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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
public class TaxiAdActivity extends CardActivity<TaxiAdCard> {

    @BindView(R.id.merchantName)
    TextView txtMerchantName;

    @BindView(R.id.merchantHeading)
    TextView txtMerchantHeading;

    @BindView(R.id.merchantIcon)
    ImageView imgMerchantIcon;

    @BindView(R.id.closeButton)
    ImageView btnClose;

    @BindView(R.id.taxiList)
    RecyclerView recyclerTaxiList;

    @BindView(R.id.layoutUnlockOverlay)
    RelativeLayout layoutUnlockOverlay;

    @BindView(R.id.textUnlock)
    TextView txtUnlock;

    @Inject
    AdApi adApi;

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_ad);
        ButterKnife.bind(this);
        Injectors.appComponent().injectTaxiAdActivity(this);
        initialize(eventBus);

        TaxiAdCard card = getCard();
        if (card == null) {
            finish();
            return;
        }
        bindData(card);
    }

    public void bindData(final TaxiAdCard card) {
        final TTaxiAd ad = card.getAd();
        final TAd tAd = new TAd();
        tAd.setTaxi(ad);
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        TMerchantInfo merchantInfo = ad.getMerchantInfo();
        Log.d("NATIVE TAXI AD", "Title is" + merchantInfo.getName());

        txtMerchantName.setText(merchantInfo.getName());
        txtMerchantHeading.setText(merchantInfo.getHeadline());
        Picasso.with(this).load(merchantInfo.getIcon()).into(
                imgMerchantIcon);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TaxiAdapter mAdapter = new TaxiAdapter(ad.getTaxis(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerTaxiList.setLayoutManager(mLayoutManager);
        recyclerTaxiList.setHasFixedSize(true);
        recyclerTaxiList.setAdapter(mAdapter);
        recyclerTaxiList.addOnItemTouchListener(new RecyclerViewItemClickListener(
                this, new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                TTaxi taxi = ad.getTaxis().get(position);
                final TDeepLink deeplink = new TDeepLink();
                deeplink.setPrimaryUrl(taxi.getDeeplink().getPrimaryUrl());

                deeplink.setPackageName(taxi.getDeeplink().getPackageName());

                boolean isInstalled = AndroidUtils.appExists(TaxiAdActivity.this, taxi.getDeeplink().getPackageName());
                if (isInstalled) {
                    txtUnlock.setText("Unlock to Book");
                    KeyguardAssist.launchUnlockActivity(TaxiAdActivity.this);
                } else {
                    txtUnlock.setText("Installing " + ad.getMerchantInfo().getName());
                }
                layoutUnlockOverlay.setVisibility(View.VISIBLE);

                android.os.Handler animationHandler = new android.os.Handler();

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
