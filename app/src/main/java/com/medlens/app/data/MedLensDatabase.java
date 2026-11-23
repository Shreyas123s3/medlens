package com.medlens.app.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {Medicine.class, SavedMedicine.class}, version = 1, exportSchema = false)
public abstract class MedLensDatabase extends RoomDatabase {
    private static volatile MedLensDatabase INSTANCE;
    public abstract MedicineDao medicineDao();
    public abstract SavedMedicineDao savedMedicineDao();
    public static MedLensDatabase get(Context context) {
        if (INSTANCE == null) synchronized (MedLensDatabase.class) {
            if (INSTANCE == null) INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MedLensDatabase.class, "medlens.db")
                    .addCallback(new Callback() {
                        @Override public void onCreate(androidx.sqlite.db.SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> seed(INSTANCE));
                        }
                    }).build();
        }
        return INSTANCE;
    }
    private static void seed(MedLensDatabase db) {
        if (db == null) return;
        String u = "Information unavailable";
        String src = "WHO Model List of Essential Medicines, 24th list (2025); name only. Clinical details require pharmacist or doctor review.";
        List<Medicine> list = new ArrayList<>();
        list.add(new Medicine("Ibuprofen", u, u,u,u,u,u,u,u,u,u,src));
        list.add(new Medicine("Paracetamol", u, u,u,u,u,u,u,u,u,u,src));
        db.medicineDao().insertAll(list);
    }
}
