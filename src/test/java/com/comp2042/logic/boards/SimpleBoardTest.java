package com.comp2042.logic.boards;

import static org.junit.jupiter.api.Assertions.*;

import com.comp2042.models.ClearRow;
import com.comp2042.models.Score;
import org.junit.jupiter.api.Test;

public class SimpleBoardTest {

    @Test
    public void newGameResetsBoardAndScore() {
        // Use the same board dimensions as the application to avoid indexing differences
        SimpleBoard b = new SimpleBoard(25, 10);
        b.getPlayerScore().add(100);
        assertTrue(b.getPlayerScore().scoreProperty().getValue() > 0);
        b.newGame();
        assertEquals(0, b.getPlayerScore().scoreProperty().getValue().intValue());
        int[][] mat = b.getBoardMatrix();
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                assertEquals(0, mat[i][j]);
            }
        }
    }

    @Test
    public void dropBrickReturnsClearRowObject() {
        // Use the same board dimensions as the application to avoid indexing differences
        SimpleBoard b = new SimpleBoard(25, 10);
        // Ensure a brick is created first (game normally does this before drop)
        b.createNewBrick();
        // call dropBrick and assert it returns a ClearRow instance (may be 0 lines)
        ClearRow cr = b.dropBrick();
        assertNotNull(cr);
        assertTrue(cr.getLinesRemoved() >= 0);
    }
}
