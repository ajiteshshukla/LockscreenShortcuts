package com.acubeapps.lockscreen.shortcuts;

import com.acubeapps.lockscreen.shortcuts.ad.DownloadReceiver;
import com.acubeapps.lockscreen.shortcuts.cards.AppAdActivity;
import com.acubeapps.lockscreen.shortcuts.cards.DodActivity;
import com.acubeapps.lockscreen.shortcuts.cards.InterimVideoBrandAdActivity;
import com.acubeapps.lockscreen.shortcuts.cards.MovieAdActivity;
import com.acubeapps.lockscreen.shortcuts.cards.RestaurantAdActivity;
import com.acubeapps.lockscreen.shortcuts.cards.StaggeredMagazineActivity;
import com.acubeapps.lockscreen.shortcuts.cards.TaxiAdActivity;
import com.acubeapps.lockscreen.shortcuts.cards.VideoBrandAdActivity;
import com.acubeapps.lockscreen.shortcuts.core.NudgeService;
import com.acubeapps.lockscreen.shortcuts.core.WakeupReciever;
import com.acubeapps.lockscreen.shortcuts.curtail.CurtailActivity;
import com.acubeapps.lockscreen.shortcuts.curtail.FeedAdapter;

import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by ritwik on 29/05/16.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void injectMainApplication(MainApplication application);

    void injectTestActivity(TestActivity obj);

    void injectNudgeService(NudgeService obj);

    void injectAppAdActivity(AppAdActivity obj);

    void injectDodAdActivity(DodActivity obj);

    void injectMovieAdActivity(MovieAdActivity obj);

    void injectRestaurantAdActivity(RestaurantAdActivity obj);

    void injectTaxiAdActivity(TaxiAdActivity obj);

    void injectUnlockActivity(UnlockActivity obj);

    void injectVideoAdActivity(VideoBrandAdActivity obj);

    void injectInterimVideoBrandAdActivity(InterimVideoBrandAdActivity obj);

    void injectMagazineAdActivity(StaggeredMagazineActivity obj);

    void injectCurtailActivity(CurtailActivity obj);

    void injectWakeupReciever(WakeupReciever obj);

    void injectDownloadReciever(DownloadReceiver obj);

    void injectFeedAdapter(FeedAdapter obj);
}
