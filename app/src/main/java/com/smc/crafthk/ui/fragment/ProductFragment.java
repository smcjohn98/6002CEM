package com.smc.crafthk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.databinding.FragmentProductBinding;
import com.smc.crafthk.entity.Product;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.ProductAdapter;
import com.smc.crafthk.implementation.ShopAdapter;
import com.smc.crafthk.ui.product.CreateProductActivity;
import com.smc.crafthk.ui.shop.ShopActivity;
import com.smc.crafthk.ui.shop.ShopPagerActivity;

import java.util.List;

public class ProductFragment extends Fragment {

    FragmentProductBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Bundle bundle = getArguments();
        int shopId = bundle.getInt(Constraint.SHOP_ID_INTENT_EXTRA);

        ProductDao productDao = AppDatabase.getDatabase(getContext()).productDao();

        List<Product> productList = productDao.getProductsByShopId(shopId);
        RecyclerView productView = binding.listProduct;
        productView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        ProductAdapter adapter = new ProductAdapter(productList, position->{}, false);
        productView.setAdapter(adapter);

        binding.buttonCreate.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), CreateProductActivity.class);
            intent.putExtra(Constraint.SHOP_ID_INTENT_EXTRA, shopId);
            startActivity(intent);
        });
        return view;
    }
}