package engine.sprites;

import engine.core.MarioWorld;
import engine.graphics.MarioImage;
import engine.helper.SpriteType;

import java.awt.*;

public abstract class MarioSprite {
    // 表示精灵的类型
    public SpriteType type;

    public MarioImage graphics = null;
    public boolean onGround = false;

    public String initialCode;

    // MarioSprite的当前位置
    public float x, y, xa, ya;
    public int width, height, facing;
    public boolean alive;
    public MarioWorld world;

    /**
     * 初始化精灵类型
     *
     * @param x    横坐标
     * @param y    纵坐标
     * @param type 精灵类型
     */
    public MarioSprite(float x, float y, SpriteType type) {
        this.initialCode = "";
        // 坐标位置
        this.x = x;
        this.y = y;
        this.xa = 0;
        this.ya = 0;
        // 朝向
        this.facing = 1;
        // 存活状态
        this.alive = true;
        // 游戏地图
        this.world = null;
        // 像素点
        this.width = 16;
        this.height = 16;
        // 类型
        this.type = type;
        this.onGround = true;
    }

    public void render(Graphics og) {
        this.graphics.render(og, (int) (this.x - this.world.cameraX), (int) (this.y - this.world.cameraY));
    }

    /**
     * 判断给定坐标位置是否存在障碍物，以及是否会阻挡敌人的移动
     *
     * @param _x 横坐标
     * @param _y 纵坐标
     * @param xa 水平速度
     * @param ya 垂直速度
     * @return 返回是否会被阻挡
     */
    public boolean isBlocking(float _x, float _y, float xa, float ya) {
        // 将像素转换为坐标
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);

        // 检查给定坐标是否与敌人坐标相同，如果相同表示当前位置是敌人所在的位置，避免误判
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16)) {
            return false;
        }

        return world.level.isBlocking(x, y, xa, ya);

    }

    /**
     * 移动敌人对象的方法，考虑了碰撞的检测和处，并对移动的有效性进行判定
     *
     * @param xa 水平移动速度
     * @param ya 垂直移动速度
     * @return 判断是否更新位置
     */
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
        }

        // 判断向左移动是否会发生碰撞
        if (xa < 0) {
            // 判断左右移动是否有障碍物
            if (isBlocking(x + xa - width, y + ya - height, xa, ya) ||
                    isBlocking(x + xa - width, y + ya - height / 2, xa, ya) ||
                    isBlocking(x + xa - width, y + ya, xa, ya)) {
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

    /**
     * 更新敌人的位置
     */
    public void update() {

    }

    public void collideCheck() {
    }

    /**
     * 检查是否发生碰撞
     *
     * @param xTile 横坐标
     * @param yTile 纵坐标
     */
    public void bumpCheck(int xTile, int yTile) {
        // 如果敌人已经死亡，则不再检查碰撞
        if (!this.alive) {
            return;
        }

        // 如果敌人的坐标与砖块坐标相同，则发生碰撞
        if (x + width > xTile * 16 && x - width < xTile * 16 + 16 && yTile == (int) ((y - 1) / 16)) {
            facing = -world.mario.facing;
            ya = -10;
        }
    }

    /**
     * 检查是否与玩家发生碰撞
     * @param shell 乌龟壳
     * @return 是否发生碰撞
     */
    public boolean shellCollideCheck(Shell shell) {
        return false;
    }

    /**
     * 检查是否与玩家发生碰撞
     * @param fireball 火球
     * @return 是否发生碰撞
     */
    public boolean fireballCollideCheck(Fireball fireball) {
        return false;
    }
}
