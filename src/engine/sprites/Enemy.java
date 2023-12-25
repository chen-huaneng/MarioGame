package engine.sprites;

import engine.effects.DeathEffect;
import engine.effects.SquishEffect;
import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.EventType;
import engine.helper.SpriteType;

import java.awt.*;

public class Enemy extends MarioSprite {
    private static final float GROUND_INERTIA = 0.89f;
    private static final float AIR_INERTIA = 0.89f;
    protected boolean avoidCliffs = true;
    protected boolean winged = true;
    protected boolean noFireballDeath;

    protected float runTime;
    protected int wingTime = 0;
    protected MarioImage wingGraphics;

    /**
     * 生成指定坐标和方向的敌人类型
     *
     * @param visuals 是否可视化
     * @param x       横坐标
     * @param y       纵坐标
     * @param dir     方向
     * @param type    类型
     */
    public Enemy(boolean visuals, float x, float y, int dir, SpriteType type) {
        super(x, y, type);

        // 设置敌人的高度和宽度
        this.width = 4;
        this.height = 24;

        // 如果不是乌龟类型则设置高度为12
        if (this.type != SpriteType.RED_KOOPA && this.type != SpriteType.GREEN_KOOPA
                && this.type != SpriteType.RED_KOOPA_WINGED && this.type != SpriteType.GREEN_KOOPA_WINGED) {
            this.height = 12;
        }

        // 判断是否带翅膀
        this.winged = this.type.getValue() % 2 == 1;

        // 根据敌人类型是否避开悬崖
        this.avoidCliffs = this.type == SpriteType.RED_KOOPA || this.type == SpriteType.RED_KOOPA_WINGED;

        // 判断敌人是否会因为火球而死亡
        this.noFireballDeath = this.type == SpriteType.SPIKY || this.type == SpriteType.SPIKY_WINGED;

        // 设置敌人的面朝方向
        this.facing = dir == 0 ? 1 : dir;

        // 判断是否可视化
        if (visuals) {
            // 设置初始的位置和宽度
            this.graphics = new MarioImage(Assets.enemies, this.type.getStartIndex());
            this.graphics.originX = 8;
            this.graphics.originY = 31;
            this.graphics.width = 16;

            // 设置初始的位置和宽度
            this.wingGraphics = new MarioImage(Assets.enemies, 32);
            this.wingGraphics.originX = 16;
            this.wingGraphics.originY = 31;
            this.wingGraphics.width = 16;
        }
    }

    @Override
    public void collideCheck() {
        if (!this.alive) {
            return;
        }

        float xMarioD = world.mario.x - x;
        float yMarioD = world.mario.y - y;
        if (xMarioD > -width * 2 - 4 && xMarioD < width * 2 + 4) {
            if (yMarioD > -height && yMarioD < world.mario.height) {
                if (type != SpriteType.SPIKY && type != SpriteType.SPIKY_WINGED && type != SpriteType.ENEMY_FLOWER &&
                        world.mario.ya > 0 && yMarioD <= 0 && (!world.mario.onGround || !world.mario.wasOnGround)) {
                    world.mario.stomp(this);
                    if (winged) {
                        winged = false;
                        ya = 0;
                    } else {
                        if (type == SpriteType.GREEN_KOOPA || type == SpriteType.GREEN_KOOPA_WINGED) {
                            this.world.addSprite(new Shell(this.graphics != null, x, y, 1, this.initialCode));
                        } else if (type == SpriteType.RED_KOOPA || type == SpriteType.RED_KOOPA_WINGED) {
                            this.world.addSprite(new Shell(this.graphics != null, x, y, 0, this.initialCode));
                        } else if (type == SpriteType.GOOMBA || type == SpriteType.GOOMBA_WINGED) {
                            if (this.graphics != null) {
                                this.world.addEffect(new SquishEffect(this.x, this.y - 7));
                            }
                        }
                        this.world.addEvent(EventType.STOMP_KILL, this.type.getValue());
                        this.world.removeSprite(this);
                    }
                } else {
                    this.world.addEvent(EventType.HURT, this.type.getValue());
                    world.mario.getHurt();
                }
            }
        }
    }

    /**
     * 更新敌人的图像
     */
    private void updateGraphics() {
        wingTime++;
        this.wingGraphics.index = 32 + wingTime / 4 % 2;

        this.graphics.flipX = this.facing == -1;
        runTime += (Math.abs(xa)) + 5;

        int runFrame = ((int) (runTime / 20)) % 2;

        if (!onGround) {
            runFrame = 1;
        }
        if (winged) {
            runFrame = wingTime / 4 % 2;
        }

        this.graphics.index = this.type.getStartIndex() + runFrame;
    }

