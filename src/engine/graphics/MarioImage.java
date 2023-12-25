package engine.graphics;

import java.awt.*;

public class MarioImage extends MarioGraphics {
    public Image[][] sheet;
    public int index;

    /**
     * 设置图像的数组以及索引
     *
     * @param sheet 图片数组
     * @param index 索引
     */
    public MarioImage(Image[][] sheet, int index) {
        super();
        this.sheet = sheet;
        this.index = index;
    }

    /**
     * 渲染图像
     *
     * @param og 画笔
     * @param x  横坐标
     * @param y  纵坐标
     */
    @Override
    public void render(Graphics og, int x, int y) {
        // 如果不可视化，则不再渲染
        if (!visible) {
            return;
        }

        // 计算图像的横坐标和纵坐标
        int xPixel = x - originX;
        int yPixel = y - originY;
        Image image = this.sheet[index % sheet.length][index / sheet.length];

        // 渲染图像
        og.drawImage(image, xPixel + (flipX ? width : 0), yPixel + (flipY ? height : 0), flipX ? -width : width, flipY ? -height : height, null);
    }

}
