package engine.helper;

import engine.sprites.Enemy;
import engine.sprites.FlowerEnemy;
import engine.sprites.MarioSprite;

public enum SpriteType {
    //Generic values
    // 空白
    NONE(0),
    // Mario
    MARIO(-31),
    // 火球
    FIREBALL(16),
    // 贡巴
    GOOMBA(2, 16),
    // 带翅膀的贡巴
    GOOMBA_WINGED(3, 16),
    // 红色库帕
    RED_KOOPA(4, 0),
    // 带翅膀的红色库帕
    RED_KOOPA_WINGED(5, 0),
    // 绿色库帕
    GREEN_KOOPA(6, 8),
    // 带翅膀的绿色库帕
    GREEN_KOOPA_WINGED(7, 8),
    // 尖刺
    SPIKY(8, 24),
    // 带翅膀的尖刺
    SPIKY_WINGED(9, 24),
    BULLET_BILL(10, 40),
    // 食人花
    ENEMY_FLOWER(11, 48),
    MUSHROOM(12),
    FIRE_FLOWER(13),
    SHELL(14),
    LIFE_MUSHROOM(15);

    private final int value;
    private int startIndex;

    SpriteType(int newValue) {
        value = newValue;
    }

    SpriteType(int newValue, int newIndex) {
        value = newValue;
        startIndex = newIndex;
    }

    /**
     * 获取精灵的值
     *
     * @return 返回精灵的值
     */
    public int getValue() {
        return value;
    }

    /**
     * 获取开始的索引
     *
     * @return 获取开始的索引
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * 根据坐标位置返回相应的精灵类型
     *
     * @param visuals 是否可视化
     * @param xTile   横坐标
     * @param yTile   纵坐标
     * @param dir     方向
     * @return 返回精灵
     */
    public MarioSprite spawnSprite(boolean visuals, int xTile, int yTile, int dir) {
        // 生成食人花
        if (this == SpriteType.ENEMY_FLOWER) {
            return new FlowerEnemy(visuals, xTile * 16 + 17, yTile * 16 + 18);
        }
        return new Enemy(visuals, xTile * 16 + 8, yTile * 16 + 15, dir, this);
    }
}
