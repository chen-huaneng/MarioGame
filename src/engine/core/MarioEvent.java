package engine.core;

import engine.helper.EventType;

public class MarioEvent {
    private EventType eventType;
    private int eventParam;
    private float marioX;
    private float marioY;
    private int marioState;
    private int time;

    /**
     * 记录Mario的事件
     *
     * @param eventType 事件类型
     * @param eventParam 事件的参数
     * @param x Mario的横坐标
     * @param y Mario的纵坐标
     * @param state Mario的状态
     * @param time 游戏时间
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
