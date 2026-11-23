package com.medlens.app.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicines")
public class Medicine {
    @PrimaryKey @NonNull public String genericName;
    @NonNull public String category;
    @NonNull public String brands;
    @NonNull public String uses;
    @NonNull public String mechanism;
    @NonNull public String sideEffects;
    @NonNull public String precautions;
    @NonNull public String contraindications;
    @NonNull public String interactions;
    @NonNull public String storage;
    @NonNull public String whenToSeeDoctor;
    @NonNull public String source;

    public Medicine(@NonNull String genericName, @NonNull String category, @NonNull String brands,
            @NonNull String uses, @NonNull String mechanism, @NonNull String sideEffects,
            @NonNull String precautions, @NonNull String contraindications, @NonNull String interactions,
            @NonNull String storage, @NonNull String whenToSeeDoctor, @NonNull String source) {
        this.genericName = genericName; this.category = category; this.brands = brands; this.uses = uses;
        this.mechanism = mechanism; this.sideEffects = sideEffects; this.precautions = precautions;
        this.contraindications = contraindications; this.interactions = interactions; this.storage = storage;
        this.whenToSeeDoctor = whenToSeeDoctor; this.source = source;
    }
}
