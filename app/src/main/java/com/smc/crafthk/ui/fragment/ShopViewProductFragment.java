package com.smc.crafthk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.databinding.FragmentProductBinding;
import com.smc.crafthk.databinding.FragmentShopViewProductBinding;
import com.smc.crafthk.entity.Product;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.ProductAdapter;
import com.smc.crafthk.ui.product.CreateProductActivity;

import java.util.ArrayList;
import java.util.List;

public class ShopViewProductFragment extends Fragment {

    FragmentShopViewProductBinding binding;
    ProductAdapter adapter;
    int shopId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShopViewProductBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Bundle bundle = getArguments();
        shopId = bundle.getInt(Constraint.SHOP_ID_INTENT_EXTRA);


        RecyclerView productView = binding.listProduct;
        productView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(new ArrayList<>(), position->{});
        productView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ProductDao productDao = AppDatabase.getDatabase(getContext()).productDao();
        List<Product> productList = productDao.getProductsByShopId(shopId);
        adapter.setData(productList);
        adapter.notifyDataSetChanged();
    }
}