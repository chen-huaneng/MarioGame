package engine.effects;

public class SquishEffect extends MarioEffect {
    /**
     * 根据坐标和是否可视化生成踩扁效果
     *
     * @param x       横坐标
     * @param y       纵坐标
     */
    public SquishEffect(float x, float y) {
        super(x, y, 0, 0, 0, 0, 40, 8);
    }
}
