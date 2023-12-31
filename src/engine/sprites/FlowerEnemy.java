package engine.sprites;

import engine.helper.SpriteType;

public class FlowerEnemy extends Enemy {
    // 起始纵坐标
    private final float yStart;
    private int tick, waitTime;

    /**
     * 根据坐标和是否可视化生成食人花
     *
     * @param visuals 是否可视化
     * @param x       横坐标
     * @param y       纵坐标
     */
    public FlowerEnemy(boolean visuals, float x, float y) {
        super(visuals, x, y, 0, SpriteType.ENEMY_FLOWER);
        // 设置为不带翅膀和可以被火球击杀
        this.winged = false;
        this.noFireballDeath = false;

        // 设置宽度和高度
        this.width = 2;
        this.yStart = this.y;
        this.ya = -1;
        this.y -= 1;
        // 设置食人花的图像
        for (int i = 0; i < 4; i++) {
            this.update();
        }

        // 如果可视化则设置食人花的图像
        if (visuals) {
            this.graphics.originY = 24;
            this.tick = 0;
        }
    }

    /**
     * 更新食人花的状态
     *
     */
    @Override
    public void update() {
        // 如果没有存活则返回
        if (!this.alive) {
            return;
        }

        // 保持食人花的位置，防止食人花到处移动
        if (ya > 0) {
            if (y >= yStart) {
                y = yStart;
                int xd = (int) (Math.abs(world.mario.x - x));
                waitTime++;
                if (waitTime > 40 && xd > 24) {
                    waitTime = 0;
                    ya = -1;
                }
            }
        } else if (ya < 0) {
            if (yStart - y > 20) {
                y = yStart - 20;
                waitTime++;
                if (waitTime > 40) {
                    waitTime = 0;
                    ya = 1;
                }
            }
        }
        y += ya;

        // 如果可视化则更新图像
        if (this.graphics != null) {
            this.tick++;
            this.graphics.index = this.type.getStartIndex() + ((tick / 2) & 1) * 2 + ((tick / 6) & 1);
        }
    }
}
