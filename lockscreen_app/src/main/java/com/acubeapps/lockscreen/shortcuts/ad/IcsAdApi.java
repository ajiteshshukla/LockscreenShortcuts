package com.acubeapps.lockscreen.shortcuts.ad;

import com.inmobi.oem.core.model.Ad;
import com.inmobi.oem.core.model.AdError;
import com.inmobi.oem.core.model.AdRequest;
import com.inmobi.oem.core.model.AppAd;
import com.inmobi.oem.core.model.BrandAd;
import com.inmobi.oem.core.model.Deeplink;
import com.inmobi.oem.core.model.DodAd;
import com.inmobi.oem.core.model.O2OAd;
import com.inmobi.oem.core.model.SiteConfig;
import com.inmobi.oem.core.model.UrlItem;
import com.inmobi.oem.core.sdk.CoreSdkApi;
import com.acubeapps.lockscreen.shortcuts.core.IconAndCard;
import com.acubeapps.lockscreen.shortcuts.core.ad.AdApi;
import com.acubeapps.lockscreen.shortcuts.core.card.AppAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.Card;
import com.acubeapps.lockscreen.shortcuts.core.card.DodAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.ImageBrandAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.MovieAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.RestaurantAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.TaxiAdCard;
import com.acubeapps.lockscreen.shortcuts.core.card.VideoBrandAdCard;
import com.acubeapps.lockscreen.shortcuts.core.icon.AppAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.BrandAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.DodAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.Icon;
import com.acubeapps.lockscreen.shortcuts.core.icon.MovieAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.RestaurantAdIcon;
import com.acubeapps.lockscreen.shortcuts.core.icon.TaxiAdIcon;
import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TAppAd;
import com.inmobi.oem.thrift.ad.model.TBrandAd;
import com.inmobi.oem.thrift.ad.model.TCommerceAd;
import com.inmobi.oem.thrift.ad.model.TDeepLink;
import com.inmobi.oem.thrift.ad.model.TMovieAd;
import com.inmobi.oem.thrift.ad.model.TRestaurantAd;
import com.inmobi.oem.thrift.ad.model.TTaxiAd;

import android.net.Uri;
import android.support.annotation.NonNull;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import timber.log.Timber;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by ritwik on 29/05/16.
 */
public class IcsAdApi implements AdApi {

    private final CoreSdkApi icsApi;
    private final ExecutorService pool;
    private final CoreSdkApi.AdCallback icsAdCallback;
    private final BlockingQueue<Ad> ads;
    private SiteConfig siteConfig;
    private boolean initialized = false;

    public IcsAdApi(CoreSdkApi icsApi, ExecutorService pool) {
        this.icsApi = icsApi;
        this.pool = pool;
        icsAdCallback = newCallback();
        ads = new LinkedBlockingQueue<>();
    }

