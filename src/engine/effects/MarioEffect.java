package engine.effects;

import engine.graphics.MarioImage;
import engine.helper.Assets;

import java.awt.*;

public abstract class MarioEffect {
    public float x, y, xv, yv, xa, ya;
    public int life, startingIndex;
    protected MarioImage graphics;

    /**
     * 根据坐标生成MarioEffect
     *
     * @param x          横坐标
     * @param y          纵坐标
     * @param xv         横向速度
     * @param yv         纵向速度
     * @param xa         横向加速度
     * @param ya         纵向加速度
     * @param startIndex 图像的索引
     * @param life       生命周期
     */
    public MarioEffect(float x, float y, float xv, float yv, float xa, float ya, int startIndex, int life) {
        this.x = x;
        this.y = y;
        this.xv = xv;
        this.yv = yv;
        this.xa = xa;
        this.ya = ya;
        this.life = life;

        this.graphics = new MarioImage(Assets.particles, startIndex);
        this.graphics.width = 16;
        this.graphics.height = 16;
        this.graphics.originX = 8;
        this.graphics.originY = 8;
        this.startingIndex = startIndex;
    }

    /**
     * 渲染MarioEffect
     * @param og 画笔
     * @param cameraX 摄像机的横坐标
     * @param cameraY 摄像机的纵坐标
     */
    public void render(Graphics og, float cameraX, float cameraY) {
        // 如果生命周期小于等于0，则不再渲染
        if (this.life <= 0) {
            return;
        }

        this.life -= 1;
        this.x += this.xv;
        this.y += this.yv;
        this.xv += this.xa;
        this.yv += this.ya;

        // 渲染MarioEffect
        graphics.render(og, (int) (this.x - cameraX), (int) (this.y - cameraY));
    }
}
