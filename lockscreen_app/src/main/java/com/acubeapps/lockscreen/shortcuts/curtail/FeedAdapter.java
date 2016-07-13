package com.acubeapps.lockscreen.shortcuts.curtail;

import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.cards.DemoUtils;
import com.acubeapps.lockscreen.shortcuts.player.DemoPlayer;
import com.acubeapps.lockscreen.shortcuts.video.VideoStore;
import com.inmobi.oem.thrift.ad.model.TVideo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import timber.log.Timber;

import java.io.File;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by ritwik on 01/07/16.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private static final int VIEW_TYPE_FEED_ITEM = 1;
    private static final int VIEW_TYPE_FEED_END = 2;
    public static final double ITEM_ASPECT_RATIO = 1.0;

    private final Context context;
    private final List<TVideo> items;
    private final int itemContentWidth;
    private final int itemContentHeight;
    private final int enderWidth;
    private final int enderHeight;
    private final ScrollListener scrollListener;
    private int itemHeight = 0;
    private File externalStorageDirectory;

    @Inject
    Picasso picasso;

    @Inject
    VideoStore videoStore;

    VideoClickListener videoClickListener;

    public FeedAdapter(@NonNull Context context, @NonNull List<TVideo> items, VideoClickListener videoClickListener) {
        this.context = context;
        this.items = items;
        this.videoClickListener = videoClickListener;

        int deviceWidth = context.getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = context.getResources().getDisplayMetrics().heightPixels;
        itemContentWidth = deviceWidth;
        itemContentHeight = (int) (itemContentWidth / ITEM_ASPECT_RATIO);

        enderHeight = deviceHeight + getNavbarHeight();
        enderWidth = deviceWidth;
        this.scrollListener = new ScrollListener();
        Injectors.appComponent().injectFeedAdapter(this);
    }

    void releasePlayers(RecyclerView recyclerView) {
        for (int i = 0, len = recyclerView.getChildCount(); i < len; i++) {
            View child = recyclerView.getChildAt(i);
            ViewHolder viewHolder = (ViewHolder) recyclerView.getChildViewHolder(child);
            viewHolder.releasePlayer();
        }
    }

    private int getNavbarHeight() {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public RecyclerView.OnScrollListener getScrollListener() {
        return scrollListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_FEED_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item,
                        parent, false);
                return new FeedItemViewHolder(view);

            case VIEW_TYPE_FEED_END:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_end,
                        parent, false);
                return new FeedEndViewHolder(view);

            default:
                throw new IllegalStateException("Unknown viewType : " + viewType);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < items.size()) {
            return VIEW_TYPE_FEED_ITEM;
        }
        return VIEW_TYPE_FEED_END;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        externalStorageDirectory = Environment.getExternalStorageDirectory();

        if (viewType == VIEW_TYPE_FEED_ITEM) {
            FeedItemViewHolder vh = (FeedItemViewHolder) viewHolder;
            TVideo video = items.get(position);

            bindFeedItem(vh, video);

            vh.initPlayer();
            vh.scrollY = itemHeight * position;
            vh.position = position;

            return;
        }

        if (viewType == VIEW_TYPE_FEED_END) {
            FeedEndViewHolder vh = (FeedEndViewHolder) viewHolder;

            bindFeedEnd(vh);
            vh.initPlayer();

            vh.scrollY = itemHeight * items.size();
        }
    }

    private void bindFeedEnd(FeedEndViewHolder vh) {
        Uri uri = Uri.parse("file:///android_asset" + "/comeback.webm");
        vh.playerView.setContentUri(uri);
        vh.playerView.preparePlayer(true, false);
        vh.thumbnailView.setImageResource(R.drawable.comeback);
    }

    private void bindFeedItem(FeedItemViewHolder vh, TVideo video) {
        String downloadedPreviewUri = videoStore.getDownloadedPreviewUri(video.getId());
        if (downloadedPreviewUri != null) {
            Uri uri = Uri.parse(downloadedPreviewUri);
            //Uri uri = Uri.parse(externalStorageDirectory.toURI() + "/Download/curtail/clip-small.webm");
            vh.uri = uri;
            vh.playerView.setContentUri(uri);
            vh.playerView.preparePlayer(true, false);
        }

        vh.titleView.setText(video.getTitle());
        vh.durationView.setText(String.format("%2.2f", (video.getDuration() / 60.0)));
        if (video.getSource() == null) {
            vh.sourceView.setText(null);
        } else {
            vh.sourceView.setText(video.getSource().getName());
        }
        String loves = DemoUtils.format(DemoUtils.getLikesCount(video));

        vh.lovesView.setText(loves);
        if (video.isSetPreview() && video.getPreview().isSetThumbnailUrl()) {
            picasso.load(video.getPreview().getThumbnailUrl()).into(vh.thumbnailView);
        } else {
            picasso.load(video.getThumbnailUrl()).into(vh.thumbnailView);
        }

    }

    @Override
    public void onViewRecycled(ViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);

        if (viewHolder instanceof FeedItemViewHolder) {
            FeedItemViewHolder vh = (FeedItemViewHolder) viewHolder;
            vh.playerView.releasePlayer();
            vh.clipView.setVisibility(View.INVISIBLE);
            return;
        }

        if (viewHolder instanceof FeedEndViewHolder) {
            FeedEndViewHolder vh = (FeedEndViewHolder) viewHolder;
            vh.playerView.releasePlayer();
            vh.clipView.setVisibility(View.INVISIBLE);
            return;
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }


    abstract class ViewHolder extends RecyclerView.ViewHolder {

        Uri uri;
        int scrollY;
        int height;

        public ViewHolder(View view) {
            super(view);
        }

        void measureSize(View view) {
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            height = view.getMeasuredHeight();
        }

        void onScrollY(int scrollY) {
            scrollY -= this.scrollY;
            onRelativeScroll(scrollY);
        }

        protected abstract void onRelativeScroll(int scrollY);

        protected abstract void initPlayer();

        protected abstract void releasePlayer();

        final LivePlayerTextureView setupPlayer(final ViewGroup clipView) {
            final LivePlayerTextureView playerView =
                    new LivePlayerTextureView(clipView.getContext());
            playerView.setPlayerStateListener(new DemoPlayer.Listener() {
                @Override
                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    playerView.setMute(true);
                    switch (playbackState) {
                        case DemoPlayer.STATE_PREPARING:
                        case DemoPlayer.STATE_IDLE:
                            clipView.setVisibility(View.INVISIBLE);
                            break;

                        case DemoPlayer.STATE_READY:
                            clipView.setVisibility(View.VISIBLE);
                            break;

                        case DemoPlayer.STATE_ENDED:
                            playerView.preparePlayer(true, false);
                            break;

                        default:
                            break;

                    }
                }

                @Override
                public void onError(Exception ex) {

                }

                @Override
                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                               float pixelWidthHeightRatio) {

                }
            });
            clipView.addView(playerView.getVideoFrame());
            return playerView;
        }
    }

    class FeedEndViewHolder extends ViewHolder {

        @BindView(R.id.thumbnail)
        ImageView thumbnailView;

        @BindView(R.id.container)
        ViewGroup containerView;

        @BindView(R.id.containerContent)
        RelativeLayout containerContentView;

        @BindView(R.id.clip)
        ViewGroup clipView;

        @BindView(R.id.darkness)
        View darknessView;

        private final LivePlayerTextureView playerView;

        public FeedEndViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(enderWidth, enderHeight);
            containerView.setLayoutParams(layoutParams);

            measureSize(view);

            playerView = setupPlayer(clipView);
        }

        @Override
        protected void onRelativeScroll(int scrollY) {
            // dark curtain effect

            float ratio = ((float) height + scrollY) / height;
            ratio = 1 - ratio;

            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(enderWidth, enderHeight);
            layoutParams.topMargin = scrollY;
            containerContentView.setLayoutParams(layoutParams);

            darknessView.setBackgroundColor(Color.argb((int) (256 * ratio), 0, 0, 0));
        }

        @Override
        protected void initPlayer() {
            playerView.setContentUri(uri);
            playerView.preparePlayer(true, false);
        }

        @Override
        protected void releasePlayer() {
            playerView.releasePlayer();
        }
    }

    class FeedItemViewHolder extends ViewHolder {

        @BindView(R.id.thumbnail)
        ImageView thumbnailView;

        @BindView(R.id.container)
        ViewGroup containerView;

        @BindView(R.id.overlay)
        RelativeLayout overlayView;

        @BindView(R.id.title)
        TextView titleView;

        @BindView(R.id.clip)
        ViewGroup clipView;

        @BindView(R.id.gestureView)
        View gestureView;

        @BindView(R.id.duration)
        TextView durationView;

        @BindView(R.id.source)
        TextView sourceView;

        @BindView(R.id.loves)
        TextView lovesView;

        private final LivePlayerTextureView playerView;

        int position;

        public FeedItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(itemContentWidth, itemContentHeight);
            containerView.setLayoutParams(layoutParams);

            if (itemHeight <= 0) {
                measureSize(view);
                itemHeight = height;
            } else {
                height = itemHeight;
            }

            playerView = setupPlayer(clipView);
            gestureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    videoClickListener.onClick(view, position);
                }
            });
        }

        @Override
        protected void onRelativeScroll(int scrollY) {
            // parallax effect

            float ratio = ((float) scrollY) / height;

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.topMargin = (int) (ratio * height * 0.03);
            overlayView.setLayoutParams(layoutParams);
        }

        private void showVideo(int position) {
            Timber.d("showVideo(%d)", position);
        }

        @Override
        protected void initPlayer() {
            playerView.setContentUri(uri);
            playerView.preparePlayer(true, false);
        }

        @Override
        protected void releasePlayer() {
            playerView.releasePlayer();
        }

    }

    public interface VideoClickListener {
        void onClick(View view, int position);
    }

    class ScrollListener extends RecyclerView.OnScrollListener {

        int scrollY = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            scrollY += dy;
            for (int i = 0, len = recyclerView.getChildCount(); i < len; i++) {
                View child = recyclerView.getChildAt(i);
                ViewHolder viewHolder = (ViewHolder) recyclerView.getChildViewHolder(child);
                viewHolder.onScrollY(scrollY);
            }
        }
    }
}
