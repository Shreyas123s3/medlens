package com.medlens.app.assistant;

public final class OfflineAssistantService implements AiService {
    @Override public String answer(String message) {
        return "I can help organize what you’re experiencing, but I can’t diagnose or recommend a medicine. "
                + "How long have you had these symptoms? If they become severe or you feel unsafe, contact a clinician.";
    }
}
