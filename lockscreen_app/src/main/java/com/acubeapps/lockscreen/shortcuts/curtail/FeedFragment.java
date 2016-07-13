package com.acubeapps.lockscreen.shortcuts.curtail;

import com.acubeapps.lockscreen.shortcuts.Constants;
import com.acubeapps.lockscreen.shortcuts.R;
import com.inmobi.oem.thrift.ad.model.TVideo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritwik on 01/07/16.
 */
public class FeedFragment extends Fragment {

    @BindView(R.id.mainFeed)
    RecyclerView mainFeed;
    private FeedAdapter feedAdapter;

    private final List<TVideo> videos;
    private SharedPreferences preferences;
    private FeedAdapter.VideoClickListener videoClickListener;

    public FeedFragment(List<TVideo> videos, SharedPreferences preferences,
                        FeedAdapter.VideoClickListener videoClickListener) {
        this.videos = videos;
        this.videoClickListener = videoClickListener;
        this.preferences = preferences;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed, container, false);

        ButterKnife.bind(this, view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mainFeed.setHasFixedSize(true);

        mainFeed.setLayoutManager(new LinearLayoutManager(getContext()));

        final List<TVideo> items = videos;

        feedAdapter = new FeedAdapter(getContext(), items, videoClickListener);
        mainFeed.setAdapter(feedAdapter);
        int currentVideoPosition = preferences.getInt(Constants.CURRENT_VIDEO_POSITION, -1);
        mainFeed.addOnScrollListener(feedAdapter.getScrollListener());
        //mainFeed.scrollToPosition(currentVideoPosition);
        return view;
    }

    private List<FeedItem> fetchItems() {
        List<FeedItem> result = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            result.add(new FeedItem());
        }
        return result;
    }

    public void pause() {
        if (feedAdapter != null) {
            feedAdapter.releasePlayers(mainFeed);
        }
    }

    public void resume() {
        if (feedAdapter != null) {
            feedAdapter.notifyDataSetChanged();
        }
    }
}
