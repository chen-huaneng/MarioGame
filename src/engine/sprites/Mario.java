package engine.sprites;

import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.EventType;
import engine.helper.MarioActions;
import engine.helper.SpriteType;
import engine.helper.TileFeature;

public class Mario extends MarioSprite {
    // 是否大Mario和火球Mario
    public boolean isLarge, isFire;
    // 是否在地面上
    public boolean wasOnGround, isDucking, canShoot, mayJump;
    // 按键操作
    public boolean[] actions = null;
    // 跳跃时间
    public int jumpTime = 0;

    // 跳跃速度
    private float xJumpSpeed, yJumpSpeed = 0;
    // 无敌时间
    private int invulnerableTime = 0;

    // 动画帧速度
    private float marioFrameSpeed = 0;
    private boolean oldLarge, oldFire = false;

    // 跳跃的起始横坐标
    private float xJumpStart = -100;
    // 水平惯性
    private final float GROUND_INERTIA = 0.89f;
    // 空气阻力
    private final float AIR_INERTIA = 0.89f;
    // 无敌时间
    private final int POWERUP_TIME = 3;

    /**
     * @param visuals 是否可视化
     * @param x       Mario的初始横坐标
     * @param y       Mario的初始纵坐标
     */
    public Mario(boolean visuals, float x, float y) {
        super(x + 8, y + 15, SpriteType.MARIO);
        // 设置初始的状态
        this.isLarge = this.oldLarge = false;
        this.isFire = this.oldFire = false;
        // 设置高度和宽度
        this.width = 4;
        this.height = 24;
        // 显示图像
        if (visuals) {
            graphics = new MarioImage(Assets.smallMario, 0);
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
        boolean collide = false;
        // 判断向上移动是否会发生碰撞
        if (ya > 0) {
            if (isBlocking(x + xa - width, y + ya, xa, 0)) {
                collide = true;
            } else if (isBlocking(x + xa + width, y + ya, xa, 0)) {
                collide = true;
            } else if (isBlocking(x + xa - width, y + ya + 1, xa, ya)) {
                collide = true;
            } else if (isBlocking(x + xa + width, y + ya + 1, xa, ya)) {
                collide = true;
            }
        }
        if (ya < 0) {
            if (isBlocking(x + xa, y + ya - height, xa, ya)) {
                collide = true;
            } else if (isBlocking(x + xa - width, y + ya - height, xa, ya)) {
                collide = true;
            } else if (isBlocking(x + xa + width, y + ya - height, xa, ya)) {
                collide = true;
            }
        }
        if (xa > 0) {
            if (isBlocking(x + xa + width, y + ya - height, xa, ya)) {
                collide = true;
            }
            if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya)) {
                collide = true;
            }
            if (isBlocking(x + xa + width, y + ya, xa, ya)) {
                collide = true;
            }
        }
        if (xa < 0) {
            if (isBlocking(x + xa - width, y + ya - height, xa, ya)) {
                collide = true;
            }
            if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya)) {
                collide = true;
            }
            if (isBlocking(x + xa - width, y + ya, xa, ya)) {
                collide = true;
            }
        }
        // 如果发生碰撞则更新位置
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
                jumpTime = 0;
                this.ya = 0;
            }
            if (ya > 0) {
                y = (int) ((y - 1) / 16 + 1) * 16 - 1;
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
     * 判断Mario是否会碰撞到障碍物
     *
     * @param _x  Mario的横坐标
     * @param _y  Mario的纵坐标
     * @param xa  Mario的水平速度
     * @param ya  Mario的垂直速度
     * @return 判断是否会碰撞到障碍物
     */
    @Override
    public boolean isBlocking(float _x, float _y, float xa, float ya) {
        int xTile = (int) (_x / 16);
        int yTile = (int) (_y / 16);
        // 如果Mario的坐标和障碍物的坐标相同则不会碰撞
        if (xTile == (int) (this.x / 16) && yTile == (int) (this.y / 16)) {
            return false;
        }

        boolean blocking = world.level.isBlocking(xTile, yTile, xa, ya);
        int block = world.level.getBlock(xTile, yTile);

        if (TileFeature.getTileType(block).contains(TileFeature.PICKABLE)) {
            // 如果是可拾取的物品则触发拾取事件
            this.world.addEvent(EventType.COLLECT, block);
            // 增加金币数量
            this.collectCoin();
            // 设置该位置的障碍物为空
            world.level.setBlock(xTile, yTile, 0);
        }
        // 如果是障碍物则触发碰撞事件
        if (blocking && ya < 0) {
            // 如果是向上碰撞则触发顶部碰撞事件
            world.bump(xTile, yTile, isLarge);
        }
        return blocking;
    }

    /**
     * 根据Mario的状态、动作、速度等信息来更新Mario的图形显示状态
     */
    public void updateGraphics() {
        // 如果Mario死亡则不更新
        if (!this.alive) {
            return;
        }

        boolean currentLarge, currentFire;
        if (this.world.pauseTimer > 0) {
            currentLarge = (this.world.pauseTimer / 2) % 2 == 0 ? this.oldLarge : this.isLarge;
            currentFire = (this.world.pauseTimer / 2) % 2 == 0 ? this.oldFire : this.isFire;
        } else {
            currentLarge = this.isLarge;
            currentFire = this.isFire;
        }

        // 根据Mario的大小选择适合的贴图
        if (currentLarge) {
            // 大Mario
            this.graphics.sheet = Assets.mario;

            // 火球Mario
            if (currentFire) {
                this.graphics.sheet = Assets.fireMario;
            }

            // 根据Mario的大小设置原点和像素大小
            graphics.originX = 16;
            graphics.originY = 31;
            graphics.width = graphics.height = 32;
        } else {
            // 小Mario
            this.graphics.sheet = Assets.smallMario;

            // 根据Mario的大小设置原点和像素大小
            graphics.originX = 8;
            graphics.originY = 15;
            graphics.width = graphics.height = 16;
        }

        // 根据水平速度xa的绝对值来控制动画的速度
        this.marioFrameSpeed += Math.abs(xa) + 5;
        // 避免在静态下切换动画过快
        if (Math.abs(xa) < 0.5f) {
            this.marioFrameSpeed = 0;
        }

        // 根据无敌时间来设置对象是否可见
        // 根据奇偶的变化来实现闪烁的效果
        graphics.visible = ((invulnerableTime / 2) & 1) == 0;

        // 根据Mario的面向来确定是否翻转
        graphics.flipX = facing == -1;

        int frameIndex = 0;
        if (currentLarge) {
            // 计算动画帧索引
            frameIndex = ((int) (marioFrameSpeed / 20)) % 4;

            // 如果索引帧过高则设为1
            if (frameIndex == 3) {
                frameIndex = 1;
            }

            // 用于处理移动速度过快的情况
            if (Math.abs(xa) > 10) {
                frameIndex += 3;
            }

            // 如果不在地面上根据水平速度选择索引帧
            if (!onGround) {
                frameIndex = Math.abs(xa) > 10 ? 6 : 5;
            }
        } else {
            // 计算动画帧索引
            frameIndex = ((int) (marioFrameSpeed / 20)) % 2;

            // 用于处理移动速度过快的情况
            if (Math.abs(xa) > 10) {
                frameIndex += 2;
            }

            // 如果不在地面上根据水平速度选择索引帧
            if (!onGround) {
                frameIndex = Math.abs(xa) > 10 ? 5 : 4;
            }
        }

        // 处理在地面上向相反方向移动的操作
        if (onGround && ((facing == -1 && xa > 0) || (facing == 1 && xa < 0))) {
            if (xa > 1 || xa < -1) {
                frameIndex = currentLarge ? 8 : 7;
            }
        }

        // 处理大Mario蹲下的动画
        if (currentLarge && isDucking) {
            frameIndex = 13;
        }

        // 将最终计算得到的索引帧赋值
        graphics.index = frameIndex;
    }

    /**
     * 更新Mario的状态
     */
    @Override
    public void update() {
        // 如果Mario死亡则不更新
        if (!this.alive) {
            return;
        }

        // 如果无敌时间大于0则减少无敌时间
        if (invulnerableTime > 0) {
            invulnerableTime--;
        }
        this.wasOnGround = this.onGround;

        // 设置速度
        float sideWaysSpeed = actions[MarioActions.SPEED.getValue()] ? 1.2f : 0.6f;

        if (onGround) {
            // 如果在地面上则设置水平速度
            isDucking = actions[MarioActions.DOWN.getValue()] && isLarge;
        }

        // 根据Mario的状态设置高度
        if (isLarge) {
            height = isDucking ? 12 : 24;
        } else {
            height = 12;
        }

        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }

        if (actions[MarioActions.JUMP.getValue()] || (jumpTime < 0 && !onGround)) {
            // 如果按下跳跃键或者跳跃时间小于0且不在地面上则设置跳跃速度
            if (jumpTime < 0) {
                // 如果跳跃时间小于0则设置跳跃速度
                xa = xJumpSpeed;
                ya = -jumpTime * yJumpSpeed;
                jumpTime++;
            } else if (onGround && mayJump) {
                // 如果在地面上且可以跳跃则设置跳跃速度
                xJumpSpeed = 0;
                yJumpSpeed = -1.9f;
                jumpTime = 7;
                ya = jumpTime * yJumpSpeed;
                onGround = false;
                if (!(isBlocking(x, y - 4 - height, 0, -4) || isBlocking(x - width, y - 4 - height, 0, -4) || isBlocking(x + width, y - 4 - height, 0, -4))) {
                    // 如果不会碰撞到障碍物则触发跳跃事件
                    this.xJumpStart = this.x;
                    this.world.addEvent(EventType.JUMP, 0);
                }
            } else if (jumpTime > 0) {
                // 如果跳跃时间大于0则设置跳跃速度
                xa += xJumpSpeed;
                ya = jumpTime * yJumpSpeed;
                jumpTime--;
            }
        } else {
            jumpTime = 0;
        }

        // 如果按下向左或向右的键则设置水平速度
        if (actions[MarioActions.LEFT.getValue()] && !isDucking) {
            xa -= sideWaysSpeed;
            // 如果跳跃时间大于0则设置面向
            if (jumpTime >= 0) {
                facing = -1;
            }
        }

        // 如果按下向右或向右的键则设置水平速度
        if (actions[MarioActions.RIGHT.getValue()] && !isDucking) {
            xa += sideWaysSpeed;
            // 如果跳跃时间大于0则设置面向
            if (jumpTime >= 0) {
                facing = 1;
            }
        }

        // 如果按下向上的键则设置垂直速度
        if (actions[MarioActions.SPEED.getValue()] && canShoot && isFire && world.fireballsOnScreen < 2) {
            // 如果可以发射火球则发射火球
            world.addSprite(new Fireball(this.graphics != null, x + facing * 6, y - 20, facing));
        }

        canShoot = !actions[MarioActions.SPEED.getValue()];

        mayJump = onGround && !actions[MarioActions.JUMP.getValue()];

        if (Math.abs(xa) < 0.5f) {
            xa = 0;
        }

        // 如果不在地面上则设置垂直速度
        onGround = false;
        move(xa, 0);
        move(0, ya);
        // 如果在地面上则触发着陆事件
        if (!wasOnGround && onGround && this.xJumpStart >= 0) {
            this.world.addEvent(EventType.LAND, 0);
            this.xJumpStart = -100;
        }

        if (x < 0) {
            x = 0;
            xa = 0;
        }

        // 如果Mario的横坐标大于地图的宽度则触发胜利事件
        if (x > world.level.exitTileX * 16) {
            x = world.level.exitTileX * 16;
            xa = 0;
            this.world.win();
        }

        // 设置水平速度
        ya *= 0.85f;

        // 设置水平惯性
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        // 设置垂直速度
        if (!onGround) {
            ya += 3;
        }

        // 更新图像
        if (this.graphics != null) {
            this.updateGraphics();
        }
    }

    /**
     * 跳跃踩踏敌人
     * @param enemy 敌人对象
     */
    public void stomp(Enemy enemy) {
        // 如果Mario死亡则不更新
        if (!this.alive) {
            return;
        }
        // 设置跳跃速度
        float targetY = enemy.y - enemy.height / 2;
        move(0, targetY - y);

        // 设置跳跃速度
        xJumpSpeed = 0;
        yJumpSpeed = -1.9f;
        jumpTime = 8;
        ya = jumpTime * yJumpSpeed;
        onGround = false;
        invulnerableTime = 1;
    }

    /**
     * 跳跃踩踏敌人
     * @param shell 敌人对象
     */
    public void stomp(Shell shell) {
        // 如果Mario死亡则不更新
        if (!this.alive) {
            return;
        }
        // 设置跳跃速度
        float targetY = shell.y - shell.height / 2;
        move(0, targetY - y);

        xJumpSpeed = 0;
        yJumpSpeed = -1.9f;
        jumpTime = 8;
        ya = jumpTime * yJumpSpeed;
        onGround = false;
        invulnerableTime = 1;
    }

    /**
     * 被敌人攻击
     */
    public void getHurt() {
        // 如果Mario死亡则不更新
        if (invulnerableTime > 0 || !this.alive) {
            return;
        }

        // 如果Mario是大Mario则变成小Mario
        if (isLarge) {
            world.pauseTimer = 3 * POWERUP_TIME;
            this.oldLarge = this.isLarge;
            this.oldFire = this.isFire;
            // 设置Mario的状态
            if (isFire) {
                this.isFire = false;
            } else {
                this.isLarge = false;
            }
            // 设置无敌时间
            invulnerableTime = 32;
        } else {
            if (this.world != null) {
                this.world.lose();
            }
        }
    }

    /**
     * 获得火花
     */
    public void getFlower() {
        // 如果Mario死亡则不更新
        if (!this.alive) {
            return;
        }

        // 如果Mario不是火球Mario则变成火球Mario
        if (!isFire) {
            world.pauseTimer = 3 * POWERUP_TIME;
            this.oldFire = this.isFire;
            this.oldLarge = this.isLarge;
            this.isFire = true;
            this.isLarge = true;
        } else {
            // 如果Mario是火球Mario则收集金币
            this.collectCoin();
        }
    }

    /**
     * 获得蘑菇
     */
    public void getMushroom() {
        // 如果Mario死亡则不更新
        if (!this.alive) {
            return;
        }

        // 如果Mario不是大Mario则变成大Mario
        if (!isLarge) {
            world.pauseTimer = 3 * POWERUP_TIME;
            this.oldFire = this.isFire;
            this.oldLarge = this.isLarge;
            this.isLarge = true;
        } else {
            // 如果Mario是大Mario则收集金币
            this.collectCoin();
        }
    }

    /**
     * 踢乌龟壳
     * @param shell 敌人对象
     */
    public void kick(Shell shell) {
        // 如果Mario死亡则不更新
        if (!this.alive) {
            return;
        }

        invulnerableTime = 1;
    }

    /**
     * 跳跃踩踏敌人
     * @param bill 敌人对象
     */
    public void stomp(BulletBill bill) {
        // 如果Mario死亡则不更新
        if (!this.alive) {
            return;
        }

        float targetY = bill.y - bill.height / 2;
        move(0, targetY - y);

        // 设置跳跃速度
        xJumpSpeed = 0;
        yJumpSpeed = -1.9f;
        jumpTime = 8;
        ya = jumpTime * yJumpSpeed;
        onGround = false;
        invulnerableTime = 1;
    }

    /**
     * 收集金币
     */
    public void collectCoin() {
        // 如果Mario死亡则不更新
        if (!this.alive) {
            return;
        }

        // 增加金币数量
        this.world.coins++;
    }
}
