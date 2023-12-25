package engine.effects;

public class DeathEffect extends MarioEffect {
    /**
     * 根据坐标和是否可视化生成死亡效果
     *
     * @param x       横坐标
     * @param y       纵坐标
     * @param flipX   是否水平翻转
     * @param yv      纵向速度
     * @param startIndex 起始图片索引
     */
    public DeathEffect(float x, float y, boolean flipX, int startIndex, float yv) {
        super(x, y, 0, yv, 0, 1f, startIndex, 30);
        this.graphics.flipX = flipX;
    }
}
