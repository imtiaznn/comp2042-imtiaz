package com.comp2042.logic.bricks;

import com.comp2042.utils.MatrixOperations;

/** Class representing information about the next shape in the Tetris game. */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Get the shape matrix of the next shape.
     * @return A copy of the shape matrix.
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Get the position of the next shape.
     * @return The position as an integer.
     */
    public int getPosition() {
        return position;
    }
}
