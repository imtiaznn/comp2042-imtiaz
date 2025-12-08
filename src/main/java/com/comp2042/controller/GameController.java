package com.comp2042.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.MoveEvent;
import com.comp2042.logic.boards.Board;
import com.comp2042.logic.boards.SimpleBoard;
import com.comp2042.models.ClearRow;
import com.comp2042.models.ViewData;
import com.comp2042.view.GuiController;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javafx.scene.input.KeyCode;
import javafx.scene.Scene;

/** Controller class for managing the game logic and user input. */
public class GameController implements InputEventListener {

    private AnimationTimer animationTimer;
    private ClearRow lastClearRow;

    // Time attack mode
    private final boolean timeAttack;
    private long timeRemainingMs = 0L;
    private final long initialTimeMs;
    private long lastTimeUpdateNano = 0L;
    private final IntegerProperty timeRemainingSeconds = new SimpleIntegerProperty(0);

    // Input smoothing
    private final Set<KeyCode> activeKeys = new HashSet<>();
    private final Map<KeyCode, Long> keyPressedAt = new HashMap<>();
    private final Map<KeyCode, Long> lastActionAt = new HashMap<>();    

    private static final long DAS_MS = 150;   // Initial delay before repeat
    private static final long ARR_MS = 50;    // Repeat interval while held
    private static final long SOFT_DROP_ARR_MS = 50; // Down key repeat rate

