package com.smc.crafthk.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class EventWithShopInfo {
    @Embedded
    public Event event;

    @Relation(parentColumn = "shopId", entityColumn = "id")
    public Shop shop;
}
