package engine.core;

import engine.helper.EventType;

public class MarioEvent {
    private final EventType eventType;
    private final int eventParam;
    private final float marioX;
    private final float marioY;
    private final int marioState;
    private final int time;

    /**
     * 记录Mario的事件
     *
     * @param eventType  事件类型
     * @param eventParam 事件的参数
     * @param x          Mario的横坐标
     * @param y          Mario的纵坐标
     * @param state      Mario的状态
     * @param time       游戏时间
     */
    public MarioEvent(EventType eventType, int eventParam, float x, float y, int state, int time) {
        this.eventType = eventType;
        this.eventParam = eventParam;
        this.marioX = x;
        this.marioY = y;
        this.marioState = state;
        this.time = time;
    }
}
