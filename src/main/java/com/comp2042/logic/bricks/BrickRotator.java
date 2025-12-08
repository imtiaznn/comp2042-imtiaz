package com.comp2042.logic.bricks;

/** Class responsible for rotating Tetris bricks. */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Get the next shape of the current brick after rotation.
     * @return NextShapeInfo containing the shape matrix and its position.
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Get the current shape matrix of the brick.
     * @return 2D array representing the current shape matrix.
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Set the current shape index of the brick.
     * @param currentShape Index of the current shape.
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Set the brick to be rotated.
     * @param brick The Brick object.
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    /**
     * Get the current brick.
     * @return The Brick object.
     */
    public Brick getBrick() {
        return brick;
    }

}
