package com.comp2042.events;

/**
 * Represents a move event in the game.
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}
