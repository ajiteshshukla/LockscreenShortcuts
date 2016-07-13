package com.acubeapps.lockscreen.shortcuts.core.ad;

import com.acubeapps.lockscreen.shortcuts.core.IconAndCard;

import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TAppAd;
import com.inmobi.oem.thrift.ad.model.TDeepLink;

import android.support.annotation.NonNull;

/**
 * Created by ritwik on 29/05/16.
 */
public interface AdApi {

    interface AdCallback {

        void onAdFetchStarted();

        void onAdFetchFinished();

        void onAdReceived(IconAndCard iconAndCard);

        void onAdFetchFailed();

    }

    interface CanShowCallback {

        void onCanShowResult(IconAndCard icon, boolean canShow);

    }

    void initialize();

    void canShow(@NonNull IconAndCard icon, @NonNull CanShowCallback callback);

    void fetchAds(@NonNull AdCallback callback);

    boolean canInstallSilently();

    void installApp(@NonNull TAppAd ad);

    void openDeeplink(@NonNull TAd ad, @NonNull TDeepLink deeplink);
}
