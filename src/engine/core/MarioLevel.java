package engine.core;

import engine.graphics.MarioImage;
import engine.graphics.MarioTilemap;
import engine.helper.Assets;
import engine.helper.SpriteType;
import engine.helper.TileFeature;

import java.awt.*;
import java.util.ArrayList;

public class MarioLevel {

    public int width = MarioGame.width;
    public int tileWidth = MarioGame.width / 16;
    public int height = MarioGame.height;
    public int tileHeight = MarioGame.height / 16;
    public int totalCoins = 0;
    public int marioTileX, marioTileY, exitTileX, exitTileY;
    // 存储地图贴图的数组
    private int[][] levelTiles;
    private SpriteType[][] spriteTemplates;
    private int[][] lastSpawnTime;
    private MarioTilemap graphics;
    private MarioImage flag;

    /**
     * 设置游戏的贴图
     *
     * @param level   游戏地图
     * @param visuals 是否可视化
     */
    public MarioLevel(String level, boolean visuals) {
        // 去除首尾的空格并判断是否为空
        if (level.trim().isEmpty()) {
            this.tileWidth = 0;
            this.width = 0;
            this.tileHeight = 0;
            this.height = 0;
            // 为空则初始化重要参数并返回
            return;
        }

        // 使用正则表达式分割字符，根据换行符和回车符分割
        String[] lines = level.split("\\r?\\n");

        // 设置贴图的宽度和长度
        this.tileWidth = lines[0].length();
        this.width = this.tileWidth * 16;
        this.tileHeight = lines.length;
        this.height = this.tileHeight * 16;

        // 用于存储贴图相关的数组
        this.levelTiles = new int[lines[0].length()][lines.length];
        this.spriteTemplates = new SpriteType[lines[0].length()][lines.length];
        this.lastSpawnTime = new int[lines[0].length()][lines.length];

        // 初始化贴图
        for (int y = 0; y < lines.length; ++y) {
            for (int x = 0; x < lines[y].length(); ++x) {
                this.levelTiles[x][y] = 0;
                this.spriteTemplates[x][y] = SpriteType.NONE;
                this.lastSpawnTime[x][y] = -40;
            }
        }

        // 判断各个字符所代表的贴图
        for (int y = 0; y < lines.length; ++y) {
            for (int x = 0; x < lines[y].length(); ++x) {
                char c = lines[y].charAt(x);
                switch (c) {
                    case 'y': // 尖刺
                        this.spriteTemplates[x][y] = SpriteType.SPIKY;
                        break;
                    case 'Y': // 带翅膀的尖刺
                        this.spriteTemplates[x][y] = SpriteType.SPIKY_WINGED;
                        break;
                    case 'E': // 贡巴
                    case 'g':
                        this.spriteTemplates[x][y] = SpriteType.GOOMBA;
                        break;
                    case 'G': // 带翅膀的贡巴
                        this.spriteTemplates[x][y] = SpriteType.GOOMBA_WINGED;
                        break;
                    case 'k': // 绿色库帕
                        this.spriteTemplates[x][y] = SpriteType.GREEN_KOOPA;
                        break;
                    case 'K': // 带翅膀的绿色库帕
                        this.spriteTemplates[x][y] = SpriteType.GREEN_KOOPA_WINGED;
                        break;
                    case 'r': // 红色库帕
                        this.spriteTemplates[x][y] = SpriteType.RED_KOOPA;
                        break;
                    case 'R': // 带翅膀的红色库帕
                        this.spriteTemplates[x][y] = SpriteType.RED_KOOPA_WINGED;
                        break;
                    case 'X': // 地板
                        this.levelTiles[x][y] = 1;
                        break;
                    case '%': // 雨林砖块
                        int tempIndex = 0;

                        // 判断是否为边界，如果为边界则改用其他砖块
                        if (x > 0 && lines[y].charAt(x - 1) == '%') {
                            tempIndex += 2;
                        }
                        if (x < this.levelTiles.length - 1 && lines[y].charAt(x + 1) == '%') {
                            tempIndex += 1;
                        }

                        this.levelTiles[x][y] = 43 + tempIndex;
                        break;
                    case '|': // 雨林背景
                        this.levelTiles[x][y] = 47;
                        break;
                    case '*': // 炮塔的贴图
                        tempIndex = 0;
                        // 判断炮塔的高度，根据高度调整贴图
                        if (y > 0 && lines[y - 1].charAt(x) == '*') {
                            tempIndex += 1;
                        }
                        if (y > 1 && lines[y - 2].charAt(x) == '*') {
                            tempIndex += 1;
                        }

                        this.levelTiles[x][y] = 3 + tempIndex;
                        break;
                    case 'D': // 使用过后的砖块
                        this.levelTiles[x][y] = 14;
                        break;
                    case 'S': // 普通砖块
                        this.levelTiles[x][y] = 6;
                        break;
                    case 'C': // 含有金币的方块
                        this.totalCoins += 1;
                        this.levelTiles[x][y] = 11;
                        break;
                    case 'U': // 含有蘑菇的方块
                        this.levelTiles[x][y] = 8;
                        break;
                    case 'o': // 金币
                        this.totalCoins += 1;
                        this.levelTiles[x][y] = 15;
                        break;
                    case 't': // 不含食人花的管道
                        tempIndex = 0;

                        // 判断是否是单个管道
                        boolean singlePipe = x < lines[y].length() - 1 && Character.toLowerCase(lines[y].charAt(x + 1)) != 't' &&
                                x > 0 && Character.toLowerCase(lines[y].charAt(x - 1)) != 't';

                        // 判断贴图的左右方向
                        if (x > 0 && (this.levelTiles[x - 1][y] == 18 || this.levelTiles[x - 1][y] == 20)) {
                            tempIndex += 1;
                        }

                        // 判断贴图的高度
                        if (y > 0 && Character.toLowerCase(lines[y - 1].charAt(x)) == 't') {
                            if (singlePipe) {
                                tempIndex += 1;
                            } else {
                                tempIndex += 2;
                            }
                        }

                        // 判断是否单个管道
                        if (singlePipe) {
                            this.levelTiles[x][y] = 52 + tempIndex;
                        } else {
                            this.levelTiles[x][y] = 18 + tempIndex;
                        }
                        break;
                    case 'T': // 含有食人花的管道
                        tempIndex = 0;

                        // 判断是否单个管道
                        singlePipe = x < lines[y].length() - 1 && Character.toLowerCase(lines[y].charAt(x + 1)) != 't' &&
                                x > 0 && Character.toLowerCase(lines[y].charAt(x - 1)) != 't';

                        // 判断贴图的左右方向
                        if (x > 0 && (this.levelTiles[x - 1][y] == 18 || this.levelTiles[x - 1][y] == 20)) {
                            tempIndex += 1;
                        }

                        // 判断贴图的高度
                        if (y > 0 && Character.toLowerCase(lines[y - 1].charAt(x)) == 't') {
                            if (singlePipe) {
                                tempIndex += 1;
                            } else {
                                tempIndex += 2;
                            }
                        }

                        // 判断是否单个管道
                        if (singlePipe) {
                            this.levelTiles[x][y] = 52 + tempIndex;
                        } else {
                            if (tempIndex == 0) {
                                this.spriteTemplates[x][y] = SpriteType.ENEMY_FLOWER;
                            }
                            this.levelTiles[x][y] = 18 + tempIndex;
                        }
                        break;
                }
            }
        }

        // 设置起点
        this.marioTileX = 0;
        this.marioTileY = findFirstFloor(lines, this.marioTileX);

        // 设置默认的终点
        this.exitTileX = lines[0].length() - 1;
        this.exitTileY = findFirstFloor(lines, this.exitTileX);

        // 设置终点的旗杆底部
        for (int y = this.exitTileY; y > Math.max(1, this.exitTileY - 11); --y) {
            this.levelTiles[this.exitTileX][y] = 40;
        }

        // 设置旗杆的顶部
        this.levelTiles[this.exitTileX][Math.max(1, this.exitTileY - 11)] = 39;

        // 判断是否可视化
        if (visuals) {
            // 设置贴图
            this.graphics = new MarioTilemap(Assets.level, this.levelTiles);
            // 设置旗杆的旗帜
            this.flag = new MarioImage(Assets.level, 41);
            // 设置旗帜的长度和宽度
            this.flag.width = 16;
            this.flag.height = 16;
        }
    }

