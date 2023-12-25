package engine.effects;

import java.awt.*;

public class CoinEffect extends MarioEffect {
    /**
     * 根据坐标生成金币
     *
     * @param x 横坐标
     * @param y 纵坐标
     */
    public CoinEffect(float x, float y) {
        super(x, y, 0, -8f, 0, 1, 0, 16);
    }

    /**
     * 更新金币的状态
     */
    @Override
    public void render(Graphics og, float cameraX, float cameraY) {
        this.graphics.index = this.startingIndex + this.life & 3;
        super.render(og, cameraX, cameraY);
    }
}
