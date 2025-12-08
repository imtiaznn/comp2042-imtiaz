package com.comp2042.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.comp2042.models.ClearRow;
import org.junit.jupiter.api.Test;

public class MatrixOperationsTest {

    @Test
    public void copyProducesDeepCopy() {
        int[][] original = new int[][]{{1,2},{3,4}};
        int[][] copy = MatrixOperations.copy(original);
        // modify original
        original[0][0] = 99;
        assertEquals(1, copy[0][0], "Copy should not reflect changes to the original array");
    }

    @Test
    public void mergePlacesBrickCorrectly() {
        int[][] board = new int[4][4];
        int[][] brick = new int[][]{{1,0},{1,1}}; // 2x2-ish data (note indexing used in merge)
        int[][] merged = MatrixOperations.merge(board, brick, 1, 1);
        assertEquals(1, merged[1][1]);
        assertEquals(1, merged[2][1]);
        assertEquals(1, merged[2][2]);
    }

    @Test
    public void intersectDetectsOverlap() {
        int[][] board = new int[4][4];
        board[2][2] = 1; // occupied cell at (x=2,y=2)

        // Note: MatrixOperations indexes bricks as [row][col] but some code uses [j][i],
        // so use a brick with a 1 at [0][0] to guarantee overlap at the target position.
        int[][] brick = new int[][]{{1,0},{0,0}}; // when placed at x=2,y=2 should intersect
        boolean it = MatrixOperations.intersect(board, brick, 2, 2);
        assertTrue(it, "intersect should detect overlapping block");

        boolean none = MatrixOperations.intersect(board, brick, 0, 0);
        assertFalse(none, "intersect should be false when no overlap");
    }

    @Test
    public void checkRemovingClearsFullRows() {
        int[][] board = new int[4][4];
        // fill row 1 and row 3 completely
        for (int x = 0; x < 4; x++) board[1][x] = 1;
        for (int x = 0; x < 4; x++) board[3][x] = 2;

        ClearRow cr = MatrixOperations.checkRemoving(board);
        assertEquals(2, cr.getLinesRemoved(), "Should detect two full rows");
        assertNotNull(cr.getNewMatrix(), "New matrix should be returned");
        assertEquals(50 * 2 * 2, cr.getScoreBonus(), "Score bonus formula should match number of cleared rows squared * 50");
    }
}
