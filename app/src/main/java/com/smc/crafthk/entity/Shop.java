package com.smc.crafthk.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shops")
public class Shop {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String userId;
    public String name;
    public Double longitude;
    public Double latitude;
    public String shopImagePath;
    public String shopPhoneNumber;
    public String shopDescription;


}