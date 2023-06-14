package com.smc.crafthk.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int shopId;
    public String name;
    public BigDecimal price;
    public int type;
    public String description;

}