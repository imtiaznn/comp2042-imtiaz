package com.comp2042.logic.bricks;

/** Interface for generating Tetris bricks. */
public interface BrickGenerator {

    Brick getBrick();

    Brick[] peekNextBricks(int index);
}
