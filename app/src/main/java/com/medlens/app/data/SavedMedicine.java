package com.medlens.app.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_medicines")
public class SavedMedicine {
    @PrimaryKey @NonNull public String genericName;
    public SavedMedicine(@NonNull String genericName) { this.genericName = genericName; }
}
