package com.comp2042.logic.bricks;

import java.util.List;

/** Interface representing a Tetris brick with multiple shapes. */
public interface Brick {

    List<int[][]> getShapeMatrix();
}
