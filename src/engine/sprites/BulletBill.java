package engine.sprites;

import engine.effects.DeathEffect;
import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.EventType;
import engine.helper.SpriteType;

public class BulletBill extends MarioSprite {

    public BulletBill(boolean visuals, float x, float y, int dir) {
        super(x, y, SpriteType.BULLET_BILL);
        this.width = 4;
        this.height = 12;
        this.ya = -5;
        this.facing = dir;

        if (visuals) {
            this.graphics = new MarioImage(Assets.enemies, 40);
            this.graphics.originX = 8;
            this.graphics.originY = 31;
            this.graphics.width = 16;
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

    @Override
    public void collideCheck() {
        if (!this.alive) {
            return;
        }

        float xMarioD = world.mario.x - x;
        float yMarioD = world.mario.y - y;
        if (xMarioD > -16 && xMarioD < 16) {
            if (yMarioD > -height && yMarioD < world.mario.height) {
                if (world.mario.ya > 0 && yMarioD <= 0 && (!world.mario.onGround || !world.mario.wasOnGround)) {
                    world.mario.stomp(this);
                    if (this.graphics != null) {
                        this.world.addEffect(new DeathEffect(this.x, this.y - 7, this.graphics.flipX, 43, 0));
                    }
                    this.world.removeSprite(this);
                } else {
                    this.world.addEvent(EventType.HURT, this.type.getValue());
                    world.mario.getHurt();
                }
            }
        }
    }

    /**
     * 移动蘑菇的位置
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

    @Override
    public boolean shellCollideCheck(Shell shell) {
        if (!this.alive) {
            return false;
        }

        float xD = shell.x - x;
        float yD = shell.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < shell.height) {
                if (this.graphics != null) {
                    this.world.addEffect(new DeathEffect(this.x, this.y - 7, this.graphics.flipX, 43, -1));
                }
                this.world.addEvent(EventType.SHELL_KILL, this.type.getValue());
                this.world.removeSprite(this);
                return true;
            }
        }
        return false;
    }
}
