package engine.graphics;

import engine.helper.Assets;

import java.awt.*;

public class MarioBackground extends MarioGraphics {
    private final Image image;
    private final Graphics2D g;
    private final int screenWidth;

    /**
     * 创建Mario游戏背景
     *
     * @param graphicsConfiguration 图片相关配置
     * @param screenWidth           屏幕的宽度
     * @param indices               索引
     */
    public MarioBackground(GraphicsConfiguration graphicsConfiguration, int screenWidth, int[][] indices) {
        super();
        // 设置宽度和长度
        this.width = indices[0].length * 16;
        this.height = indices.length * 16;
        // 设置屏幕的大小
        this.screenWidth = screenWidth;

        // 创建兼容的图像对象
        image = graphicsConfiguration.createCompatibleImage(width, height, Transparency.BITMASK);
        // 用于图像绘制操作
        g = (Graphics2D) image.getGraphics();
        // 设置图像的合成模式
        g.setComposite(AlphaComposite.Src);

        // 设置背景色为完全黑色的透明色
        g.setBackground(new Color(0, 0, 0, 0));
        // 清空背景
        g.clearRect(0, 0, this.width, this.height);
        for (int x = 0; x < indices[0].length; ++x) {
            for (int y = 0; y < indices.length; ++y) {

                // 计算砖块在数组中的索引位置
                int xTile = indices[y][x] % 8;
                int yTile = indices[y][x] / 8;

                // 绘制游戏砖块的图像，像素大小为16*16
                g.drawImage(Assets.level[xTile][yTile], x * 16, y * 16, 16, 16, null);
            }
        }
    }

    @Override
    public void render(Graphics og, int x, int y) {
        int xOff = x % this.width;
        for (int i = -1; i < this.screenWidth / this.width + 1; i++) {
            og.drawImage(image, -xOff + i * this.width, 0, null);
        }
    }
}
