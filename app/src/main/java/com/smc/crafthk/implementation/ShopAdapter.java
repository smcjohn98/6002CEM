package com.smc.crafthk.implementation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smc.crafthk.R;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.ui.shop.CreateShopActivity;

import java.io.File;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    private List<Shop> shopList;

    public ShopAdapter(List<Shop> shopList) {
        this.shopList = shopList;
    }

    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShopViewHolder holder, int position) {
        Shop shop = shopList.get(position);
        holder.textShopName.setText(shop.name);

        Glide.with(holder.itemView.getContext())
                .load(new File(shop.imagePath))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textShopName;

        public ShopViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textShopName = itemView.findViewById(R.id.text_shop_name);
        }
    }
}