    /**
     * 更新敌人的位置和状态
     */
    @Override
    public void update() {
        // 如果敌人死亡则不需要更新
        if (!this.alive) {
            return;
        }

        // 定义水平速度的初始值
        float sideWaysSpeed = 1.75f;

        //根据水平速度值来设定朝向
        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }

        // 更新矢量的速度，包含了朝向和水平速度
        xa = facing * sideWaysSpeed;

        if (!move(xa, 0)) {
            facing = -facing;
        }

        // 更新矢量的速度，包含了朝向和水平速度
        onGround = false;
        move(0, ya);

        // 判断是否有翅膀
        ya *= winged ? 0.95f : 0.85f;
        // 判断是否在地面上
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        // 判断是否在地面上
        if (!onGround) {
            if (winged) {
                ya += 0.6f;
            } else {
                ya += 2;
            }
        } else if (winged) {
            ya = -10;
        }

        // 判断是否可视化
        if (this.graphics != null) {
            this.updateGraphics();
        }
    }

    /**
     * 移动敌人对象的方法，考虑了碰撞的检测和处，并对移动的有效性进行判定
     *
     * @param xa 水平移动速度
     * @param ya 垂直移动速度
     * @return 判断是否更新位置
     */
    @Override
    public boolean move(float xa, float ya) {
        // 当移动距离大于8的时候，进行迭代，每次移动8个单位，直到距离小于等于8
        while (xa > 8) {
            if (!move(8, 0)) {
                return false;
            }
            xa -= 8;
        }
        while (xa < -8) {
            if (!move(-8, 0)) {
                return false;
            }
            xa += 8;
        }
        while (ya > 8) {
            if (!move(0, 8)) {
                return false;
            }
            ya -= 8;
        }
        while (ya < -8) {
            if (!move(0, -8)) {
                return false;
            }
            ya += 8;
        }

        // 用于标记是否发生碰撞
        boolean collide = ya > 0 &&
                (isBlocking(x + xa - width, y + ya, xa, 0) ||
                        isBlocking(x + xa + width, y + ya, xa, 0) ||
                        isBlocking(x + xa - width, y + ya + 1, xa, ya) ||
                        isBlocking(x + xa + width, y + ya + 1, xa, ya));

        // 判断向上移动是否会发生碰撞

        // 判断向下移动是否会发生碰撞
        if (ya < 0 &&
                (isBlocking(x + xa, y + ya - height, xa, ya) ||
                        isBlocking(x + xa - width, y + ya - height, xa, ya) ||
                        isBlocking(x + xa + width, y + ya - height, xa, ya))) {
            collide = true;
        }

        // 判断向右移动是否会发生碰撞
        if (xa > 0) {
            // 判断左右移动是否有障碍物
            if (isBlocking(x + xa + width, y + ya - height, xa, ya) ||
                    isBlocking(x + xa + width, y + ya - height / 2, xa, ya) ||
                    isBlocking(x + xa + width, y + ya, xa, ya)) {
                collide = true;
            }

            // 判断是否需要避免掉落悬崖
            if (avoidCliffs && onGround
                    && !world.level.isBlocking((int) ((x + xa + width) / 16), (int) ((y) / 16 + 1), xa, 1)) {
                collide = true;
            }
        }

        // 判断向左移动是否会发生碰撞
        if (xa < 0) {
            // 判断左右移动是否有障碍物
            if (isBlocking(x + xa - width, y + ya - height, xa, ya) ||
                    isBlocking(x + xa - width, y + ya - height / 2, xa, ya) ||
                    isBlocking(x + xa - width, y + ya, xa, ya)) {
                collide = true;
            }

            // 判断是否需要避免掉落悬崖
            if (avoidCliffs && onGround
                    && !world.level.isBlocking((int) ((x + xa - width) / 16), (int) ((y) / 16 + 1), xa, 1)) {
                collide = true;
            }
        }

        // 如果发生碰撞
        if (collide) {
            // 向左移动发生碰撞后调整位置到碰撞点的右侧，并把速度调为0
            if (xa < 0) {
                x = (int) ((x - width) / 16) * 16 + width;
                this.xa = 0;
            }
            // 向右移动发生碰撞后调整位置到碰撞点的左侧，并把速度调为0
            if (xa > 0) {
                x = (int) ((x + width) / 16 + 1) * 16 - width - 1;
                this.xa = 0;
            }
            // 向下移动发生碰撞后调整位置到碰撞点的上侧，并把速度调为0
            if (ya < 0) {
                y = (int) ((y - height) / 16) * 16 + height;
                this.ya = 0;
            }
            // 向上移动发生碰撞后调整位置到地面
            if (ya > 0) {
                y = (int) (y / 16 + 1) * 16 - 1;
                onGround = true;
            }
            return false;
        } else {
            // 如果没有发生碰撞则更新敌人的位置
            x += xa;
            y += ya;
            return true;
        }
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
                xa = shell.facing * 2;
                ya = -5;
                this.world.addEvent(EventType.SHELL_KILL, this.type.getValue());
                if (this.graphics != null) {
                    if (this.type == SpriteType.GREEN_KOOPA || this.type == SpriteType.GREEN_KOOPA_WINGED) {
                        this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 42, -5));
                    } else if (this.type == SpriteType.RED_KOOPA || this.type == SpriteType.RED_KOOPA_WINGED) {
                        this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 41, -5));
                    } else if (this.type == SpriteType.GOOMBA || this.type == SpriteType.GOOMBA_WINGED) {
                        this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 44, -5));
                    } else if (this.type == SpriteType.SPIKY || this.type == SpriteType.SPIKY_WINGED) {
                        this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 45, -5));
                    }
                }
                this.world.removeSprite(this);
                return true;
            }
        }
        return false;
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
                if (noFireballDeath) {
                    return true;
                }

                xa = fireball.facing * 2;
                ya = -5;
                this.world.addEvent(EventType.FIRE_KILL, this.type.getValue());
                if (this.graphics != null) {
                    if (this.type == SpriteType.GREEN_KOOPA || this.type == SpriteType.GREEN_KOOPA_WINGED) {
                        this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 42, -5));
                    } else if (this.type == SpriteType.RED_KOOPA || this.type == SpriteType.RED_KOOPA_WINGED) {
                        this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 41, -5));
                    } else if (this.type == SpriteType.GOOMBA || this.type == SpriteType.GOOMBA_WINGED) {
                        this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 44, -5));
                    }
                }
                this.world.removeSprite(this);
                return true;
            }
        }
        return false;
    }

    @Override
    public void bumpCheck(int xTile, int yTile) {
        if (!this.alive) {
            return;
        }

        if (x + width > xTile * 16 && x - width < xTile * 16 + 16 && yTile == (int) ((y - 1) / 16)) {
            xa = -world.mario.facing * 2;
            ya = -5;
            if (this.graphics != null) {
                if (this.type == SpriteType.GREEN_KOOPA || this.type == SpriteType.GREEN_KOOPA_WINGED) {
                    this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 42, -5));
                } else if (this.type == SpriteType.RED_KOOPA || this.type == SpriteType.RED_KOOPA_WINGED) {
                    this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 41, -5));
                } else if (this.type == SpriteType.GOOMBA || this.type == SpriteType.GOOMBA_WINGED) {
                    this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 44, -5));
                } else if (this.type == SpriteType.SPIKY || this.type == SpriteType.SPIKY_WINGED) {
                    this.world.addEffect(new DeathEffect(this.x, this.y, this.graphics.flipX, 45, -5));
                }
            }
            this.world.removeSprite(this);
        }
    }

    @Override
    public void render(Graphics og) {
        if (winged) {
            if (type != SpriteType.RED_KOOPA && type != SpriteType.GREEN_KOOPA && type != SpriteType.RED_KOOPA_WINGED
                    && type != SpriteType.GREEN_KOOPA_WINGED) {
                this.wingGraphics.flipX = false;
                this.wingGraphics.render(og, (int) (this.x - this.world.cameraX - 6), (int) (this.y - this.world.cameraY - 6));
                this.wingGraphics.flipX = true;
                this.wingGraphics.render(og, (int) (this.x - this.world.cameraX + 22), (int) (this.y - this.world.cameraY - 6));
            }
        }

        this.graphics.render(og, (int) (this.x - this.world.cameraX), (int) (this.y - this.world.cameraY));

        if (winged) {
            if (type == SpriteType.RED_KOOPA || type == SpriteType.GREEN_KOOPA || type == SpriteType.RED_KOOPA_WINGED
                    || type == SpriteType.GREEN_KOOPA_WINGED) {
                int shiftX = -1;
                if (this.graphics.flipX) {
                    shiftX = 17;
                }
                this.wingGraphics.flipX = this.graphics.flipX;
                this.wingGraphics.render(og, (int) (this.x - this.world.cameraX + shiftX), (int) (this.y - this.world.cameraY - 8));
            }
        }
    }
}
