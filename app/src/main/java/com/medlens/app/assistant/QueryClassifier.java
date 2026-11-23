package com.medlens.app.assistant;

import java.util.Locale;

public final class QueryClassifier {
    private QueryClassifier() {}
    public static boolean isMedicineQuery(String text) {
        if (text == null) return false;
        String q = text.toLowerCase(Locale.ROOT);
        return q.contains("medicine") || q.contains("medication") || q.contains("tablet")
                || q.contains("capsule") || q.contains("drug") || q.contains("side effect")
                || q.contains("paracetamol") || q.contains("ibuprofen");
    }
}
