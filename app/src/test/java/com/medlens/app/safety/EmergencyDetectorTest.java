package com.medlens.app.safety;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmergencyDetectorTest {
    @Test public void detectsEmergencyPhrasesCaseInsensitively() {
        assertTrue(EmergencyDetector.isEmergency("I have severe chest pain"));
        assertTrue(EmergencyDetector.isEmergency("I can't breathe"));
        assertTrue(EmergencyDetector.isEmergency("My face is drooping and speech is slurred"));
    }
    @Test public void allowsNonEmergencyExample() {
        assertFalse(EmergencyDetector.isEmergency("I have a headache and mild fever"));
        assertFalse(EmergencyDetector.isEmergency(""));
    }
}
