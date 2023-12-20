package engine.sprites;

import engine.helper.SpriteType;

public class FlowerEnemy extends Enemy {
    private float yStart;
    private int tick, waitTime;

    /**
     * 根据坐标和是否可视化生成食人花
     *
     * @param visuals 是否可视化
     * @param x 横坐标
     * @param y 纵坐标
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
        for (int i = 0; i < 4; i++) {
            this.update();
        }

        if (visuals) {
            this.graphics.originY = 24;
            this.tick = 0;
        }
    }

    /**
     * 覆盖默认的克隆方法
     *
     * @return 克隆之后的食人花
     */
    @Override
    public MarioSprite clone() {
        FlowerEnemy sprite = new FlowerEnemy(false, this.x, this.y);
        sprite.xa = this.xa;
        sprite.ya = this.ya;
        sprite.initialCode = this.initialCode;
        sprite.width = this.width;
        sprite.height = this.height;
        sprite.onGround = this.onGround;
        sprite.winged = this.winged;
        sprite.avoidCliffs = this.avoidCliffs;
        sprite.noFireballDeath = this.noFireballDeath;
        sprite.yStart = yStart;
        sprite.waitTime = waitTime;
        return sprite;
    }

    /**
     *
     */
    @Override
    public void update() {
        // 如果没有存活则返回
        if (!this.alive) {
            return;
        }

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

        if (this.graphics != null) {
            this.tick++;
            this.graphics.index = this.type.getStartIndex() + ((tick / 2) & 1) * 2 + ((tick / 6) & 1);
        }
    }
}
