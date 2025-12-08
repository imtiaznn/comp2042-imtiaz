package com.comp2042.models;

import com.comp2042.utils.MatrixOperations;

/** Immutable data class representing the view data of the game board. */
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

    /** Get a copy of the brick data matrix. */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /** Get the x position of the brick. */
    public int getxPosition() {
        return xPosition;
    }

    /** Get the y position of the brick. */
    public int getyPosition() {
        return yPosition;
    }

    /** Get the x position of the ghost brick. */
    public int getGhostXPosition() {
        return ghostXPosition;
    }

    /** Get the y position of the ghost brick. */
    public int getGhostYPosition() {
        return ghostYPosition;
    }

    /** Get a copy of the next bricks data. */
    public int[][][] getNextBricksData() {
        return nextBricksData;
    }

    /** Get a copy of the held brick data. */
    public int[][] getHeldBrickData() {
        return MatrixOperations.copy(heldBrickData);

    }
}
