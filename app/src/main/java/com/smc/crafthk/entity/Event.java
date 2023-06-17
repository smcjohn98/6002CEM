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
    public String name;
    public BigDecimal price;
    public String imagePath;
    public LocalDateTime dateTime;
    public String description;

}