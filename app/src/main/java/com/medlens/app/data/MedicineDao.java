package com.medlens.app.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao public interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY genericName COLLATE NOCASE") List<Medicine> all();
    @Query("SELECT * FROM medicines WHERE genericName LIKE '%' || :q || '%' OR brands LIKE '%' || :q || '%' OR category LIKE '%' || :q || '%' ORDER BY genericName COLLATE NOCASE") List<Medicine> search(String q);
    @Query("SELECT * FROM medicines WHERE genericName = :name LIMIT 1") Medicine get(String name);
    @Insert(onConflict = OnConflictStrategy.REPLACE) void insertAll(List<Medicine> list);
}
