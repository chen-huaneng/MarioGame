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

    @Override
    public void render(Graphics og, int x, int y) {
        if (!visible) {
            return;
        }

        int xPixel = x - originX;
        int yPixel = y - originY;
        Image image = this.sheet[index % sheet.length][index / sheet.length];

        og.drawImage(image, xPixel + (flipX ? width : 0), yPixel + (flipY ? height : 0), flipX ? -width : width, flipY ? -height : height, null);
    }

}
