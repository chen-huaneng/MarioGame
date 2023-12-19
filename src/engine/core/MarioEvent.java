package engine.core;

import engine.helper.EventType;

public class MarioEvent {
    private EventType eventType;
    private int eventParam;
    private float marioX;
    private float marioY;
    private int marioState;
    private int time;

    public MarioEvent(EventType eventType, int eventParam, float x, float y, int state, int time) {
        this.eventType = eventType;
        this.eventParam = eventParam;
        this.marioX = x;
        this.marioY = y;
        this.marioState = state;
        this.time = time;
    }

    @Override
    public boolean equals(Object obj) {
        MarioEvent otherEvent = (MarioEvent) obj;
        return this.eventType == otherEvent.eventType &&
                (this.eventParam == 0 || this.eventParam == otherEvent.eventParam);
    }
}
