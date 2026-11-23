package com.medlens.app.safety;

import java.util.Locale;

public final class EmergencyDetector {
    private EmergencyDetector() {}
    private static final String[][] GROUPS = {
        {"chest pain", "chest hurts", "pain in my chest", "pressure in my chest"},
        {"can't breathe", "cannot breathe", "trouble breathing", "difficulty breathing", "shortness of breath", "breathless"},
        {"throat is closing", "swelling of my tongue", "swollen tongue", "severe allergic", "anaphylaxis"},
        {"fainted", "fainting", "unconscious", "not waking", "passed out"},
        {"face drooping", "one-sided weakness", "one side is weak", "slurred speech", "sudden weakness", "can't lift my arm", "sudden confusion", "sudden vision loss"},
        {"severe bleeding", "bleeding heavily", "won't stop bleeding", "cannot stop bleeding", "bleeding won't stop", "spurting blood"},
        {"worst headache", "sudden severe headache", "thunderclap headache", "sudden worst headache"}
    };
    public static boolean isEmergency(String text) {
        String s = text == null ? "" : text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
        for (String[] group : GROUPS) for (String term : group) if (s.contains(term)) return true;
        return false;
    }
}
