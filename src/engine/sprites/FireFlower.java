package engine.sprites;

import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.EventType;
import engine.helper.SpriteType;

public class FireFlower extends MarioSprite {
    private int life;

    /**
     * 根据坐标和是否可视化生成火花
     *
     * @param visuals 是否可视化
     * @param x       横坐标
     * @param y       纵坐标
     */
    public FireFlower(boolean visuals, float x, float y) {
        // 设置火花的类型
        super(x, y, SpriteType.FIRE_FLOWER);
        this.width = 4;
        this.height = 12;
        this.facing = 1;
        this.life = 0;

        // 如果可视化则设置火花的图像
        if (visuals) {
            this.graphics = new MarioImage(Assets.items, 1);
            this.graphics.originX = 8;
            this.graphics.originY = 15;
            this.graphics.width = 16;
            this.graphics.height = 16;
        }
    }

    /**
     * 碰撞检测
     */
    @Override
    public void collideCheck() {
        // 如果火花死亡，则不再检测
        if (!this.alive) {
            return;
        }

        // 判断Mario是否与火花碰撞
        float xMarioD = world.mario.x - x;
        float yMarioD = world.mario.y - y;
        // 如果Mario与火花碰撞，则Mario获得火花
        if (xMarioD > -16 && xMarioD < 16) {
            if (yMarioD > -height && yMarioD < world.mario.height) {
                // 事件触发
                world.addEvent(EventType.COLLECT, this.type.getValue());
                // 获得火花
                world.mario.getFlower();
                world.removeSprite(this);
            }
        }
    }

    /**
     * 更新火花的位置
     */
    @Override
    public void update() {
        // 如果火花死亡，则不再更新
        if (!this.alive) {
            return;
        }

        life++;
        if (life < 9) {
            this.y--;
            return;
        }
        // 是否可视化
        if (this.graphics != null) {
            this.graphics.index = 1 + (this.life / 2) % 2;
        }
    }

}
