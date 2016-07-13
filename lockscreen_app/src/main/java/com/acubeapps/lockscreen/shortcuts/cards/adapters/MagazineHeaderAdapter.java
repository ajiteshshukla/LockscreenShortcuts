package com.acubeapps.lockscreen.shortcuts.cards.adapters;

import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.cards.DemoUtils;
import com.inmobi.oem.thrift.ad.model.TContent;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import timber.log.Timber;

import java.util.List;

/**
 * Created by aasha.medhi on 6/21/16.
 */
public class MagazineHeaderAdapter extends PagerAdapter {

    private Context mContext;
    private List<TContent> magazineVideoList;
    Picasso picasso;
    MagazineContentAdapter.VideoClickListener videoClickListener;
    VideoScrollListener videoScrollListener;


    public MagazineHeaderAdapter(Context context, MagazineContentAdapter.VideoClickListener videoClickListener,
                                 List<TContent> magazineHeaderVideos, Picasso picasso,
                                 VideoScrollListener videoScrollListener) {
        mContext = context;
        magazineVideoList = magazineHeaderVideos;
        this.picasso = picasso;
        this.videoClickListener = videoClickListener;
        this.videoScrollListener = videoScrollListener;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        Timber.d("Instantiate item %s", position);
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup rowLayout = (ViewGroup) layoutInflater.inflate(
                R.layout.magazine_header_adapter_item, null);
        ImageView imageView = (ImageView) rowLayout.findViewById(R.id.imageView);
        TextView textViewTitle = (TextView) rowLayout.findViewById(R.id.textVideoTitle);
        TextView textViewLikeCount = (TextView) rowLayout.findViewById(R.id.textLikeCount);
        picasso.load(magazineVideoList.get(position).getVideo().getThumbnailUrl()).into(imageView);
        textViewTitle.setText(magazineVideoList.get(position).getVideo().getTitle());
        long likeCount = DemoUtils.getLikesCount(magazineVideoList.get(position)
                .getVideo());
        textViewLikeCount.setText("" + DemoUtils.format(likeCount));
        collection.addView(rowLayout);
        final GestureDetector detector = new GestureDetector(mContext,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent ev) {
                        videoClickListener.onClick(rowLayout, 0);
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        Timber.d("Header Entered onScroll. Speeding scroll e1 %s e2 %s dx %s dy %s", e1, e2, distanceX,
                                distanceY);
                        videoScrollListener.onStartScroll();
                        return true;
                    }



                });
        rowLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        return rowLayout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return magazineVideoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