    @Override
    public void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        icsApi.connectWithPackageName("com.inmobi.oem.lockscreen", new CoreSdkApi.Callback() {
            @Override
            public void connected(String siteId, SiteConfig siteConfig) {
                Timber.i("connected(%s, %s)", siteId, siteConfig);
                icsApi.setAdCallback(icsAdCallback);
            }

            @Override
            public void connectionError(Throwable throwable) {
                Timber.e(throwable, "connectionError");
            }

            @Override
            public void disconnected() {
                Timber.i("disconnected()");
            }
        }, null);

    }

    @Override
    public void canShow(final IconAndCard iconAndCard, final CanShowCallback callback) {
        pool.submit(new Runnable() {
            @Override
            public void run() {
                final boolean result;

                try {
                    try {
                        Timber.d("canShowAd(%s)", iconAndCard.getAd());
                        result = icsApi.canShowAd(fromTAd(iconAndCard.getAd()));
                    } catch (Exception e) {
                        Timber.e(e, "Unable to call canShowAd()");
                        callback.onCanShowResult(iconAndCard, false);
                        return;
                    }

                    callback.onCanShowResult(iconAndCard, result);
                } catch (Exception e) {
                    Timber.e(e, "Unable to call callback");
                }
            }
        });
    }

    @Override
    public void fetchAds(final AdCallback callback) {
        pool.submit(new Runnable() {
            @Override
            public void run() {
                ads.clear();
                try {
                    Timber.d("fetchAds()");
                    callback.onAdFetchStarted();
                    icsApi.fetchAds(new AdRequest.Builder()
                            .setAdMinBatchCount(1)
                            .build());

                    Ad ad = ads.poll(1, TimeUnit.SECONDS);
                    if (ad == null) {
                        return;
                    }
                    IconAndCard iconAndCard = createIconAndCard(ad);
                    if (iconAndCard == null) {
                        // No-fill
                        callback.onAdFetchFailed();
                        return;
                    }
                    callback.onAdReceived(iconAndCard);
                } catch (Exception e) {
                    Timber.e(e, "Exception occurred during ad fetch");
                    callback.onAdFetchFailed();
                } finally {
                    callback.onAdFetchFinished();
                }
            }
        });
    }

    private IconAndCard createIconAndCard(Ad ad) {
        if (ad instanceof AppAd) {
            return createAppAdIconAndCard((AppAd) ad);
        } else if (ad instanceof DodAd) {
            return createDodAdIconAndCard((DodAd) ad);
        } else if (ad instanceof O2OAd) {
            return createO2oAdIconAndCard((O2OAd) ad);
        } else if (ad instanceof BrandAd) {
            return createBrandAdIconAndCard((BrandAd) ad);
        } else {
            return null;
        }
    }

    private IconAndCard createO2oAdIconAndCard(O2OAd ad) {
        TAd tad = new TAd();
        try {
            new TDeserializer().deserialize(tad, ad.getAd());
        } catch (TException e) {
            Timber.e(e, "Unable to deserialize ad");
            return null;
        }

        if (tad.isSetTaxi()) {
            return createTaxiAd(tad, tad.getTaxi());
        }

        if (tad.isSetMovie()) {
            return createMovieAd(tad, tad.getMovie());
        }

        if (tad.isSetRestaurant()) {
            return createRestaurantAd(tad, tad.getRestaurant());
        }
        return null;
    }

    private IconAndCard createTaxiAd(TAd ad, TTaxiAd taxiAd) {
        Icon icon = new TaxiAdIcon(taxiAd);
        Card card = new TaxiAdCard(ad);
        return new IconAndCard(ad, icon, card);
    }

    private IconAndCard createMovieAd(TAd ad, TMovieAd movieAd) {
        Icon icon = new MovieAdIcon(movieAd);
        Card card = new MovieAdCard(ad);
        return new IconAndCard(ad, icon, card);
    }

    private IconAndCard createRestaurantAd(TAd ad, TRestaurantAd restaurantAd) {
        Icon icon = new RestaurantAdIcon(restaurantAd);
        Card card = new RestaurantAdCard(ad);
        return new IconAndCard(ad, icon, card);
    }


    private IconAndCard createDodAdIconAndCard(DodAd ad) {
        TAd tad = new TAd();
        TCommerceAd tCommerceAd = createTDodAd(ad);
        tad.setCommerce(tCommerceAd);
        Icon icon = new DodAdIcon(tCommerceAd);
        Card card = new DodAdCard(tad);
        return new IconAndCard(tad, icon, card);
    }

    @NonNull
    private IconAndCard createAppAdIconAndCard(AppAd ad) {
        Uri iconUri = icsApi.appAdApi().getIconUri(ad);
        Uri backgroundUri = icsApi.appAdApi().getBackgroundUri(ad);
        TAd tad = new TAd();
        TAppAd tappad = createTAppAd(ad);
        tappad.setIconUrl(iconUri.toString());
        tappad.setBackgroundUrl(backgroundUri.toString());
        Icon icon = new AppAdIcon(tappad);
        Card card = new AppAdCard(tad);
        return new IconAndCard(tad, icon, card);
    }

    private IconAndCard createBrandAdIconAndCard(BrandAd ad) {
        Uri iconUri = icsApi.brandAdApi().getIconUri(ad);
        Uri videoUri = icsApi.brandAdApi().getVideoUri(ad);
        Uri imageUri = icsApi.brandAdApi().getImageUri(ad);

        TBrandAd tbrandad = createTBrandAd(ad);
        tbrandad.setIconUrl(iconUri.toString());
        tbrandad.setVideoUrl(videoUri.toString());
        tbrandad.setImageUrl(imageUri.toString());
        TAd tad = new TAd();
        tad.setBrand(tbrandad);

        Card card;

        if (videoUri != null) {
            card = new VideoBrandAdCard(tad);
        } else if (imageUri != null) {
            card = new ImageBrandAdCard(tad);
        } else {
            Timber.e("No Supported card type for the BrandAd (%s)", ad);
            return null;
        }

        Icon icon = new BrandAdIcon(tad.getBrand());
        return new IconAndCard(tad, icon, card);
    }


    private CoreSdkApi.AdCallback newCallback() {
        return new CoreSdkApi.AdCallback() {
            @Override
            public void gotNewAds(List<Ad> list) {
                Timber.e("Wasn't expecting ads here : %s", list);
            }

            @Override
            public void onNewAdsLoaded(AdRequest adRequest, Ad ad) {
                Timber.d("onNewAdsLoaded(%s, %s)", adRequest, ad);
                ads.add(ad);
            }

            @Override
            public void onAdLoadFailed(AdRequest adRequest, AdError adError) {
                Timber.d("onNewAdsLoaded(%s, %s)", adRequest, adError);
            }

            @Override
            public void onOpenUrl(String url, String title) {
            }
        };
    }

    @Override
    public boolean canInstallSilently() {
        try {
            return icsApi.canInstallSilently();
        } catch (final Throwable e) {
            Timber.e(e, "Unable to call canInstallSilently");
            return false;
        }
    }

    @Override
    public void installApp(final TAppAd ad) {
        final AppAd appAd = new AppAd(0L);
        appAd.setPackageName(ad.getPackageName());
        appAd.setTitle(ad.getTitle());
        appAd.setSubtitle(ad.getSubtitle());
        appAd.setCategories(new HashSet<String>(ad.getCategories()));
        appAd.setStartRating((float) ad.getStarRating());
        appAd.setInstalls(ad.getInstalls());

        pool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    icsApi.appAdApi().installApp(appAd);
                } catch (Exception e) {
                    Timber.e(e, "Unable to call installApp(%s)", ad);
                }
            }
        });
    }

    private Ad fromTAd(TAd tad) {
        Ad ad = null;
        if (tad.isSetApp()) {
            ad = createAppAd(tad.getApp());
        } else if (tad.isSetCommerce()) {
            ad = createDodAd(tad.getCommerce());
        } else if (tad.isSetMovie() || tad.isSetTaxi() || tad.isSetRestaurant()) {
            ad = createO2OAd(tad);
        } else if (tad.isSetBrand()) {
            ad = createBrandAd(tad.getBrand());
        }
        return ad;
    }

    private O2OAd createO2OAd(TAd tad) {
        try {
            byte[] serializedAd = new TSerializer().serialize(tad);
            O2OAd.Category category = null;
            if (tad.isSetTaxi()) {
                category = O2OAd.Category.TAXI;
            } else if (tad.isSetMovie()) {
                category = O2OAd.Category.MOVIE;
            } else if (tad.isSetRestaurant()) {
                category = O2OAd.Category.RESTAURANT;
            }
            O2OAd o2OAd = new O2OAd(category, serializedAd);
            return o2OAd;
        } catch (Exception e) {
            Timber.e("Exception creating serialized ad");
        }
        return null;
    }

    private DodAd createDodAd(TCommerceAd tcommerceAd) {
        DodAd dodAd = new DodAd(0L);
        dodAd.setPackageName(tcommerceAd.getPackageName());
        dodAd.setHeadline(tcommerceAd.getHeadline());
        dodAd.setHtml(tcommerceAd.getHtml());
        dodAd.setIcon(tcommerceAd.getIcon());
        dodAd.setMerchantName(tcommerceAd.getMerchantName());
        dodAd.setPreRenderBeacon(tcommerceAd.getPreRenderBeacon());
        dodAd.setTitle(tcommerceAd.getTitle());

        Timber.i("dodAd {}", dodAd);
        return dodAd;
    }

    private TCommerceAd createTDodAd(DodAd dodAd) {
        TCommerceAd tCommerceAd = new TCommerceAd();
        tCommerceAd.setPackageName(dodAd.getPackageName());
        tCommerceAd.setHeadline(dodAd.getHeadline());
        tCommerceAd.setHtml(dodAd.getHtml());
        tCommerceAd.setIcon(dodAd.getIcon());
        tCommerceAd.setMerchantName(dodAd.getMerchantName());
        tCommerceAd.setPreRenderBeacon(dodAd.getPreRenderBeacon());
        tCommerceAd.setTitle(dodAd.getTitle());

        Timber.i("dodAd {}", tCommerceAd);
        return tCommerceAd;
    }

    private AppAd createAppAd(TAppAd tappAd) {
        AppAd appAd = new AppAd(0L);
        appAd.setPackageName(tappAd.getPackageName());
        appAd.setTitle(tappAd.getTitle());
        appAd.setSubtitle(tappAd.getSubtitle());
        appAd.setCategories(new HashSet<String>(tappAd.getCategories()));
        appAd.setStartRating((float) tappAd.getStarRating());
        appAd.setInstalls(tappAd.getInstalls());

        Timber.i("AppAd {}", appAd);
        return appAd;
    }

    private TAppAd createTAppAd(AppAd appAd) {
        TAppAd tAppAd = new TAppAd();
        tAppAd.setPackageName(appAd.getPackageName());
        tAppAd.setTitle(appAd.getTitle());
        tAppAd.setSubtitle(appAd.getSubtitle());
        tAppAd.setCategories(appAd.getCategories());
        tAppAd.setStarRating(appAd.getStartRating());
        tAppAd.setInstalls(appAd.getInstalls());

        tAppAd.setIconUrl(icsApi.appAdApi().getIconUri(appAd).toString());
        tAppAd.setBackgroundUrl(icsApi.appAdApi().getBackgroundUri(appAd).toString());

        Timber.i("AppAd {}", tAppAd);
        return tAppAd;
    }

    private BrandAd createBrandAd(TBrandAd tbrandAd) {
        BrandAd brandAd = new BrandAd(0L);
        brandAd.setName(tbrandAd.getName());
        brandAd.setTitle(tbrandAd.getTitle());
        brandAd.setHeadline(tbrandAd.getHeadline());
        brandAd.setLandingUrl(tbrandAd.getLandingUrl());

        Timber.i("BrandAd {}", brandAd);
        return brandAd;
    }

    private TBrandAd createTBrandAd(BrandAd brandAd) {
        TBrandAd tbrandAd = new TBrandAd();
        tbrandAd.setName(brandAd.getName());
        tbrandAd.setTitle(brandAd.getTitle());
        tbrandAd.setHeadline(brandAd.getHeadline());
        tbrandAd.setLandingUrl(brandAd.getLandingUrl());

        Timber.i("TBrandAd {}", tbrandAd);
        return tbrandAd;
    }

    @Override
    public void openDeeplink(final TAd tad, TDeepLink deeplink) {
        final Deeplink dl = new Deeplink();
        dl.setPackageName(deeplink.getPackageName());

        UrlItem urlItem = new UrlItem(deeplink.getPrimaryUrl(), UrlItem.ActionType.OPEN);
        dl.setPrimary(urlItem);

        UrlItem urlItem2 = new UrlItem(deeplink.getPrimaryUrl(), UrlItem.ActionType.DOWNLOAD);
        dl.setFallback(urlItem2);

        final Ad ad = fromTAd(tad);
        openDeeplink(ad, dl);
    }

    private void openDeeplink(final Ad ad, final Deeplink deeplink) {
        pool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    icsApi.openDeeplink(ad, deeplink);
                } catch (Exception e) {
                    Timber.e(e, "Unable to call openDeeplink(%s)", ad);
                }
            }
        });
    }
}
