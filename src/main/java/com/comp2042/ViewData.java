package com.comp2042;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][][] nextBricksData;
    private final int[][] heldBrickData;

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][][] nextBricksData, int[][] heldBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBricksData = nextBricksData;
        this.heldBrickData = heldBrickData;
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

    public int[][][] getNextBricksData() {
        return nextBricksData;
    }

    public int[][] getHeldBrickData() {
        return MatrixOperations.copy(heldBrickData);
    }
}
