package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

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

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 3) {
            shuffleBricks();
        }
        return nextBricks.poll();
    }

    public void shuffleBricks() {
        List<Brick> shuffled = new ArrayList<>(brickList);
        java.util.Collections.shuffle(shuffled);
        nextBricks.clear();
        nextBricks.addAll(shuffled);
    }

    // Peek next 3 bricks
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