package com.smc.crafthk.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int shopId;
    public String productName;
    public BigDecimal productPrice;
    public String productImagePath;
    public int productType;
    public String productDescription;

}