package engine.helper;

// 游戏中发生的事件类型
public enum EventType {
    STOMP_KILL(2),
    FIRE_KILL(3),
    SHELL_KILL(4),
    FALL_KILL(5),
    JUMP(6),
    LAND(7),
    COLLECT(8),
    HURT(9),
    KICK(10),
    LOSE(11),
    WIN(12);

    private final int value;

    EventType(int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
