package com.acubeapps.lockscreen.shortcuts.cards.adapters;

import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.analytics.Analytics;
import com.acubeapps.lockscreen.shortcuts.cards.DemoUtils;
import com.acubeapps.lockscreen.shortcuts.cards.model.MagazineContentItemLayout;
import com.acubeapps.lockscreen.shortcuts.cards.model.MagazineHeaderItemLayout;
import com.inmobi.oem.thrift.ad.model.TVideo;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.felipecsl.asymmetricgridview.AGVRecyclerViewAdapter;
import com.felipecsl.asymmetricgridview.AsymmetricItem;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;
import timber.log.Timber;

import java.lang.reflect.Field;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by aasha.medhi on 6/16/16.
 */
public class MagazineContentAdapter extends AGVRecyclerViewAdapter<MagazineContentAdapter.MyViewHolder> {
    private static final int HEADER = 0;
    private static final int CONTENT = 1;
    private List<AsymmetricItem> magazineItemList;
    private Context context;
    private VideoClickListener videoClickListener;
    private Analytics analytics;
    Picasso picasso;
    ViewPager viewPager;
    FixedSpeedScroller scroller;
    Handler handler;
    Runnable updatePage;


    @Inject
    public MagazineContentAdapter(List<AsymmetricItem> magazineItemList, Context context,
                                  VideoClickListener videoClickListener, Picasso picasso,
                                  Analytics analytics) {
        this.magazineItemList = magazineItemList;
        this.context = context;
        this.videoClickListener = videoClickListener;
        this.picasso = picasso;
        this.analytics = analytics;
    }

    @Override
    public MagazineContentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                viewType == HEADER ? R.layout.magazine_adapter_item_header
                        : R.layout.magazine_adapter_item, parent, false);
        return new MyViewHolder(itemView, viewType, videoClickListener);
    }

    @Override
    public void onBindViewHolder(MagazineContentAdapter.MyViewHolder holder, int position) {
        holder.bind(context, magazineItemList.get(position), getItemViewType(position), position);
    }

    @Override
    public int getItemCount() {
        return magazineItemList.size();
    }

    @Override public int getItemViewType(int position) {
        return position == 0 ? HEADER : CONTENT;
    }

    @Override
    public AsymmetricItem getItem(int position) {
        return magazineItemList.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {
        public ImageView imageView;
        private int position;
        public TextView textView;
        VideoClickListener videoClickListener;
        VideoScrollListener videoScrollListener;
        private ViewPager viewPager;
        private CirclePageIndicator viewPagerIndicator;
        private TextView textViewLikeCount;

        public MyViewHolder(final View view, int viewType, final VideoClickListener videoClickListener) {
            super(view);
            //view.setOnClickListener(this);

            if (viewType == HEADER) {
                viewPager = (ViewPager) view.findViewById(R.id.viewpager);
                viewPagerIndicator = (CirclePageIndicator) view.findViewById(
                        R.id.view_pager_indicator);
            } else {
                imageView = (ImageView) view.findViewById(R.id.imageView);
                textView = (TextView) view.findViewById(R.id.textVideoTitle);
                textViewLikeCount = (TextView) view.findViewById(R.id.textLikeCount);
            }
            this.videoClickListener = videoClickListener;
            this.videoScrollListener = new VideoScrollListener() {
                @Override
                public void onStartScroll() {
                    Timber.d("Entered VideoScrollListener onScroll");
                    handler.removeCallbacks(updatePage);
                    viewPager = (ViewPager) view.findViewById(R.id.viewpager);
                    scroller.setDuration(100);
                    handler.postDelayed(updatePage, 8000);
                }
            };

            final GestureDetector detector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent ev) {
                    videoClickListener.onClick(view, position);
                    return true;
                }

            });

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    detector.onTouchEvent(event);
                    return false;
                }
            });

        }

        public void bind(Context context, final AsymmetricItem item, int viewType, int position) {
            Timber.d("Position in bind %s", position);
            this.position = position;
            if (viewType == HEADER) {
                MagazineHeaderAdapter headerAdapter = new MagazineHeaderAdapter(context, videoClickListener,
                        ((MagazineHeaderItemLayout) item).getHeaderContent(), picasso, videoScrollListener);
                viewPager.setAdapter(headerAdapter);
                DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
                viewPager.getLayoutParams().width = displayMetrics.widthPixels;
                viewPager.requestLayout();
                try {
                    Field mScroller;
                    mScroller = ViewPager.class.getDeclaredField("mScroller");
                    mScroller.setAccessible(true);
                    scroller = new FixedSpeedScroller(viewPager.getContext(),
                            new AccelerateDecelerateInterpolator());
                    scroller.setDuration(800);
                    mScroller.set(viewPager, scroller);
                } catch (NoSuchFieldException e) {
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
                int pageCount = ((MagazineHeaderItemLayout) item).getHeaderContent().size();
                viewPager.addOnPageChangeListener(new CustomPageListener(viewPager, pageCount));
                viewPagerIndicator.setViewPager(viewPager);
                MagazineContentAdapter.this.viewPager = viewPager;
                handler = new Handler();
                updatePage = new Runnable() {
                    public void run() {
                        Timber.d("Entered update handler. resetting scroller duration");
                        scroller.setDuration(800);
                        int currentPage = viewPager.getCurrentItem();
                        if (currentPage == ((MagazineHeaderItemLayout) item).getHeaderContent()
                                .size() - 1) {
                            currentPage = 0;
                        } else {
                            currentPage++;
                        }
                        logTileViewed("H", currentPage);
                        viewPager.setCurrentItem(currentPage, true);
                        handler.postDelayed(this, 4000);
                    }
                };
                handler.postDelayed(updatePage, 4000);

            } else {
                TVideo video = ((MagazineContentItemLayout) item).getMagazineContent()
                        .getVideo();
                picasso.load(video
                        .getThumbnailUrl())
                        .into(imageView);
                textView.setText(video
                        .getTitle());
                long likeCount = DemoUtils.getLikesCount(video);

                textViewLikeCount.setText(DemoUtils.format(likeCount));
                logTileViewed("L", position);
            }

        }
    }

    public void stopMagazineHeaderAnimationHandler() {
        handler.removeCallbacks(updatePage);
    }

    public void pauseMagazineHeaderAnimation(long sleepDurationInMillis) {
        Timber.d("entered pauseMagazineHeaderAnimation, sleep duration %s", sleepDurationInMillis);
        handler.removeCallbacks(updatePage);
        handler.postDelayed(updatePage, sleepDurationInMillis);
    }

    public interface VideoClickListener {
        void onClick(View view, int position);
    }

    public void logTileViewed(String prefix, int tilePosition) {
        String position = String.format("%s:%d", prefix, tilePosition);
        analytics.tileViewed(position);
    }

    public int getCurrentHeaderItem() {
        return viewPager.getCurrentItem();
    }

}
