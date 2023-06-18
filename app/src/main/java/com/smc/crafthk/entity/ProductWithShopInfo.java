package com.smc.crafthk.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ProductWithShopInfo {
    @Embedded
    public Product product;

    @Relation(parentColumn = "shopId", entityColumn = "id")
    public Shop shop;
}