    public boolean isBlocking(int xTile, int yTile, float xa, float ya) {
        int block = this.getBlock(xTile, yTile);
        ArrayList<TileFeature> features = TileFeature.getTileType(block);
        boolean blocking = features.contains(TileFeature.BLOCK_ALL);
        blocking |= (ya < 0) && features.contains(TileFeature.BLOCK_UPPER);
        blocking |= (ya > 0) && features.contains(TileFeature.BLOCK_LOWER);

        return blocking;
    }

    public int getBlock(int xTile, int yTile) {
        if (xTile < 0) {
            xTile = 0;
        }
        if (xTile > this.tileWidth - 1) {
            xTile = this.tileWidth - 1;
        }
        if (yTile < 0 || yTile > this.tileHeight - 1) {
            return 0;
        }
        return this.levelTiles[xTile][yTile];
    }

    public void setBlock(int xTile, int yTile, int index) {
        if (xTile < 0 || yTile < 0 || xTile > this.tileWidth - 1 || yTile > this.tileHeight - 1) {
            return;
        }
        this.levelTiles[xTile][yTile] = index;
    }

    public void setShiftIndex(int xTile, int yTile, int shift) {
        if (this.graphics == null || xTile < 0 || yTile < 0 || xTile > this.tileWidth - 1 || yTile > this.tileHeight - 1) {
            return;
        }
        this.graphics.moveShift[xTile][yTile] = shift;
    }

