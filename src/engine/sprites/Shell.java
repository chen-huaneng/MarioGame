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


    /**
     * 根据坐标和是否可视化生成乌龟壳
     *
     * @param visuals   是否可视化
     * @param x         横坐标
     * @param y         纵坐标
     * @param shellType 乌龟壳类型
     */
    public Shell(boolean visuals, float x, float y, int shellType, String spriteCode) {
        super(x, y, SpriteType.SHELL);

        this.width = 4;
        this.height = 12;
        this.facing = 0;
        this.ya = -5;
        this.shellType = shellType;
        this.initialCode = spriteCode;

        // 如果可视化则设置乌龟壳的图像
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

    /**
     * 碰撞检测
     */
    @Override
    public void collideCheck() {
        // 如果乌龟壳死亡，则不再碰撞检测
        if (!this.alive) {
            return;
        }

        // 判断Mario是否与乌龟壳碰撞
        float xMarioD = world.mario.x - x;
        float yMarioD = world.mario.y - y;
        // 如果Mario与乌龟壳碰撞，则触发事件
        if (xMarioD > -16 && xMarioD < 16) {
            if (yMarioD > -height && yMarioD < world.mario.height) {
                // 如果Mario在乌龟壳上方，则Mario获得乌龟壳
                if (world.mario.ya > 0 && yMarioD <= 0 && (!world.mario.onGround || !world.mario.wasOnGround)) {
                    // 事件触发
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
                        // Mario受伤
                        world.mario.getHurt();
                    } else {
                        world.addEvent(EventType.KICK, this.type.getValue());
                        // Mario踢乌龟壳
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
