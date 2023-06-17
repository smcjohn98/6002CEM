package com.smc.crafthk.helper;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.smc.crafthk.dao.EventDao;
import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.dao.UserDao;
import com.smc.crafthk.entity.Event;
import com.smc.crafthk.entity.Product;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.entity.User;
import com.smc.crafthk.implementation.BigDecimalConverter;
import com.smc.crafthk.implementation.LocalDateTimeConverter;

@Database(entities = {Shop.class, Product.class, Event.class}, version = 1)
@TypeConverters({LocalDateTimeConverter.class, BigDecimalConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ShopDao shopDao();
    public abstract ProductDao productDao();
    public abstract EventDao eventDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "crafthk")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}