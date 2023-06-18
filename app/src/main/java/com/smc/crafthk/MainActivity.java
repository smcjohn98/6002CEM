package com.smc.crafthk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest.permission;

import com.smc.crafthk.constraint.CraftType;
import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.databinding.ActivityMainBinding;
import com.smc.crafthk.entity.Product;
import com.smc.crafthk.entity.ProductWithShopInfo;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.implementation.ProductAdapter;
import com.smc.crafthk.implementation.ProductWithShopInfoAdapter;
import com.smc.crafthk.ui.profile.LoginActivity;
import com.smc.crafthk.viewmodel.HomeViewModel;
import com.smc.crafthk.viewmodel.UserProfileViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private BottomNavigationView bottomNavigationView;

    private HomeViewModel viewModel;
    private int pageOfSize = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        //ProductDao productDao = AppDatabase.getDatabase(this).productDao();

        RecyclerView productView = binding.listProduct;
        productView.setLayoutManager(new GridLayoutManager(this, 2));
        ProductWithShopInfoAdapter adapter = new ProductWithShopInfoAdapter(new ArrayList<>(), position->{});
        productView.setAdapter(adapter);

        viewModel.getSearchOffset().observe(this, offset->{
            List<ProductWithShopInfo> productList = fetchProduct(pageOfSize, offset);

            if(offset == 0){
                adapter.setData(productList);
                adapter.notifyDataSetChanged();
            }
            else if(offset != 0 && productList.size() > 0){
                adapter.appendItems(productList);
            }
        });
        viewModel.getSearchProductName().observe(this, productName->{
            viewModel.setSearchOffset(0);
        });
        viewModel.getSearchProductType().observe(this, productType->{
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
                this, android.R.layout.simple_spinner_item, CraftType.values()) {
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
                    /*List<ProductWithShopInfo> nextPageList = fetchProduct(pageOfSize, totalItemCount);
                    //productDao.getProductsWithShopInfo(pageOfSize, totalItemCount);
                    if(nextPageList.size() > 0)
                        adapter.appendItems(nextPageList);*/
                }
            }
        });



        bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public List<ProductWithShopInfo> fetchProduct(int pageSize, int offset){
        ProductDao productDao = AppDatabase.getDatabase(this).productDao();
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