package engine.sprites;

import java.awt.Graphics;

import engine.effects.DeathEffect;
import engine.effects.SquishEffect;
import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.EventType;
import engine.helper.SpriteType;

public class Enemy extends MarioSprite {
    private static final float GROUND_INERTIA = 0.89f;
    private static final float AIR_INERTIA = 0.89f;

    protected boolean onGround = false;
    protected boolean avoidCliffs = true;
    protected boolean winged = true;
    protected boolean noFireballDeath;

    protected float runTime;
    protected int wingTime = 0;
    protected MarioImage wingGraphics;
    protected MarioImage graphics;

    /**
     * 生成指定坐标和方向的敌人类型
     *
     * @param visuals 是否可视化
     * @param x 横坐标
     * @param y 纵坐标
     * @param dir 方向
     * @param type 类型
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

    /**
     * 覆盖原本的克隆方法
     * @return 克隆之后的精灵
     */
    @Override
    public MarioSprite clone() {
        Enemy e = new Enemy(false, this.x, this.y, this.facing, this.type);
        e.xa = this.xa;
        e.ya = this.ya;
        e.initialCode = this.initialCode;
        e.width = this.width;
        e.height = this.height;
        e.onGround = this.onGround;
        e.winged = this.winged;
        e.avoidCliffs = this.avoidCliffs;
        e.noFireballDeath = this.noFireballDeath;
        return e;
    }

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

    private void updateGraphics() {
        wingTime++;
        this.wingGraphics.index = 32 + wingTime / 4 % 2;

        this.graphics.flipX = this.facing == -1;
        runTime += (Math.abs(xa)) + 5;

        int runFrame = ((int) (runTime / 20)) % 2;

        if (!onGround) {
            runFrame = 1;
        }
        if (winged)
            runFrame = wingTime / 4 % 2;

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

        onGround = false;
        move(0, ya);

        ya *= winged ? 0.95f : 0.85f;
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        if (!onGround) {
            if (winged) {
                ya += 0.6f;
            } else {
                ya += 2;
            }
        } else if (winged) {
            ya = -10;
        }

        if (this.graphics != null) {
            this.updateGraphics();
        }
    }

    /**
     * 移动敌人对象的方法，考虑了碰撞的检测和处，并对移动的有效性进行判定
     *
     * @param xa
     * @param ya
     * @return
     */
    private boolean move(float xa, float ya) {
        // 当移动距离大于8的时候，进行迭代，每次移动8个单位，直到距离小于等于8
        while (xa > 8) {
            if (!move(8, 0)) {
                return false;
            }
            xa -= 8;
        }
        while (xa < -8) {
            if (!move(-8, 0))
                return false;
            xa += 8;
        }
        while (ya > 8) {
            if (!move(0, 8))
                return false;
            ya -= 8;
        }
        while (ya < -8) {
            if (!move(0, -8))
                return false;
            ya += 8;
        }

        // 用于标记是否发生碰撞
        boolean collide = false;
        if (ya > 0) {
            if (isBlocking(x + xa - width, y + ya, xa, 0))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya, xa, 0))
                collide = true;
            else if (isBlocking(x + xa - width, y + ya + 1, xa, ya))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya + 1, xa, ya))
                collide = true;
        }
        if (ya < 0) {
            if (isBlocking(x + xa, y + ya - height, xa, ya))
                collide = true;
            else if (collide || isBlocking(x + xa - width, y + ya - height, xa, ya))
                collide = true;
            else if (collide || isBlocking(x + xa + width, y + ya - height, xa, ya))
                collide = true;
        }
        if (xa > 0) {
            if (isBlocking(x + xa + width, y + ya - height, xa, ya))
                collide = true;
            if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya))
                collide = true;
            if (isBlocking(x + xa + width, y + ya, xa, ya))
                collide = true;

            if (avoidCliffs && onGround
                    && !world.level.isBlocking((int) ((x + xa + width) / 16), (int) ((y) / 16 + 1), xa, 1))
                collide = true;
        }
        if (xa < 0) {
            if (isBlocking(x + xa - width, y + ya - height, xa, ya))
                collide = true;
            if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya))
                collide = true;
            if (isBlocking(x + xa - width, y + ya, xa, ya))
                collide = true;

            if (avoidCliffs && onGround
                    && !world.level.isBlocking((int) ((x + xa - width) / 16), (int) ((y) / 16 + 1), xa, 1))
                collide = true;
        }

        if (collide) {
            if (xa < 0) {
                x = (int) ((x - width) / 16) * 16 + width;
                this.xa = 0;
            }
            if (xa > 0) {
                x = (int) ((x + width) / 16 + 1) * 16 - width - 1;
                this.xa = 0;
            }
            if (ya < 0) {
                y = (int) ((y - height) / 16) * 16 + height;
                this.ya = 0;
            }
            if (ya > 0) {
                y = (int) (y / 16 + 1) * 16 - 1;
                onGround = true;
            }
            return false;
        } else {
            x += xa;
            y += ya;
            return true;
        }
    }

    /**
     * 判断给定坐标位置是否存在障碍物，以及是否会阻挡敌人的移动
     *
     * @param _x
     * @param _y
     * @param xa
     * @param ya
     * @return
     */
    private boolean isBlocking(float _x, float _y, float xa, float ya) {
        // 将像素转换为坐标
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);

        // 检查给定坐标是否与敌人坐标相同，如果相同表示当前位置是敌人所在的位置，避免误判
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16)) {
            return false;
        }

        return world.level.isBlocking(x, y, xa, ya);
    }

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

    public boolean fireballCollideCheck(Fireball fireball) {
        if (!this.alive) {
            return false;
        }

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < fireball.height) {
                if (noFireballDeath)
                    return true;

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
