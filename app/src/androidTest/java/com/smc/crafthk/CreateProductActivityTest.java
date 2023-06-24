package com.smc.crafthk;


import static com.google.common.truth.Truth.assertThat;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.entity.Product;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.ui.product.CreateProductActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CreateProductActivityTest {

    private AppDatabase appDatabase;
    private ProductDao productDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        productDao = appDatabase.productDao();
    }

    @After
    public void close() {
        appDatabase.close();
    }

    @Test
    public void getProduct(){
        Product product = new Product();
        product.productName = "test";
        product.shopId = 1;
        productDao.insert(product);
        Product p = productDao.getProduct(1);
        assertThat(p).isNotNull();
    }
}