package com.comp2042.logic.boards;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.BrickRotator;
import com.comp2042.logic.bricks.NextShapeInfo;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.models.ClearRow;
import com.comp2042.models.Score;
import com.comp2042.models.ViewData;
import com.comp2042.utils.MatrixOperations;

import java.awt.*;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private Brick heldBrick;
    private boolean hasHeldThisTurn = false;
    private int[][] currentGameMatrix;
    private Point brickOffset;
    private Point ghostBrickOffset;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(brickOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            brickOffset = p;
            updateGhost();
            return true;
        }
    }
    
    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(brickOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            brickOffset = p;
            updateGhost();
            return true;
        }
    }
    
    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(brickOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            brickOffset = p;
            updateGhost();
            return true;
        }
    }
    
    @Override
    public boolean dropBrick() {
        Point p = calculateGhostOffset(brickOffset);
        brickOffset = p;

        mergeBrickToBackground();

        ClearRow clearRow = clearRows();

        if (clearRow.getLinesRemoved() > 0) {
            score.add(clearRow.getScoreBonus());
        }

        createNewBrick();

        return false;
    }

    @Override
    public boolean updateGhost() {
        Point p = calculateGhostOffset(brickOffset);
        ghostBrickOffset = p;
        return true;
    }

    // Ghost position helper function
    private Point calculateGhostOffset(Point num) {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(num);

        while(true) {
            p.setLocation(brickOffset.getX(), p.getY() + 1);
            // p.translate(0, 1);
            boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
            if (conflict) {
                p.setLocation(p.getX(), p.getY() - 1);
                return p;
            }
        }
    }
    
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        int[][] rotatedShape = nextShape.getShape();

        int[][] kicks = {
            {0, 0},
            {-1, 0},
            {1, 0},
            {-2, 0},
            {2, 0},
            {0, -1}
        };

        for (int[] k : kicks) {
            int tryX = (int) brickOffset.getX() + k[0];
            int tryY = (int) brickOffset.getY() + k[1];
            boolean conflict = MatrixOperations.intersect(currentMatrix, rotatedShape, tryX, tryY);
            if (!conflict) {
                // apply rotation and the successful kick offset
                brickRotator.setCurrentShape(nextShape.getPosition());
                brickOffset = new Point(tryX, tryY);
                updateGhost();
                return true;
            }
        }

        return false;
    }

    @Override
    public ViewData holdBrick() {
        if (hasHeldThisTurn) {
            return getViewData();
        }

        Brick current = brickRotator.getBrick();
        if (current == null) {
            return getViewData();
        }

        if (heldBrick == null) {
            heldBrick = current;
            Brick next = brickGenerator.getBrick();
            brickRotator.setBrick(next);
        } else {
            Brick swap = heldBrick;
            heldBrick = current;
            brickRotator.setBrick(swap);
        }

        brickOffset = new Point(4, 0);
        hasHeldThisTurn = true;
        return getViewData();
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        brickOffset = new Point(4, 0);
        ghostBrickOffset = calculateGhostOffset(brickOffset);
        hasHeldThisTurn = false;
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) brickOffset.getX(), (int) brickOffset.getY());
    }
    
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) brickOffset.getX(), (int) brickOffset.getY());
    }
    
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        // int[][] next = brickGenerator.getNextBrick() != null ? brickGenerator.getNextBrick().getShapeMatrix().get(0) : new int[][]{{0}};

        // Peek next 3 bricks
        final int AMOUNT_NEXT_BRICKS = 3;
        int[][][] next = new int[AMOUNT_NEXT_BRICKS][4][4];

        for (int i = 0; i < AMOUNT_NEXT_BRICKS; i++) {
            Brick b = brickGenerator.peekNextBricks(AMOUNT_NEXT_BRICKS)[i];
            if (b != null) {
                next[i] = b.getShapeMatrix().get(0);
            } else {
                next[i] = new int[][]{{0}};
            }
        }

        // Held brick
        int[][] held = heldBrick != null ? heldBrick.getShapeMatrix().get(0) : new int[][]{{0}};

        return new ViewData(brickRotator.getCurrentShape(), (int) brickOffset.getX(), (int) brickOffset.getY(), next, held, (int) ghostBrickOffset.getX(), (int) ghostBrickOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        heldBrick = null;
        hasHeldThisTurn = false;
        createNewBrick();
    }

}
