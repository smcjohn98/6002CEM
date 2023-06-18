package com.smc.crafthk.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.smc.crafthk.entity.Event;
import com.smc.crafthk.entity.EventWithShopInfo;
import com.smc.crafthk.entity.ProductWithShopInfo;

import java.util.List;

@Dao
public interface EventDao {
    @Insert
    void insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("SELECT * FROM events")
    List<Event> getAllEvents();

    @Query("SELECT * FROM events where shopId = :shopId")
    List<Event> getEventByShopId(int shopId);

    @Query("SELECT * FROM events e, shops s where s.id = e.shopId LIMIT :pageSize OFFSET :offset")
    List<EventWithShopInfo> getEventsWithShopInfo(int pageSize, int offset);
}