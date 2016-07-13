package com.acubeapps.lockscreen.shortcuts.cards.adapters;

import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.utils.RoundedCornerTransform;
import com.inmobi.oem.thrift.ad.model.TMovie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by aasha.medhi on 5/29/16.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private List<TMovie> movieList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgMovieIcon)
        public ImageView imgMovieIcon;

        @BindView(R.id.txtMovieName)
        public TextView txtMovieName;

        @BindView(R.id.ratingMovieRating)
        public RatingBar ratingBarMovie;

        @BindView(R.id.txtMovieCategory)
        public TextView txtMovieCategory;

        @BindView(R.id.txtMovieTheatreName)
        public TextView txtMovieTheatre;

        @BindView(R.id.txtShowTimings1)
        public TextView txtFirstTiming;

        @BindView(R.id.separatorView1)
        public View viewSeparator1;

        @BindView(R.id.txtShowTimings2)
        public TextView txtSecondTiming;

        @BindView(R.id.separatorView2)
        public View viewSeparator2;

        @BindView(R.id.txtShowTimings3)
        public TextView txtThirdTiming;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public MovieAdapter(List<TMovie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MyViewHolder holder, int position) {
        TMovie movie = this.movieList.get(position);
        final Transformation transformation = new RoundedCornerTransform(10,
                RoundedCornerTransform.Corners.LEFT);
        Picasso.with(context).load(movie.getPosterUrl()).transform(transformation).into(holder.imgMovieIcon);
        holder.txtMovieName.setText(movie.getTitle());
        holder.txtMovieCategory.setText(movie.getGenre());
        holder.ratingBarMovie.setRating(((Double) movie.getRating()).floatValue());
        holder.txtMovieTheatre.setText(movie.getAddress().getName());
        if (movie.getShowTimesSize() >= 1) {
            String movieTime = getMovieTime(movie.getShowTimes().get(0).getShowTime());
            holder.txtFirstTiming.setText(movieTime);
        }
        if (movie.getShowTimesSize() >= 2) {
            String movieTime = getMovieTime(movie.getShowTimes().get(1).getShowTime());
            holder.txtSecondTiming.setVisibility(View.VISIBLE);
            holder.viewSeparator1.setVisibility(View.VISIBLE);
            holder.txtSecondTiming.setText(movieTime);
        }
        if (movie.getShowTimesSize() >= 3) {
            String movieTime = getMovieTime(movie.getShowTimes().get(2).getShowTime());
            holder.txtThirdTiming.setVisibility(View.VISIBLE);
            holder.viewSeparator2.setVisibility(View.VISIBLE);
            holder.txtThirdTiming.setText(movieTime);
        }
    }

    private String getMovieTime(long timeInMillis) {
        Date date = new Date(timeInMillis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String movieTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
        String amPm = calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";
        return movieTime + " " + amPm;
    }

    @Override
    public int getItemCount() {
        return this.movieList.size();
    }
}
