package com.comp2042;

public interface InputEventListener {

    ViewData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    ViewData onHoldEvent(MoveEvent event);

    ViewData onMoveEvent(MoveEvent event);

    ClearRow getClearRows()
    ;
    ViewData getViewData();

    void startGame();
    
    void stopGame();

    void createNewGame();
}
