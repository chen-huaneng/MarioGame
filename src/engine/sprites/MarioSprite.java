package engine.sprites;

import java.awt.*;

import engine.core.MarioWorld;
import engine.helper.SpriteType;

public abstract class MarioSprite {
    public SpriteType type;

    public String initialCode;
    public float x, y, xa, ya;
    public int width, height, facing;
    public boolean alive;
    public MarioWorld world;

    /**
     * 初始化精灵类型
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @param type 精灵类型
     */
    public MarioSprite(float x, float y, SpriteType type) {
        this.initialCode = "";
        // 坐标位置
        this.x = x;
        this.y = y;
        this.xa = 0;
        this.ya = 0;
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
    }

    public MarioSprite clone() {
        return null;
    }

    public void added() {

    }

    public void removed() {

    }

    public void render(Graphics og) {

    }

    public void update() {

    }

    public void collideCheck() {
    }

    public void bumpCheck(int xTile, int yTile) {
    }

    public boolean shellCollideCheck(Shell shell) {
        return false;
    }

    public boolean fireballCollideCheck(Fireball fireball) {
        return false;
    }
}
