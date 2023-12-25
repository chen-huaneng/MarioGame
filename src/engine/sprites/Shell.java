package engine.sprites;

import engine.effects.DeathEffect;
import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.EventType;
import engine.helper.SpriteType;

public class Shell extends MarioSprite {
    private static final float GROUND_INERTIA = 0.89f;
    private static final float AIR_INERTIA = 0.89f;

    private int shellType = 0;


    public Shell(boolean visuals, float x, float y, int shellType, String spriteCode) {
        super(x, y, SpriteType.SHELL);

        this.width = 4;
        this.height = 12;
        this.facing = 0;
        this.ya = -5;
        this.shellType = shellType;
        this.initialCode = spriteCode;

        if (visuals) {
            this.graphics = new MarioImage(Assets.enemies, shellType * 8 + 3);
            this.graphics.originX = 8;
            this.graphics.originY = 31;
            this.graphics.width = 16;
        }
    }

    /**
     * 更新乌龟的位置
     */
    @Override
    public void update() {
        // 如果乌龟死亡，则不再更新
        if (!this.alive) {
            return;
        }

        float sideWaysSpeed = 11f;

        // 判断朝向
        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }

        xa = facing * sideWaysSpeed;

        // 判断是否碰撞
        if (facing != 0) {
            world.checkShellCollide(this);
        }

        if (!move(xa, 0)) {
            facing = -facing;
        }
        onGround = false;
        move(0, ya);

        ya *= 0.85f;
        // 设置惯性
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        if (!onGround) {
            ya += 2;
        }

        // 设置图像的朝向
        if (this.graphics != null) {
            this.graphics.flipX = facing == -1;
        }
    }

    @Override
    public boolean fireballCollideCheck(Fireball fireball) {
        if (!this.alive) {
            return false;
        }

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < fireball.height) {
                if (facing != 0) {
                    return true;
                }

                xa = fireball.facing * 2;
                ya = -5;
                if (this.graphics != null) {
                    this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 41 + this.shellType, -5));
                }
                this.world.removeSprite(this);
                return true;
            }
        }
        return false;
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
                    if (facing != 0) {
                        xa = 0;
                        facing = 0;
                    } else {
                        facing = world.mario.facing;
                    }
                } else {
                    if (facing != 0) {
                        world.addEvent(EventType.HURT, this.type.getValue());
                        world.mario.getHurt();
                    } else {
                        world.addEvent(EventType.KICK, this.type.getValue());
                        world.mario.kick(this);
                        facing = world.mario.facing;
                    }
                }
            }
        }
    }

    @Override
    public boolean isBlocking(float _x, float _y, float xa, float ya) {
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16)) {
            return false;
        }

        boolean blocking = world.level.isBlocking(x, y, xa, ya);

        if (blocking && ya == 0 && xa != 0) {
            world.bump(x, y, true);
        }

        return blocking;
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
                this.world.addEvent(EventType.SHELL_KILL, this.type.getValue());
                if (this != shell) {
                    this.world.removeSprite(shell);
                }
                this.world.removeSprite(this);
                return true;
            }
        }
        return false;
    }
}
