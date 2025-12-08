package com.comp2042.controller;

import com.comp2042.events.MoveEvent;
import com.comp2042.models.ClearRow;
import com.comp2042.models.ViewData;

/**
 * Interface for listening to input events.
 */
public interface InputEventListener {

    ViewData onMoveEvent(MoveEvent event);

    ClearRow getClearRows()
    ;
    ViewData getViewData();

    void startGame();
    
    void stopGame();

    void createNewGame();
}