    /**
     * 获取指定位置的精灵贴图
     *
     * @param xTile 贴图的横坐标
     * @param yTile 贴图的纵坐标
     * @return 返回指定位置的精灵贴图
     */
    public SpriteType getSpriteType(int xTile, int yTile) {
        // 判断精灵的位置是否超出了贴图的范围
        if (xTile < 0 || yTile < 0 || xTile >= this.tileWidth || yTile >= this.tileHeight) {
            return SpriteType.NONE;
        }
        return this.spriteTemplates[xTile][yTile];
    }

    /**
     * 根据传入的位置返回上一次生成的时刻
     *
     * @param xTile 贴图的横坐标
     * @param yTile 贴图的纵坐标
     * @return 上一次生成的时刻
     */
    public int getLastSpawnTick(int xTile, int yTile) {
        // 判断给定位置是否超出边界
        if (xTile < 0 || yTile < 0 || xTile > this.tileWidth - 1 || yTile > this.tileHeight - 1) {
            return 0;
        }
        return this.lastSpawnTime[xTile][yTile];
    }

    public void setLastSpawnTick(int xTile, int yTile, int tick) {
        if (xTile < 0 || yTile < 0 || xTile > this.tileWidth - 1 || yTile > this.tileHeight - 1) {
            return;
        }
        this.lastSpawnTime[xTile][yTile] = tick;
    }

    /**
     * 返回指定位置的精灵贴图的字符串表示
     *
     * @param xTile 贴图的横坐标
     * @param yTile 贴图的纵坐标
     * @return 贴图的字符串表示
     */
    public String getSpriteCode(int xTile, int yTile) {
        return xTile + "_" + yTile + "_" + this.getSpriteType(xTile, yTile).getValue();
    }

    /**
     * 判断是否为固定的静态物体
     *
     * @param c 地图中的字符
     * @return 是否为静态物体
     */
    private boolean isSolid(char c) {
        return c == 'X' || c == '*' || c == 'C' || c == 'S' ||
                c == 'U' || c == 'D' || c == '%' || c == 't' || c == 'T';
    }

    /**
     * 找到指定横坐标的地板高度
     *
     * @param lines 以String形式存储的地图
     * @param x     横坐标的位置
     * @return 纵坐标的位置
     */
    private int findFirstFloor(String[] lines, int x) {
        boolean skipLines = true;
        for (int i = lines.length - 1; i >= 0; --i) {
            char c = lines[i].charAt(x);
            // 如果是静态物体则跳过
            if (isSolid(c)) {
                skipLines = false;
                continue;
            }
            if (!skipLines && !isSolid(c)) {
                return i;
            }
        }
        return -1;
    }

    public void render(Graphics og, int cameraX, int cameraY) {
        this.graphics.render(og, cameraX, cameraY);
        if (cameraX + MarioGame.width >= this.exitTileX * 16) {
            this.flag.render(og, this.exitTileX * 16 - 8 - cameraX, Math.max(1, this.exitTileY - 11) * 16 + 16 - cameraY);
        }
    }
}
