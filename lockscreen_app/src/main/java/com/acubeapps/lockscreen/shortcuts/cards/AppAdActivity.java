package com.acubeapps.lockscreen.shortcuts.cards;

import com.inmobi.oem.internal.AndroidUtils;
import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.core.ad.AdApi;
import com.acubeapps.lockscreen.shortcuts.core.card.AppAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.CardActivity;
import com.acubeapps.lockscreen.shortcuts.utils.KeyguardAssist;
import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TAppAd;
import com.inmobi.oem.thrift.ad.model.TDeepLink;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by aasha.medhi on 29/05/16.
 */
public class AppAdActivity extends CardActivity<AppAdCard> {

    @BindView(R.id.installButton)
    Button btnInstallNow;

    @BindView(R.id.appName)
    TextView txtAppName;

    @BindView(R.id.appCategory)
    TextView txtCategory;

    @BindView(R.id.appInstalls)
    TextView txtAppInstalls;

    @BindView(R.id.rating)
    TextView txtRatings;

    @BindView(R.id.separator)
    View viewSeparator;

    @BindView(R.id.closeButton)
    ImageView btnClose;

    @BindView(R.id.appIcon)
    ImageView imgAppIcon;

    @BindView(R.id.cardBackground)
    ImageView imgAppBackground;

    @Inject
    AdApi adApi;

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_ad);
        ButterKnife.bind(this);
        Injectors.appComponent().injectAppAdActivity(this);
        initialize(eventBus);

        AppAdCard card = getCard();
        if (card == null) {
            finish();
            return;
        }
        bindData(card);
    }

    private void bindData(final AppAdCard card) {
        final TAppAd ad = card.getAd();

        final boolean isInstalled =
                AndroidUtils.appExists(this, ad.getPackageName());
        if (isInstalled) {
            btnInstallNow.setText(getResources().getString(R.string.open_app));
        } else {
            btnInstallNow.setText(getResources().getString(R.string.install_now));
        }

        btnInstallNow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (isInstalled) {
                    KeyguardAssist.launchUnlockActivity(AppAdActivity.this);
                    openWork(btnInstallNow, ad);
                } else {
                    installWork(btnInstallNow, ad);
                }
                return false;
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.d("NATIVE APP AD", "Title is" + ad.getTitle());

        txtAppName.setText(ad.getTitle());
        txtCategory.setText(ad.getCategories().get(0));
        if (ad.getInstalls() == null || "".equals(ad.getInstalls().trim())) {
            txtAppInstalls.setVisibility(View.GONE);
            viewSeparator.setVisibility(View.GONE);
        } else {
            txtAppInstalls.setVisibility(View.VISIBLE);
            viewSeparator.setVisibility(View.VISIBLE);
            txtAppInstalls.setText(ad.getInstalls() + "+ downloads");
        }
        txtRatings.setText("" + String.format("%.2g%n", ad.getStarRating()));

        Picasso.with(this).load(ad.getIconUrl()).into(
                imgAppIcon);
        Picasso.with(this).load(ad.getBackgroundUrl()).into(
                imgAppBackground);
    }

    private void openWork(final Button installNowButton, final TAppAd myAd) {
        installNowButton.setText(getResources().getString(R.string.unlock_to_explore));
        installNowButton.invalidate();
        android.os.Handler animationHandler = new android.os.Handler();

        final TAd ad = new TAd();
        ad.setApp(myAd);
        animationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TDeepLink deepLink = new TDeepLink();
                deepLink.setPackageName(myAd.getPackageName());
                adApi.openDeeplink(ad, deepLink);
                finish();
            }
        }, 2000);

    }

    private void installWork(final Button installNowButton, final TAppAd myAd) {
        final TAd tAd = new TAd();
        tAd.setApp(myAd);

        new AsyncTask<Void, Void, Void>() {
            boolean canInstallSilently = false;

            @Override
            protected Void doInBackground(Void... params) {
                canInstallSilently = adApi.canInstallSilently();
                return null;
            }


            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (canInstallSilently) {
                    installNowButton.setText(getResources().getString(R.string.installing_prog));
                } else {
                    installNowButton.setText(getResources().getString(R.string.unlock_to_install));
                }
                installNowButton.invalidate();

                adApi.installApp(myAd);

                android.os.Handler animationHandler = new android.os.Handler();

                animationHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
