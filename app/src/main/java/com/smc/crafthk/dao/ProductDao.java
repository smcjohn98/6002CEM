package com.smc.crafthk.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.smc.crafthk.entity.Product;
import com.smc.crafthk.entity.ProductWithShopInfo;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products where shopId = :shopId")
    List<Product> getProductsByShopId(int shopId);

    @Query("SELECT * FROM products p, shops s where s.id = p.shopId LIMIT :pageSize OFFSET :offset")
    List<ProductWithShopInfo> getProductsWithShopInfo(int pageSize, int offset);
}