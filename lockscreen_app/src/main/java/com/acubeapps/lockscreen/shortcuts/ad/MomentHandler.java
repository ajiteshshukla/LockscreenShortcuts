package com.acubeapps.lockscreen.shortcuts.ad;

import com.acubeapps.lockscreen.shortcuts.adstore.AdStore;
import com.acubeapps.lockscreen.shortcuts.adstore.InternalAd;
import com.acubeapps.lockscreen.shortcuts.video.VideoService;
import com.acubeapps.lockscreen.shortcuts.video.VideoStore;
import com.inmobi.oem.moments.matcher.MomentApi;
import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TAppAd;
import com.inmobi.oem.thrift.ad.model.TContent;
import com.inmobi.oem.thrift.ad.model.TMagazine;
import com.inmobi.oem.thrift.ad.model.TMovie;
import com.inmobi.oem.thrift.ad.model.TMovieAd;
import com.inmobi.oem.thrift.ad.model.TRestaurant;
import com.inmobi.oem.thrift.ad.model.TRestaurantAd;
import com.inmobi.oem.thrift.ad.model.TTaxi;
import com.inmobi.oem.thrift.ad.model.TTaxiAd;
import com.inmobi.oem.thrift.ad.model.TVideo;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.Picasso;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul.srivastava on 08/06/16.
 */
public class MomentHandler implements MomentApi.AdCallback {

    private final AdStore adStore;

    private final VideoStore videoStore;

    private final Context context;

    private final Picasso picasso;

    private final VideoService videoService;

    public MomentHandler(Context context, AdStore adStore, VideoStore videoStore, Picasso picasso,
                         VideoService videoService) {
        this.context = context;
        this.adStore = adStore;
        this.videoStore = videoStore;
        this.picasso = picasso;
        this.videoService = videoService;
    }

    @Override
    public void onAdsReceived(String momentId, List<TAd> adList) {
        Timber.i("onAdsReceived callback to MomentHandler %s", adList);
        if (adList != null) {
            for (TAd ad : adList) {
                if (ad.isSetApp()) {
                    processAndStoreAppAd(momentId, ad);
                } else if (ad.isSetCommerce()) {
                    processAndStoreDodAd(momentId, ad);
                } else if (ad.isSetMovie() || ad.isSetTaxi() || ad.isSetRestaurant()) {
                    processAndStoreO2OAd(momentId, ad);
                } else if (ad.isSetBrand()) {
                    processAndStoreBrandAd(momentId, ad);
                } else if (ad.isSetMagazine()) {
                    processAndStoreMagazine(momentId, ad);
                }
            }
        } else {
            Timber.i("onAdsReceived callback to MomentHandler with empty adList");
        }
    }

    private void processAndStoreAppAd(String momentId, TAd ad) {
        TAppAd appAd = ad.getApp();
        picasso.load(appAd.getIconUrl()).fetch();
        picasso.load(appAd.getBackgroundUrl()).fetch();
        addToAdStore(new InternalAd(momentId, ad));
    }

    private void processAndStoreDodAd(String momentId, TAd ad) {
        addToAdStore(new InternalAd(momentId, ad));
    }

    private void processAndStoreBrandAd(String momentId, TAd ad) {
        addToAdStore(new InternalAd(momentId, ad));
    }

    private void processAndStoreO2OAd(String momentId, TAd ad) {
        if (ad.isSetTaxi()) {
            TTaxiAd tTaxiAd = ad.getTaxi();
            picasso.load(tTaxiAd.getMerchantInfo().getIcon()).fetch();
            List<TTaxi> taxiList = tTaxiAd.getTaxis();
            for (TTaxi tTaxi : taxiList) {
                //Cache image urls of taxis
                picasso.load(tTaxi.getImageUrl()).fetch();
            }
        } else if (ad.isSetMovie()) {
            TMovieAd movieAd = ad.getMovie();
            picasso.load(movieAd.getMerchantInfo().getIcon()).fetch();
            List<TMovie> movieList = movieAd.getMovies();
            for (TMovie tMovie : movieList) {
                picasso.load(tMovie.getPosterUrl()).fetch();
            }
        } else if (ad.isSetRestaurant()) {
            TRestaurantAd tRestaurantAd = ad.getRestaurant();
            picasso.load(tRestaurantAd.getMerchantInfo().getIcon()).fetch();
            List<TRestaurant> tRestaurantList = tRestaurantAd.getRestaurants();
            for (TRestaurant tRestaurant : tRestaurantList) {
                picasso.load(tRestaurant.getImageUrl()).fetch();
            }
        }
        addToAdStore(new InternalAd(momentId, ad));
    }

    private void processAndStoreMagazine(String momentId, TAd ad) {
        Timber.d("MomentHandler processAndStoreMagazine");
        TMagazine magazine = ad.getMagazine();
        picasso.load(magazine.getNudgeIcon()).fetch();
        List<TVideo> videos = new ArrayList<>();
        for (TContent content : magazine.getHeader()) {
            if (content.isSetVideo()) {
                processVideo(content.getVideo());
                videos.add(content.getVideo());
            }
        }

        for (TContent content : magazine.getContents()) {
            if (content.isSetVideo()) {
                processVideo(content.getVideo());
                videos.add(content.getVideo());
            }
        }

        addToAdStore(new InternalAd(momentId, ad));

        if (!videos.isEmpty()) {
            videoService.getVideoUrls(videos, new VideoService.Callback() {
                @Override
                public void onVideo(String videoId, String url) {
                    // ignore
                }
            }, true);
        }
    }

    private void processVideo(TVideo video) {
        picasso.load(video.getThumbnailUrl()).fetch();
        if (video.isSetSource()) {
            if (video.getSource().isSetIcon()) {
                picasso.load(video.getSource().getIcon()).fetch();
            }
        }
        try {
            //TODO extract url from video.getPreview().getVideoUrl
            if (video.isSetPreview()) {
                if (videoStore.getDownloadId(video.getId()) == 0 && video.getPreview().isSetVideoUrl()) {
                    DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(video.getPreview().getVideoUrl()));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                    request.setVisibleInDownloadsUi(false);
                    long downloadId = dm.enqueue(request);
                    videoStore.setDownloadId(video.getId(), downloadId);
                    videoStore.apply();

                    Timber.d("Submitting to DM %s %d", video.getId(), downloadId);
                }

                if (video.getPreview().isSetThumbnailUrl()) {
                    picasso.load(video.getPreview().getThumbnailUrl()).fetch();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e(e, "Error");
        }
    }

    private void addToAdStore(InternalAd ad) {
        Timber.i("Added ad " + ad + "to adstore");
        adStore.addAd(ad);
    }

    @Override
    public void onAdsExpired(String momentId) {
        //expire from adstore
        Timber.i("MomentHandler: onAdsExpired invoked " + momentId);
        //Delete app from app install store if ad is expired
        adStore.clearNudgeById(momentId);
    }

}
