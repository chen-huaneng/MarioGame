package engine.graphics;

import engine.core.MarioGame;
import engine.helper.TileFeature;

import java.awt.*;
import java.util.ArrayList;

public class MarioTilemap extends MarioGraphics {
    public Image[][] sheet;
    public int[][] currentIndeces;
    public int[][] indexShift;
    public float[][] moveShift;
    public int animationIndex;

    /**
     * 初始化游戏地图的贴图
     *
     * @param sheet          贴图的图片
     * @param currentIndices 贴图的索引值
     */
    public MarioTilemap(Image[][] sheet, int[][] currentIndices) {
        this.sheet = sheet;
        this.currentIndeces = currentIndices;
        this.indexShift = new int[currentIndices.length][currentIndices[0].length];
        this.moveShift = new float[currentIndices.length][currentIndices[0].length];
        this.animationIndex = 0;
    }

    /**
     * 绘制游戏界面
     *
     * @param og 原始图形
     * @param x  x坐标
     * @param y  y坐标
     */
    @Override
    public void render(Graphics og, int x, int y) {
        this.animationIndex = (this.animationIndex + 1) % 5;

        int xMin = (x / 16) - 1;
        int yMin = (y / 16) - 1;
        int xMax = (x + MarioGame.width) / 16 + 1;
        int yMax = (y + MarioGame.height) / 16 + 1;

        // 绘制游戏地图
        for (int xTile = xMin; xTile <= xMax; xTile++) {
            for (int yTile = yMin; yTile <= yMax; yTile++) {
                // 如果xTile或yTile小于0，或者xTile或yTile大于等于currentIndeces的长度或宽度，则跳过
                if (xTile < 0 || yTile < 0 || xTile >= currentIndeces.length || yTile >= currentIndeces[0].length) {
                    continue;
                }
                // 如果moveShift[xTile][yTile]大于0，则减1
                if (this.moveShift[xTile][yTile] > 0) {
                    this.moveShift[xTile][yTile] -= 1;
                    if (this.moveShift[xTile][yTile] < 0) {
                        this.moveShift[xTile][yTile] = 0;
                    }
                }
                // 获取砖块的特征
                ArrayList<TileFeature> features = TileFeature.getTileType(this.currentIndeces[xTile][yTile]);
                if (features.contains(TileFeature.ANIMATED)) {
                    // 如果砖块是动画的，则将indexShift[xTile][yTile]的值加1
                    if (this.animationIndex == 0) {
                        this.indexShift[xTile][yTile] = (this.indexShift[xTile][yTile] + 1) % 3;
                    }
                } else {
                    this.indexShift[xTile][yTile] = 0;
                }
                // 获取砖块的索引值
                int index = currentIndeces[xTile][yTile] + indexShift[xTile][yTile];
                int move = (int) moveShift[xTile][yTile];
                // 绘制砖块
                Image img = sheet[index % 8][index / 8];
                og.drawImage(img, xTile * 16 - x, yTile * 16 - y - move, null);
            }
        }
    }
}
