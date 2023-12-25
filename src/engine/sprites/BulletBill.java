package engine.sprites;

import engine.effects.DeathEffect;
import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.EventType;
import engine.helper.SpriteType;

public class BulletBill extends MarioSprite {

    /**
     * 根据坐标和是否可视化生成炮弹
     *
     * @param visuals 是否可视化
     * @param x       横坐标
     * @param y       纵坐标
     * @param dir     方向
     */
    public BulletBill(boolean visuals, float x, float y, int dir) {
        super(x, y, SpriteType.BULLET_BILL);
        this.width = 4;
        this.height = 12;
        this.ya = -5;
        this.facing = dir;

        // 如果可视化则设置蘑菇的图像
        if (visuals) {
            this.graphics = new MarioImage(Assets.enemies, 40);
            this.graphics.originX = 8;
            this.graphics.originY = 31;
            this.graphics.width = 16;
        }
    }

    /**
     * 更新炮弹的位置
     */
    @Override
    public void update() {
        // 如果炮弹死亡，则不再更新
        if (!this.alive) {
            return;
        }

        //super.update();
        // 设置速度
        float sideWaysSpeed = 4f;
        xa = facing * sideWaysSpeed;
        // 设置移动，包含了方向和速度
        move(xa);
        // 根据方向设置图片的朝向
        if (this.graphics != null) {
            this.graphics.flipX = facing == -1;
        }
    }

    /**
     * 碰撞检测
     */
    @Override
    public void collideCheck() {
        // 如果炮弹死亡，则不再检测
        if (!this.alive) {
            return;
        }

        // 检测是否与玩家碰撞
        float xMarioD = world.mario.x - x;
        float yMarioD = world.mario.y - y;

        // 如果玩家在炮弹的范围内，则玩家受到伤害
        if (xMarioD > -16 && xMarioD < 16) {
            // 如果玩家在炮弹的高度范围内，则玩家受到伤害
            if (yMarioD > -height && yMarioD < world.mario.height) {
                // 如果玩家在炮弹的上方，则玩家踩到炮弹，炮弹死亡
                if (world.mario.ya > 0 && yMarioD <= 0 && (!world.mario.onGround || !world.mario.wasOnGround)) {
                    // 玩家踩到炮弹，炮弹死亡
                    world.mario.stomp(this);
                    // 炮弹死亡
                    if (this.graphics != null) {
                        // 添加死亡效果
                        this.world.addEffect(new DeathEffect(this.x, this.y - 7, this.graphics.flipX, 43, 0));
                    }
                    // 添加事件
                    this.world.removeSprite(this);
                } else {
                    // 玩家受到伤害
                    this.world.addEvent(EventType.HURT, this.type.getValue());
                    world.mario.getHurt();
                }
            }
        }
    }

    /**
     * 移动炮弹的位置
     *
     * @param xa 水平移动速度
     */
    private void move(float xa) {
        x += xa;
    }

    @Override
    public boolean fireballCollideCheck(Fireball fireball) {
        if (!this.alive) {
            return false;
        }

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16) {
            return yD > -height && yD < fireball.height;
        }
        return false;
    }

    /**
     * 检测是否与炮弹碰撞
     *
     * @param shell 炮弹对象
     * @return 返回是否碰撞
     */
    @Override
    public boolean shellCollideCheck(Shell shell) {
        // 如果炮弹死亡，则不再检测
        if (!this.alive) {
            return false;
        }

        float xD = shell.x - x;
        float yD = shell.y - y;

        // 如果炮弹在炮弹的范围内，则炮弹死亡
        if (xD > -16 && xD < 16) {
            // 如果炮弹在炮弹的高度范围内，则炮弹死亡
            if (yD > -height && yD < shell.height) {
                // 炮弹死亡
                if (this.graphics != null) {
                    this.world.addEffect(new DeathEffect(this.x, this.y - 7, this.graphics.flipX, 43, -1));
                }
                // 添加事件
                this.world.addEvent(EventType.SHELL_KILL, this.type.getValue());
                this.world.removeSprite(this);
                return true;
            }
        }
        return false;
    }
}
