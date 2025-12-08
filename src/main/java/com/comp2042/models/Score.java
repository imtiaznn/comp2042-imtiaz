package com.comp2042.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/** Class representing the player's score in the Tetris game. */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    public IntegerProperty scoreProperty() {
        return score;
    }

    /** Add a value to the current score.
     *
     * @param i value to add
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /** Reset the score to zero. */
    public void reset() {
        score.setValue(0);
    }

    /** Set the score to a new value.
     *
     * @param newScore the new score value
     */
    public void setScore(int newScore) {
        score.setValue(newScore);
    }
}
