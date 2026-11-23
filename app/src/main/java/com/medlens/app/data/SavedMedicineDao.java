package com.medlens.app.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao public interface SavedMedicineDao {
    @Query("SELECT * FROM saved_medicines ORDER BY genericName COLLATE NOCASE") List<SavedMedicine> all();
    @Query("SELECT EXISTS(SELECT 1 FROM saved_medicines WHERE genericName = :name)") boolean isSaved(String name);
    @Insert(onConflict = OnConflictStrategy.REPLACE) void save(SavedMedicine item);
    @Query("DELETE FROM saved_medicines WHERE genericName = :name") void remove(String name);
}
