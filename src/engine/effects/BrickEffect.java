package engine.effects;

import java.awt.*;

public class BrickEffect extends MarioEffect {

    /**
     * 根据坐标生成砖块
     *
     * @param x  横坐标
     * @param y  纵坐标
     * @param xv 横向速度
     * @param yv 纵向速度
     */
    public BrickEffect(float x, float y, float xv, float yv) {
        super(x, y, xv, yv, 0, 3, 16, 10);
    }

    /**
     * 更新砖块的状态
     */
    @Override
    public void render(Graphics og, float cameraX, float cameraY) {
        // 每次更新砖块的图像
        this.graphics.index = this.startingIndex + this.life % 4;
        // 每次更新砖块的速度
        this.ya *= 0.95f;
        // 调用父类的render方法
        super.render(og, cameraX, cameraY);
    }

}
