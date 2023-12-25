package engine.sprites;

import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.SpriteType;

public class Fireball extends MarioSprite {

    private static final float GROUND_INERTIA = 0.89f;
    private static final float AIR_INERTIA = 0.89f;

    private int anim = 0;

    /**
     * 根据坐标和是否可视化生成火球
     *
     * @param visuals 是否可视化
     * @param x       横坐标
     * @param y       纵坐标
     * @param facing  朝向
     */
    public Fireball(boolean visuals, float x, float y, int facing) {
        // 调用父类的构造函数
        super(x, y, SpriteType.FIREBALL);
        this.facing = facing;
        this.ya = 4;
        this.width = 4;
        this.height = 8;

        // 如果可视化则设置火球的图像
        if (visuals) {
            this.graphics = new MarioImage(Assets.particles, 24);
            this.graphics.originX = 8;
            this.graphics.originY = 8;
            this.graphics.width = 16;
            this.graphics.height = 16;
        }
    }

    /**
     * 更新火球的位置
     */
    @Override
    public void update() {
        // 如果火球死亡，则不再更新
        if (!this.alive) {
            return;
        }

        // 判断朝向
        if (facing != 0) {
            anim++;
        }

        // 设置速度
        float sideWaysSpeed = 8f;
        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }
        // 设置移动，包含了方向和速度
        xa = facing * sideWaysSpeed;

        world.checkFireballCollide(this);

        if (!move(xa, 0)) {
            this.world.removeSprite(this);
            return;
        }

        onGround = false;
        move(0, ya);
        if (onGround) {
            ya = -10;
        }

        ya *= 0.95f;
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        if (!onGround) {
            ya += 1.5F;
        }

        // 根据方向设置图片的朝向
        if (this.graphics != null) {
            this.graphics.flipX = facing == -1;
            this.graphics.index = 24 + anim % 4;
        }
    }

}
