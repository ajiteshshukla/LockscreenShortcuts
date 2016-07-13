package com.acubeapps.lockscreen.shortcuts.cards;

import com.inmobi.oem.core.model.Deeplink;
import com.inmobi.oem.internal.AndroidUtils;
import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.core.ad.AdApi;
import com.acubeapps.lockscreen.shortcuts.core.card.CardActivity;
import com.acubeapps.lockscreen.shortcuts.core.card.DodAdCard;
import com.acubeapps.lockscreen.shortcuts.core.events.UserOnHomeScreenEvent;
import com.acubeapps.lockscreen.shortcuts.utils.KeyguardAssist;
import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TCommerceAd;
import com.inmobi.oem.thrift.ad.model.TDeepLink;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

/**
 * Created by aasha.medhi on 29/05/16.
 */
public class DodActivity extends CardActivity<DodAdCard> {

    private TCommerceAd dodAd;

    private TDeepLink deeplink;

    @BindView(R.id.closeButton)
    ImageView btnClose;

    @BindView(R.id.dealWebView)
    WebView webViewDod;

    @BindView(R.id.installing_app_overlay)
    TextView installingApp;

    @Inject
    AdApi adApi;

    @Inject
    EventBus eventBus;

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dod_ad);
        ButterKnife.bind(this);
        Injectors.appComponent().injectDodAdActivity(this);
        initialize(eventBus);
        DodAdCard card = getCard();
        if (card == null) {
            finish();
            return;
        }
        bindData(card);
    }

    public void bindData(DodAdCard card) {
        dodAd = card.getAd();
        webViewDod.getSettings().setJavaScriptEnabled(true);
        webViewDod.loadDataWithBaseURL("", dodAd.getHtml(), "text/html", "UTF-8", "");
        webViewDod.addJavascriptInterface(new JavaScriptHandler(), "vault");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    class JavaScriptHandler {

        public JavaScriptHandler() {

        }

        @JavascriptInterface
        public void log(String urlJson) {
            Timber.d("JS log %s ", urlJson);
        }

        @JavascriptInterface
        public void openUrl(String urlJson) {
            Timber.d("Open url %s", urlJson);
            Gson gson = new Gson();
            Deeplink deeplink = gson.fromJson(urlJson, Deeplink.class);
            DodActivity.this.deeplink = new TDeepLink();
            DodActivity.this.deeplink.setPrimaryUrl(deeplink.getPrimary().getUrl());
            DodActivity.this.deeplink.setPackageName(deeplink.getPackageName());

            if (null != dodAd) {
                if (AndroidUtils.appExists(DodActivity.this, deeplink.getPackageName())) {
                    KeyguardAssist.launchUnlockActivity(DodActivity.this);
                } else {
                    MAIN_HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            installingApp.setVisibility(View.VISIBLE);
                        }
                    });
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                }
            }

        }

        @JavascriptInterface
        public boolean hasApp(String packageName) {
            Timber.d("Has app " + packageName);
            return AndroidUtils.appExists(DodActivity.this, packageName);
        }

    }

    @Subscribe
    public void onUserPresent(UserOnHomeScreenEvent event) {
        TAd tAd = new TAd();
        tAd.setCommerce(dodAd);
        try {
            adApi.openDeeplink(tAd, deeplink);
            deeplink = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO check
    private static Set<String> urlsToOpenSet = Collections.synchronizedSet(new HashSet<String>());
//    public static Set<String> getUrlsToOpenSet() {
//        Log.d(TAG, "returning URLs" + urlsToOpenSet);
//        return urlsToOpenSet;
//    }

}
