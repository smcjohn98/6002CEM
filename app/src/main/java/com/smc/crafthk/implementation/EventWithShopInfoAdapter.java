package com.smc.crafthk.implementation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smc.crafthk.R;
import com.smc.crafthk.entity.EventWithShopInfo;

import java.io.File;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventWithShopInfoAdapter extends RecyclerView.Adapter<EventWithShopInfoAdapter.EventViewHolder> {

    private List<EventWithShopInfo> list;
    private OnItemClickListener onItemClickListener;

    public EventWithShopInfoAdapter(List<EventWithShopInfo> list, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<EventWithShopInfo> data){
        list = data;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        EventWithShopInfo eventWithShopInfo = list.get(position);
        holder.textEventName.setText(eventWithShopInfo.event.eventName);
        if(eventWithShopInfo.event.eventPrice.compareTo(BigDecimal.ZERO) > 0)
            holder.textPrice.setText("$"+eventWithShopInfo.event.eventPrice.toString());
        else
            holder.textPrice.setText("Free");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        holder.textDateTime.setText(dateTimeFormatter.format(eventWithShopInfo.event.eventDateTime));
        holder.textShopName.setText(eventWithShopInfo.shop.name);

        Glide.with(holder.itemView.getContext())
                .load(new File(eventWithShopInfo.event.eventImagePath))
                .into(holder.imageView);

        Glide.with(holder.itemView.getContext())
                .load(new File(eventWithShopInfo.shop.shopImagePath))
                .circleCrop()
                .into(holder.shopImageView);

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
        ImageView shopImageView;
        TextView textEventName;
        TextView textPrice;
        TextView textShopName;
        TextView textDateTime;

        public EventViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_event);
            shopImageView = itemView.findViewById(R.id.image_shop);
            textEventName = itemView.findViewById(R.id.text_event_name);
            textPrice = itemView.findViewById(R.id.text_price);
            textShopName = itemView.findViewById(R.id.text_shop_name);
            textDateTime = itemView.findViewById(R.id.text_date_time);
        }
    }
}