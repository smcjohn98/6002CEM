package com.smc.crafthk.implementation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smc.crafthk.R;
import com.smc.crafthk.entity.Event;
import com.smc.crafthk.entity.Product;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> list;
    private OnItemClickListener onItemClickListener;

    private boolean displayShopName;
    public EventAdapter(List<Event> list, OnItemClickListener onItemClickListener, boolean displayShopName) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
        this.displayShopName = displayShopName;
    }

    public void setData(List<Event> data){
        list = data;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = list.get(position);
        holder.textEventName.setText(event.name);
        if(event.price.compareTo(BigDecimal.ZERO) > 0)
            holder.textPrice.setText("$"+event.price.toString());
        else
            holder.textPrice.setText("Free");
        holder.textDateTime.setText(event.dateTime.toString());
        if(!displayShopName){
            holder.textShopName.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext())
                .load(new File(event.imagePath))
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textEventName;
        TextView textPrice;
        TextView textShopName;
        TextView textDateTime;

        public EventViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_event);
            textEventName = itemView.findViewById(R.id.text_event_name);
            textPrice = itemView.findViewById(R.id.text_price);
            textShopName = itemView.findViewById(R.id.text_shop_name);
            textDateTime = itemView.findViewById(R.id.text_date_time);
        }
    }
}