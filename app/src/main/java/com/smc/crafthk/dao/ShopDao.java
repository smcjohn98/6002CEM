package com.smc.crafthk.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.entity.User;

import java.util.List;

@Dao
public interface ShopDao {
    @Insert
    void insert(Shop shop);

    @Update
    void update(Shop shop);

    @Delete
    void delete(Shop shop);

    @Query("SELECT * FROM shops")
    List<Shop> getAllShops();

    @Query("SELECT * FROM shops where userId = :userId")
    List<Shop> getShopByUserId(String userId);

    @Query("SELECT * FROM shops where id = :id")
    Shop getShopById(int id);
}