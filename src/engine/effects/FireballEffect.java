package engine.effects;

import java.awt.*;

public class FireballEffect extends MarioEffect {
    /**
     * 根据坐标生成火球
     *
     * @param x 横坐标
     * @param y 纵坐标
     */
    public FireballEffect(float x, float y) {
        super(x, y, 0, 0, 0, 0, 32, 8);
    }

    /**
     * 更新火球的位置
     */
    @Override
    public void render(Graphics og, float cameraX, float cameraY) {
        this.graphics.index = this.startingIndex + (8 - this.life);
        super.render(og, cameraX, cameraY);
    }
}
