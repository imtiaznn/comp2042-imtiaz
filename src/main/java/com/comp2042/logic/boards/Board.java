package com.comp2042.logic.boards;

import com.comp2042.models.ClearRow;
import com.comp2042.models.Score;
import com.comp2042.models.ViewData;
import java.awt.*;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean dropBrick();

    boolean createNewBrick();

    boolean updateGhostPosition();

    int[][] getBoardMatrix();

    ViewData getViewData();

    ViewData holdBrick();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();
}
