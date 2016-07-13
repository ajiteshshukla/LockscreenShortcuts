package com.acubeapps.lockscreen.shortcuts.cards.adapters;

import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.utils.RoundedCornerTransform;
import com.inmobi.oem.thrift.ad.model.TEstimate;
import com.inmobi.oem.thrift.ad.model.TRestaurant;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;
import java.util.Locale;

import static com.acubeapps.lockscreen.shortcuts.Constants.METERS_IN_ONE_MILE;

/**
 * Created by aasha.medhi on 5/29/16.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder> {

    private List<TRestaurant> restaurantList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView cuisine;
        TextView distance;
        TextView review;
        RatingBar rating;
        public ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txtRestaurantName);
            rating = (RatingBar) view.findViewById(R.id.ratingMovieRating);
            distance = (TextView) view.findViewById(R.id.txtRestaurantDistance);
            cuisine = (TextView) view.findViewById(R.id.txtRestaurantcuisines);
            review = (TextView) view.findViewById(R.id.txtRestaurantReview);
            icon = (ImageView) view.findViewById(R.id.imgRestaurantIcon);
        }
    }

    public RestaurantAdapter(List<TRestaurant> restaurantList, Context context) {
        this.restaurantList = restaurantList;
        this.context = context;
    }

    @Override
    public RestaurantAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantAdapter.MyViewHolder holder, int position) {
        TRestaurant restaurant = this.restaurantList.get(position);
        final Transformation transformation = new RoundedCornerTransform(10,
                RoundedCornerTransform.Corners.LEFT);
        Picasso.with(context).load(restaurant.getImageUrl()).transform(transformation).into(holder.icon);

        holder.name.setText(restaurant.getName());
        Double ratingDouble = restaurant.getRating();
        holder.rating.setRating(ratingDouble.floatValue());
        if (restaurant.isSetEstimate()) {
            TEstimate estimate = restaurant.getEstimate();
            if (estimate.isSetDistanceInMeters()) {
                double distanceInMile = estimate.getDistanceInMeters() / METERS_IN_ONE_MILE;
                if (distanceInMile <= 1) {
                    holder.distance.setText(
                            String.format(Locale.US,
                                    "%.2f", distanceInMile) + " mile");
                } else {
                    holder.distance.setText(
                            String.format(Locale.US,
                                    "%.2f", distanceInMile) + " miles");
                }
            }
        }
        StringBuilder appendedCuisine = new StringBuilder();
        if (restaurant.isSetCuisines() && restaurant.getCuisines() != null) {
            for (String cuisine : restaurant.getCuisines()) {
                appendedCuisine.append(cuisine + ",");
            }
        }
        String appendedCuisineStr = appendedCuisine.toString();
        if (appendedCuisine.length() > 0) {
            //Remove trailing comma
            int length = appendedCuisine.lastIndexOf(",");
            appendedCuisineStr = appendedCuisine.substring(0, length);
        }
        holder.cuisine.setText(appendedCuisineStr);
        if (restaurant.getReviewText() == null || restaurant.getReviewText().trim().length() == 0) {
            holder.review.setVisibility(View.GONE);
        } else {
            holder.review.setText("\"" + restaurant.getReviewText() + "\"");
        }
    }

    @Override
    public int getItemCount() {
        return this.restaurantList.size();
    }
}
