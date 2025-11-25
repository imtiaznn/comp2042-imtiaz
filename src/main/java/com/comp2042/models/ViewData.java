package com.comp2042.models;

import com.comp2042.utils.MatrixOperations;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][][] nextBricksData;
    private final int[][] heldBrickData;
    private final int ghostXPosition;
    private final int ghostYPosition;

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][][] nextBricksData, int[][] heldBrickData, int ghostXPosition, int ghostYPosition) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBricksData = nextBricksData;
        this.heldBrickData = heldBrickData;
        
        this.ghostXPosition = ghostXPosition;
        this.ghostYPosition = ghostYPosition;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getGhostXPosition() {
        return ghostXPosition;
    }

    public int getGhostYPosition() {
        return ghostYPosition;
    }

    public int[][][] getNextBricksData() {
        return nextBricksData;
    }

    public int[][] getHeldBrickData() {
        return MatrixOperations.copy(heldBrickData);

    }
}
