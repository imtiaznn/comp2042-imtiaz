package com.comp2042.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import com.comp2042.models.ClearRow;

/** Utility class for matrix operations related to the Tetris game. */
public class MatrixOperations {

    /** Private constructor to prevent instantiation of this utility class. */
    private MatrixOperations(){

    }

    /** Checks if a brick intersects with the board matrix at a given position.
     *
     * @param matrix The game board matrix.
     * @param brick The brick matrix.
     * @param x The x-coordinate on the board where the brick is placed.
     * @param y The y-coordinate on the board where the brick is placed.
     * @return true if there is an intersection, false otherwise.
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Checks if the target position is out of bounds of the matrix.
     *
     * @param matrix The game board matrix.
     * @param targetX The x-coordinate to check.
     * @param targetY The y-coordinate to check.
     * @return true if out of bounds, false otherwise.
     */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        boolean returnValue = true;
        if (targetX >= 0 && targetY < matrix.length && targetX < matrix[targetY].length) {
            returnValue = false;
        }
        return returnValue;
    }

    /** Creates a deep copy of a 2D integer array.
     *
     * @param original The original 2D integer array.
     * @return A deep copy of the original array.
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /** Merges a brick into the game board matrix at a specified position.
     *
     * @param filledFields The game board matrix.
     * @param brick The brick matrix.
     * @param x The x-coordinate on the board where the brick is placed.
     * @param y The y-coordinate on the board where the brick is placed.
     * @return A new matrix with the brick merged into the board.
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    copy[targetY][targetX] = brick[j][i];
                }
            }
        }
        return copy;
    }

    /** Checks and removes full rows from the game board matrix.
     *
     * @param matrix The game board matrix.
     * @return A ClearRow object containing the number of cleared rows, the new matrix, and the score bonus.
     */    
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /** Creates a deep copy of a list of 2D integer arrays.
     *
     * @param list The original list of 2D integer arrays.
     * @return A deep copy of the original list.
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}
