package engine.sprites;

import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.EventType;
import engine.helper.SpriteType;

public class LifeMushroom extends MarioSprite {
    private int life;

    private static final float GROUND_INERTIA = 0.89f;
    private static final float AIR_INERTIA = 0.89f;

    /**
     * 根据坐标和是否可视化生成蘑菇
     *
     * @param visuals 是否可视化
     * @param x       横坐标
     * @param y       纵坐标
     */
    public LifeMushroom(boolean visuals, float x, float y) {
        // 设置蘑菇的类型
        super(x, y, SpriteType.LIFE_MUSHROOM);
        this.width = 4;
        this.height = 12;
        this.facing = 1;
        this.life = 0;

        // 如果可视化则设置蘑菇的图像
        if (visuals) {
            this.graphics = new MarioImage(Assets.items, 3);
            this.graphics.width = 16;
            this.graphics.height = 16;
            this.graphics.originX = 8;
            this.graphics.originY = 15;
        }
    }

    /**
     * 判断蘑菇是否与Mario碰撞
     */
    @Override
    public void collideCheck() {
        // 如果蘑菇死亡，则不再碰撞检测
        if (!this.alive) {
            return;
        }

        float xMarioD = world.mario.x - x;
        float yMarioD = world.mario.y - y;

        // 如果Mario与蘑菇碰撞，则触发收集事件
        if (xMarioD > -16 && xMarioD < 16) {
            if (yMarioD > -height && yMarioD < world.mario.height) {
                // 触发收集事件
                world.addEvent(EventType.COLLECT, this.type.getValue());
                // Mario获得蘑菇
                world.removeSprite(this);
            }
        }
    }

    /**
     * 更新蘑菇的位置
     */
    @Override
    public void update() {
        // 如果蘑菇死亡，则不再更新
        if (!this.alive) {
            return;
        }

        if (life < 9) {
            y--;
            life++;
            return;
        }
        float sideWaysSpeed = 1.75f;
        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }

        // 设置移动，包含了方向和速度
        xa = facing * sideWaysSpeed;

        if (!move(xa, 0)) {
            facing = -facing;
        }
        onGround = false;
        move(0, ya);

        ya *= 0.85f;
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        if (!onGround) {
            ya += 2;
        }
    }
}
