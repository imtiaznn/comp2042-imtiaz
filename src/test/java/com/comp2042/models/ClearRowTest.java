package com.comp2042.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ClearRowTest {

    @Test
    public void clearRowAccessorMethods() {
        int[][] matrix = new int[2][2];
        ClearRow cr = new ClearRow(1, matrix, 100);
        assertEquals(1, cr.getLinesRemoved());
        assertNotNull(cr.getNewMatrix());
        assertEquals(100, cr.getScoreBonus());
    }
}
