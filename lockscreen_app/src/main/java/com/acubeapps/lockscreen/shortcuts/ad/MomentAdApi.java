package com.acubeapps.lockscreen.shortcuts.ad;

import com.inmobi.oem.internal.AndroidUtils;
import com.inmobi.oem.internal.PackageManagerUtil;
import com.acubeapps.lockscreen.shortcuts.adstore.AdStore;
import com.acubeapps.lockscreen.shortcuts.adstore.InternalAd;
import com.acubeapps.lockscreen.shortcuts.core.IconAndCard;
import com.acubeapps.lockscreen.shortcuts.core.ad.AdApi;
import com.acubeapps.lockscreen.shortcuts.core.card.AppAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.Card;
import com.acubeapps.lockscreen.shortcuts.core.card.DodAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.ImageBrandAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.MagazineAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.RestaurantAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.TaxiAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.VideoBrandAdCard;
import com.acubeapps.lockscreen.shortcuts.core.icon.AppAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.BrandAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.DodAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.Icon;
import com.acubeapps.lockscreen.shortcuts.core.icon.MagazineAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.MovieAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.RestaurantAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.TaxiAdIcon;
import com.acubeapps.lockscreen.shortcuts.utils.Device;
import com.inmobi.oem.moments.matcher.MomentApi;
import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TAppAd;
import com.inmobi.oem.thrift.ad.model.TDeepLink;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import timber.log.Timber;

import java.util.Collections;
import java.util.List;

import static com.inmobi.oem.internal.AndroidUtils.openUrl;
import static com.acubeapps.lockscreen.shortcuts.Constants.AD_STORE_PREFERENCES;
import static com.acubeapps.lockscreen.shortcuts.Constants.AD_STORE_VERSION;
import static com.acubeapps.lockscreen.shortcuts.Constants.AD_STORE_VERSION_KEY;


/**
 * Created by anshul.srivastava on 08/06/16.
 */
public class MomentAdApi implements AdApi {

    private Context context;
    private AdStore adStore;
    private final MomentApi momentApi;

    public MomentAdApi(Context context, AdStore adStore, MomentApi momentApi) {
        this.context = context;
        this.adStore = adStore;
        this.momentApi = momentApi;
    }

    @Override
    public void initialize() {
        Timber.i("initialize() MomentAdApi");
        try {
            SharedPreferences preferences =
                    context.getSharedPreferences(AD_STORE_PREFERENCES, Context.MODE_PRIVATE);

            String adStoreVersion = preferences.getString(AD_STORE_VERSION_KEY, AD_STORE_VERSION);
            Timber.i("adStoreVersion: %s", adStoreVersion);

            if (!adStoreVersion.equalsIgnoreCase(AD_STORE_VERSION)) {
                Timber.i("App version updated. Clearing active moments. %s -> %s", adStoreVersion, AD_STORE_VERSION);
                momentApi.clearActiveMoments();
            }
        } catch (Exception e) {
            Timber.e(e, "Unable to initialize()");
        }
    }

    @Override
    public void canShow(@NonNull IconAndCard iconAndCard, @NonNull CanShowCallback callback) {
        //TODO add check for VOIP calls + Android Alarms/Reminders
        if (Device.isCallOngoing(context)) {
            callback.onCanShowResult(iconAndCard, false);
        } else {
            callback.onCanShowResult(iconAndCard, true);
        }
    }

    @Override
    public void fetchAds(@NonNull AdCallback callback) {
        callback.onAdFetchStarted();
        try {
            List<InternalAd> nudgeAds = adStore.getAllNudgedAds();
            if (nudgeAds != null) {
                Timber.i("NudgeAds(%s) ", nudgeAds);
                //randomize the nudgeAds list
                Collections.shuffle(nudgeAds);
                for (InternalAd nudgeAd : nudgeAds) {
                    if (!momentApi.isMomentExpired(nudgeAd.getMomentId())) {
                        Timber.i("MomentId(%s) is active", nudgeAd.getMomentId());
                        IconAndCard iconAndCard = createIconAdCard(nudgeAd.getAd());
                        if (iconAndCard == null) {
                            callback.onAdFetchFailed();
                        } else {
                            callback.onAdReceived(iconAndCard);
                        }
                        return;
                    } else {
                        Timber.i("MomentId(%s) has expired", nudgeAd.getMomentId());
                    }
                }
            }
        } catch (Exception e) {
            callback.onAdFetchFailed();
        } finally {
            callback.onAdFetchFinished();
        }
    }

    private IconAndCard createIconAdCard(TAd tad) {

        IconAndCard iconAndCard = null;
        if (tad.isSetApp()) {
            Icon icon = new AppAdIcon(tad.getApp());
            Card card = new AppAdCard(tad);
            iconAndCard = new IconAndCard(tad, icon, card);
        } else if (tad.isSetCommerce()) {
            Icon icon = new DodAdIcon(tad.getCommerce());
            Card card = new DodAdCard(tad);
            iconAndCard = new IconAndCard(tad, icon, card);
        } else if (tad.isSetMovie()) {
            Icon icon = new MovieAdIcon(tad.getMovie());
            Card card = new AppAdCard(tad);
            iconAndCard = new IconAndCard(tad, icon, card);
        } else if (tad.isSetRestaurant()) {
            Icon icon = new RestaurantAdIcon(tad.getRestaurant());
            Card card = new RestaurantAdCard(tad);
            iconAndCard = new IconAndCard(tad, icon, card);
        } else if (tad.isSetTaxi()) {
            Icon icon = new TaxiAdIcon(tad.getTaxi());
            Card card = new TaxiAdCard(tad);
            iconAndCard = new IconAndCard(tad, icon, card);
        } else if (tad.isSetBrand()) {
            Icon icon = new BrandAdIcon(tad.getBrand());
            Card card = null;
            if (tad.getBrand().isSetVideoUrl()) {
                card = new VideoBrandAdCard(tad);
            } else if (tad.getBrand().isSetImageUrl()) {
                card = new ImageBrandAdCard(tad);
            }
            iconAndCard = new IconAndCard(tad, icon, card);
        } else if (tad.isSetMagazine()) {
            Icon icon = new MagazineAdIcon(tad.getMagazine());
            Card card = new MagazineAdCard(tad);
            iconAndCard = new IconAndCard(tad, icon, card);
        }

        return iconAndCard;
    }

    @Override
    public boolean canInstallSilently() {
        return false;
    }

    @Override
    public void installApp(@NonNull TAppAd ad) {
        openUrlInPlaystore(ad.getPackageName());
    }

    private void openUrlInPlaystore(String packageName) {
        try {
            openUrl(context, "market://details?id=" + packageName);
        } catch (Exception e) {
            Timber.e(e, "Exception while opening app in playstore");
        }
    }

    @Override
    public void openDeeplink(@NonNull TAd ad, @NonNull TDeepLink deeplink) {

        //Process primary url
        boolean status = PackageManagerUtil.openAppUrl(context, deeplink.getPrimaryUrl());

        if (!status && deeplink.getPackageName() != null) {
            //Try to open app with package name
            AndroidUtils.openApp(context, deeplink.getPackageName());
        }

        if (!status) {
            openUrlInPlaystore(deeplink.getPackageName());
        }

    }

}
