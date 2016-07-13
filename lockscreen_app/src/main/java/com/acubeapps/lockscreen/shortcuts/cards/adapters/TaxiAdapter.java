package com.acubeapps.lockscreen.shortcuts.cards.adapters;

import com.acubeapps.lockscreen.shortcuts.R;
import com.inmobi.oem.thrift.ad.model.TTaxi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by aasha.medhi on 5/29/16.
 */
public class TaxiAdapter extends RecyclerView.Adapter<TaxiAdapter.MyViewHolder> {

    private List<TTaxi> taxiList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView surgePricing;
        TextView distance;
        ImageView icon;
        ImageView imgSurgePricing;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txtTaxiName);
            price = (TextView) view.findViewById(R.id.txtTaxiPrice);
            icon = (ImageView) view.findViewById(R.id.imgTaxiIcon);
            surgePricing = (TextView) view.findViewById(R.id.txtTaxiSurgePrice);
            distance = (TextView) view.findViewById(R.id.txtTaxiDistance);
            imgSurgePricing = (ImageView) view.findViewById(R.id.imgTaxiSurgePriceIcon);
        }
    }

    public TaxiAdapter(List<TTaxi> taxiList, Context context) {
        this.taxiList = taxiList;
        this.context = context;
    }

    @Override
    public TaxiAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.taxi_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TaxiAdapter.MyViewHolder holder, int position) {
        final TTaxi taxi = this.taxiList.get(position);
        holder.name.setText(taxi.getName());
        holder.price.setText(taxi.getPrice());
        Picasso.with(context)
                .load(taxi.getImageUrl())
                .into(holder.icon);

        if (taxi.isSetSurgeFactor()) {
            holder.surgePricing.setText(String.format("%.2g%n", taxi.getSurgeFactor()));
        } else {
            holder.surgePricing.setVisibility(View.GONE);
            holder.imgSurgePricing.setVisibility(View.GONE);
        }
        holder.distance.setText(taxi.getEstimate().getTimeInMintues() + " mins away");
    }

    @Override
    public int getItemCount() {
        return this.taxiList.size();
    }
}

