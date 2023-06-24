package com.smc.crafthk.implementation;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.entity.ProductWithShopInfo;
import com.smc.crafthk.ui.home.ShopViewPagerActivity;

import java.io.File;
import java.util.List;

public class ProductWithShopInfoAdapter extends RecyclerView.Adapter<ProductWithShopInfoAdapter.ProductViewHolder> {

    private List<ProductWithShopInfo> list;
    private OnItemClickListener onItemClickListener;

    public ProductWithShopInfoAdapter(List<ProductWithShopInfo> list, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<ProductWithShopInfo> list){
        this.list = list;
    }
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    public void appendItems(List<ProductWithShopInfo> items) {
        list.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        ProductWithShopInfo product = list.get(position);
        holder.textProductName.setText(product.product.productName);
        holder.textPrice.setText("$"+product.product.productPrice.toString());
        holder.textShopName.setText(product.shop.name);

        Glide.with(holder.itemView.getContext())
                .load(new File(product.product.productImagePath))
                .into(holder.imageView);

        Glide.with(holder.itemView.getContext())
                .load(new File(product.shop.shopImagePath))
                .circleCrop()
                .into(holder.shopImageView);

        holder.shopImageView.setOnClickListener(v->{
            Intent intent = new Intent(holder.textShopName.getContext(), ShopViewPagerActivity.class);
            intent.putExtra(Constraint.SHOP_ID_INTENT_EXTRA, product.shop.id);
            holder.textShopName.getContext().startActivity(intent);
        });

        holder.textShopName.setOnClickListener(v->{
            Intent intent = new Intent(holder.textShopName.getContext(), ShopViewPagerActivity.class);
            intent.putExtra(Constraint.SHOP_ID_INTENT_EXTRA, product.shop.id);
            holder.textShopName.getContext().startActivity(intent);
        });

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

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView shopImageView;
        TextView textProductName;
        TextView textPrice;
        TextView textShopName;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_product);
            shopImageView = itemView.findViewById(R.id.image_shop);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textPrice = itemView.findViewById(R.id.text_price);
            textShopName = itemView.findViewById(R.id.text_shop_name);
        }
    }
}