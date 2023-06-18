package com.smc.crafthk.implementation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smc.crafthk.R;
import com.smc.crafthk.entity.Product;

import java.io.File;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> list;
    private OnItemClickListener onItemClickListener;

    public ProductAdapter(List<Product> list, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<Product> data){
        list = data;
    }
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = list.get(position);
        holder.textProductName.setText(product.productName);
        holder.textPrice.setText("$"+product.productPrice.toString());
        holder.shopInfoLayout.setVisibility(View.GONE);

        Glide.with(holder.itemView.getContext())
                .load(new File(product.productImagePath))
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

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textProductName;
        TextView textPrice;
        TextView textShopName;
        LinearLayout shopInfoLayout;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textPrice = itemView.findViewById(R.id.text_price);
            textShopName = itemView.findViewById(R.id.text_shop_name);
            shopInfoLayout = itemView.findViewById(R.id.shop_info);
        }
    }
}