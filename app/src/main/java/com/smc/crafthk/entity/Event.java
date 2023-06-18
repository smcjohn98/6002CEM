package com.smc.crafthk.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity(tableName = "events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int shopId;
    public String eventName;
    public BigDecimal eventPrice;
    public String eventImagePath;
    public LocalDateTime eventDateTime;
    public String eventDescription;

}