package com.comp2042.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ScoreTest {

    @Test
    public void addAndResetWork() {
        Score s = new Score();
        s.add(10);
        assertEquals(10, s.scoreProperty().getValue().intValue());
        s.add(5);
        assertEquals(15, s.scoreProperty().getValue().intValue());
        s.reset();
        assertEquals(0, s.scoreProperty().getValue().intValue());
    }
}
