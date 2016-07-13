package com.acubeapps.lockscreen.shortcuts.curtail;

import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.core.card.MagazineActivity;
import com.inmobi.oem.thrift.ad.model.TContent;
import com.inmobi.oem.thrift.ad.model.TMagazine;
import com.inmobi.oem.thrift.ad.model.TVideo;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class CurtailActivity extends MagazineActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbarView;

//    @BindView(R.id.tabs)
//    TabLayout tabView;

    @BindView(R.id.container)
    ViewPager containerView;

    @Inject
    EventBus eventBus;

    @Inject
    SharedPreferences preferences;

    private FeedFragment feedFragment;

    @Override
    protected void initialize() {
        setContentView(R.layout.activity_curtail);
        ButterKnife.bind(this);
        Injectors.appComponent().injectCurtailActivity(this);
    }

    private List<TVideo> extractVideos(TMagazine magazine) {
        List<TVideo> videos = new ArrayList<>();
        for (TContent content : magazine.getHeader()) {
            if (content.isSetVideo()) {
                videos.add(content.getVideo());
            }
        }
        for (TContent content : magazine.getContents()) {
            if (content.isSetVideo()) {
                videos.add(content.getVideo());
            }
        }
        return videos;
    }


//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        feedFragment.resume();
    }

    @Override
    protected void bindData(TMagazine magazine) {
        Timber.d("Magazine : %s", magazine);
        List<TVideo> videos = extractVideos(magazine);
        setupContainer(videos);
    }

    @Override
    protected void onPause() {
        super.onPause();
        feedFragment.pause();
    }

    @Override
    protected int getBackgroundColor() {
        return getResources().getColor(R.color.colorPrimary);
    }

    @Override
    protected void onCloseOverlay() {
        findViewById(R.id.heading).setVisibility(View.VISIBLE);
    }


    private void setupContainer(final List<TVideo> videos) {
        feedFragment = new FeedFragment(videos, preferences, new FeedAdapter.VideoClickListener() {
            @Override
            public void onClick(View view, int position) {
                TVideo video = videos.get(position);
                onClickVideo(video);
                findViewById(R.id.heading).setVisibility(View.GONE);
            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(feedFragment, "Feed");
//        adapter.addFragment(new FeedFragment(), "Saved");
        containerView.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