    // Game logic
    private static final long GAME_TICK = 400 * 1000000; // Game tick in miliseconds
    private long fallCooldown = 0;

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c, Scene scene) {
        this(c, scene, 0);
    }

    /**
     * Create a GameController. If timeLimitSeconds > 0, the game will run in Timed mode
     * and will end when the timer reaches zero.
     */
    public GameController(GuiController c, Scene scene, int timeLimitSeconds) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);

        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());

        attachKeyInput(scene);

        // Timed mode setup
        this.timeAttack = timeLimitSeconds > 0;
        this.initialTimeMs = timeLimitSeconds * 1000L;
        if (this.timeAttack) {
            this.timeRemainingMs = initialTimeMs;
            this.timeRemainingSeconds.set((int) (this.timeRemainingMs / 1000L));
            viewGuiController.bindTimer(this.timeRemainingSeconds);
            this.lastTimeUpdateNano = System.nanoTime();
        }

        // Game loop insitialisation
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                processKeyInput();

                if (timeAttack) {
                    long deltaNs = now - lastTimeUpdateNano;
                    if (deltaNs > 0) {
                        timeRemainingMs -= deltaNs / 1000000L; // convert to ms
                        lastTimeUpdateNano = now;
                        int secs = (int) Math.max(0, (timeRemainingMs + 999) / 1000);
                        if (secs != timeRemainingSeconds.get()) {
                            timeRemainingSeconds.set(secs);
                        }
                        if (timeRemainingMs <= 0) {
                            // Time is up -> end the game
                            viewGuiController.gameOver(board.getPlayerScore().scoreProperty().getValue());
                            return;
                        }
                    }
                }

                update(now);
            }
        };

        animationTimer.start();


        viewGuiController.bindScore(board.getPlayerScore().scoreProperty());
    }

    /**
     * Game update tick.
     * @param now
     */
    public void update(long now) {
        boolean canMove = true;

        // Refresh
        if (now - fallCooldown >= GAME_TICK) {
            canMove = board.moveBrickDown();
            fallCooldown = now;
        }

        // Move brick down
        viewGuiController.refreshBrick(board.getViewData());

        if (!canMove) {
            // Clear and merge rows
            board.mergeBrickToBackground();

            // Clear rows
            ClearRow clearRow = board.clearRows();
            lastClearRow = clearRow;
            System.out.println("[GameController] clearRows returned lines=" + clearRow.getLinesRemoved() + " bonus=" + clearRow.getScoreBonus());

            // Update score
            if (clearRow.getLinesRemoved() > 0) {
                System.out.println("[GameController] Detected cleared rows: " + clearRow.getLinesRemoved() + ", calling showScoreNotification on viewGuiController id=" + System.identityHashCode(viewGuiController));
                board.getPlayerScore().add(clearRow.getScoreBonus());
                try {
                    viewGuiController.showScoreNotification(clearRow);
                } catch (Exception ex) {
                    System.out.println("[GameController] Exception while calling showScoreNotification: " + ex);
                    ex.printStackTrace();
                }
                viewGuiController.updateScore(board.getPlayerScore().scoreProperty().getValue());
            }

            // End game if cannot move and final block is intersecting
            if (board.createNewBrick()) {
                viewGuiController.gameOver(board.getPlayerScore().scoreProperty().getValue());
            }
            
            // Refresh view
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            
        }
    }

    /**
     * Handle move events from user input or other sources.
     * @param event the MoveEvent to process
     * @return updated ViewData after processing the move
       */
    public ViewData onMoveEvent(MoveEvent event) {
        switch (event.getEventType()) {
            case LEFT:
                board.moveBrickLeft();
                break;
            case RIGHT:
                board.moveBrickRight();
                break;
            case DOWN:
                board.moveBrickDown();
                break;
            case ROTATE:
                board.rotateLeftBrick();
                break;
            case HOLD:
                return board.holdBrick();
            case DROP:
                board.dropBrick();
                break;
            default:
                break;
        }

        return board.getViewData();
    }

    /**
     * Attach key input handlers to the given scene.
     * @param scene
     */
    public void attachKeyInput(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();

            if (!activeKeys.contains(code)) {
                activeKeys.add(code);

                long now = System.currentTimeMillis();
                keyPressedAt.put(code, now);
                lastActionAt.put(code, now);

                if (code == KeyCode.LEFT) onMoveEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
                else if (code == KeyCode.RIGHT) onMoveEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
                else if (code == KeyCode.UP) onMoveEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
                else if (code == KeyCode.C) onMoveEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
                else if (code == KeyCode.DOWN) { onMoveEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
                    // Reset last action time for soft drop to apply immediately
                    // lastActionAt.put(code, now);
                }
                else if (code == KeyCode.SPACE) {
                    onMoveEvent(new MoveEvent(EventType.DROP, EventSource.USER));
                    viewGuiController.refreshGameBackground(board.getBoardMatrix());
                    viewGuiController.refreshBrick(board.getViewData());
                };

                e.consume();                
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            activeKeys.remove(code);
            keyPressedAt.remove(code);
            lastActionAt.remove(code);
            e.consume();
        });
    }

    /**
     * Process continuous key input for smooth movement.
     */
    private void processKeyInput() {
        long now = System.currentTimeMillis();

        if (viewGuiController.isPause() == Boolean.TRUE || viewGuiController.isGameOver() == Boolean.TRUE) {
            return;
        }

        // Horizontal movement with DAS + ARR
        if (activeKeys.contains(KeyCode.LEFT)) {
            long pressed = keyPressedAt.getOrDefault(KeyCode.LEFT, now);
            long last = lastActionAt.getOrDefault(KeyCode.LEFT, 0L);
            if (now - pressed >= DAS_MS) {
                if (now - last >= ARR_MS) {
                    onMoveEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
                    lastActionAt.put(KeyCode.LEFT, now);
                }
            }
        }

        if (activeKeys.contains(KeyCode.RIGHT)) {
            long pressed = keyPressedAt.getOrDefault(KeyCode.RIGHT, now);
            long last = lastActionAt.getOrDefault(KeyCode.RIGHT, 0L);
            if (now - pressed >= DAS_MS) {
                if (now - last >= ARR_MS) {
                    onMoveEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
                    lastActionAt.put(KeyCode.RIGHT, now);
                }
            }
        }

        // Down key continuous (soft drop) with its own repeat rate
        if (activeKeys.contains(KeyCode.DOWN)) {
            // long pressed = keyPressedAt.getOrDefault(KeyCode.DOWN, now);
            long last = lastActionAt.getOrDefault(KeyCode.DOWN, 0L);
            if (now - last >= SOFT_DROP_ARR_MS) {
                onMoveEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
                lastActionAt.put(KeyCode.DOWN, now);
            }
        }
    }

    /**
     * Start the game animation timer.
     */
    @Override
    public void startGame() {
        if (animationTimer != null) animationTimer.start();
    }

    /**
     * Stop the game animation timer.
     */
    @Override
    public void stopGame() {
        if (animationTimer != null) animationTimer.stop();
    }

    /**
     * Create a new game, resetting the board and timer if applicable.
     */
    @Override
    public void createNewGame() {
        board.newGame();
        // Reset time attack timer if applicable
        if (timeAttack) {
            this.timeRemainingMs = this.initialTimeMs;
            this.timeRemainingSeconds.set((int) (this.timeRemainingMs / 1000L));
            this.lastTimeUpdateNano = System.nanoTime();
        }
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    // Getters
    public ViewData getViewData() {
        return board.getViewData();
    }

    public ClearRow getClearRows() {
        ClearRow tmp = lastClearRow;
        lastClearRow = null;
        return tmp;
    }
}

