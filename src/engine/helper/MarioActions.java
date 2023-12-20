package engine.helper;

public enum MarioActions {
    LEFT(0, "Left"),
    RIGHT(1, "Right"),
    DOWN(2, "Down"),
    SPEED(3, "Speed"),
    JUMP(4, "Jump");

    private final int value;
    private final String name;

    MarioActions(int newValue, String newName) {
        value = newValue;
        name = newName;
    }

    public int getValue() {
        return value;
    }

    public String getString() {
        return name;
    }

    /**
     * 获取游戏动作
     *
     * @return 返回数组的长度
     */
    public static int numberOfActions() {
        return MarioActions.values().length;
    }
}
