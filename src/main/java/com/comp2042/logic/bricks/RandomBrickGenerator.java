package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Class that generates random bricks for the Tetris game. */
public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());

        // Initial shuffle
        shuffleBricks();
    }

    /**
     * Get a random brick from the generator.
     * @return A Brick object.
     */
    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 3) {
            shuffleBricks();
        }
        return nextBricks.poll();
    }
    
    /**
     * Shuffle the bricks to generate a new random sequence.
     */
    public void shuffleBricks() {
        List<Brick> shuffled = new ArrayList<>(brickList);
        java.util.Collections.shuffle(shuffled);
        nextBricks.clear();
        nextBricks.addAll(shuffled);
    }

    /**
     * Peek at the next bricks without removing them from the queue.
     * @param index The index of the brick to peek at.
     * @return An array of Brick objects up to the specified index.
     */
    @Override
    public Brick[] peekNextBricks(int index) {
        while (nextBricks.size() <= index) {
            shuffleBricks();
        }
        Brick[] bricks = new Brick[index + 1];
        int i = 0;
        for (Brick b : nextBricks) {
            if (i > index) break;
            bricks[i++] = b;
        }
        return bricks;
    }
}