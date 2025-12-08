package com.comp2042.logic.boards;

import com.comp2042.models.ClearRow;
import com.comp2042.models.Score;
import com.comp2042.models.ViewData;

/**
 * Interface representing the game board.
 */
public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    com.comp2042.models.ClearRow dropBrick();

    boolean createNewBrick();

    boolean updateGhost();

    int[][] getBoardMatrix();

    ViewData getViewData();

    ViewData holdBrick();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getPlayerScore();

    void setPlayerScore(Score newScore);

    void newGame();
}
