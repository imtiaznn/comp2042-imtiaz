package com.comp2042.models;

import com.comp2042.utils.MatrixOperations;

/** Class representing the result of clearing rows in the Tetris game. */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /** Get the number of lines removed.
     *
     * @return number of lines removed
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /** Get the new matrix after rows have been cleared.
     *
     * @return new matrix
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /** Get the score bonus awarded for clearing rows.
     *
     * @return score bonus
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}
