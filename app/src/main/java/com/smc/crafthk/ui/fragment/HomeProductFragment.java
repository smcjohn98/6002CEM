package com.smc.crafthk.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smc.crafthk.constraint.CraftType;
import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.databinding.FragmentHomeProductBinding;
import com.smc.crafthk.entity.ProductWithShopInfo;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.ProductWithShopInfoAdapter;
import com.smc.crafthk.viewmodel.HomeProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeProductFragment extends Fragment {

    FragmentHomeProductBinding binding;
    ProductWithShopInfoAdapter adapter;

    HomeProductViewModel viewModel;

    private int pageOfSize = 6;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeProductBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(HomeProductViewModel.class);
        RecyclerView productView = binding.listProduct;
        productView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductWithShopInfoAdapter(new ArrayList<>(), position->{});
        productView.setAdapter(adapter);

        viewModel.getSearchOffset().observe(getActivity(), offset->{
            List<ProductWithShopInfo> productList = fetchProduct(pageOfSize, offset);

            if(offset == 0){
                adapter.setData(productList);
                adapter.notifyDataSetChanged();
            }
            else if(offset != 0 && productList.size() > 0){
                adapter.appendItems(productList);
            }
        });
        viewModel.getSearchProductName().observe(getActivity(), productName->{
            viewModel.setSearchOffset(0);
        });
        viewModel.getSearchProductType().observe(getActivity(), productType->{
            viewModel.setSearchOffset(0);
        });
        viewModel.setSearchOffset(0);

        SearchView searchView = binding.searchProductName;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchProductName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    viewModel.setSearchProductName(null);
                }
                return false;
            }
        });

        Spinner spinner = binding.spinnerProductType;
        ArrayAdapter<CraftType> arrayAdapter = new ArrayAdapter<CraftType>(
                getActivity(), android.R.layout.simple_spinner_item, CraftType.values()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setText(CraftType.values()[position].getName());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setText(CraftType.values()[position].getName());
                return view;
            }
        };

        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CraftType selectedType = (CraftType) parent.getItemAtPosition(position);
                viewModel.setSearchProductType(selectedType.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        productView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = recyclerView.getAdapter().getItemCount();

                if (totalItemCount - lastVisibleItemPosition <= 1) {
                    viewModel.setSearchOffset(totalItemCount);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ProductDao productDao = AppDatabase.getDatabase(getContext()).productDao();
        List<ProductWithShopInfo> productList = productDao.getProductsWithShopInfo(pageOfSize, 0);
        adapter.setData(productList);
        adapter.notifyDataSetChanged();
    }

    public List<ProductWithShopInfo> fetchProduct(int pageSize, int offset){
        ProductDao productDao = AppDatabase.getDatabase(getContext()).productDao();
        String searchProductName = viewModel.getSearchProductName().getValue() != null && viewModel.getSearchProductName().getValue().trim().length() > 0
                ? viewModel.getSearchProductName().getValue().trim() : null;
        Integer searchProductType = viewModel.getSearchProductType().getValue() != null ? viewModel.getSearchProductType().getValue() : 0;

        if(searchProductName != null && searchProductType > 0){
            return productDao.getProductsWithShopInfoByTypeAndName(pageOfSize, offset, searchProductType, "%"+viewModel.getSearchProductName().getValue()+"%");
        }
        else if(searchProductName != null){
            return productDao.getProductsWithShopInfoByName(pageOfSize, offset, "%"+viewModel.getSearchProductName().getValue()+"%");
        }
        else if(searchProductType > 0 ){
            return productDao.getProductsWithShopInfoByType(pageOfSize, offset, searchProductType);
        }
        else
            return productDao.getProductsWithShopInfo(pageOfSize, offset);
    }
}