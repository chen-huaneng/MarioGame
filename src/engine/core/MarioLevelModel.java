package engine.core;

public class MarioLevelModel {
    //start and end of the level
    // 空白
    public static final char EMPTY = '-';

    //game tiles symbols
    // 地面方块
    public static final char GROUND = 'X';
    // 普通砖块
    public static final char NORMAL_BRICK = 'S';
    // 含有金币的方块
    public static final char COIN_BRICK = 'C';
    // 含有蘑菇的方块
    public static final char MUSHROOM_BRICK = 'U';
    // 金币
    public static final char COIN = 'o';
    // 空管道
    public static final char PIPE = 't';
    // 带有食人花的管道
    public static final char PIPE_FLOWER = 'T';
    // 子弹，其中顶部的'*'将会是子弹头
    public static final char BULLET_BILL = '*';
    // 跳过平台的背景
    public static final char PLATFORM_BACKGROUND = '|';
    // 跳过平台
    public static final char PLATFORM = '%';

    //enemies that can be in the level
    // 蘑菇怪
    public static final char GOOMBA = 'g';
    // 有翅膀的蘑菇怪
    public static final char GOOMBA_WINGED = 'G';
    // 红色库巴
    public static final char RED_KOOPA = 'r';
    // 带有翅膀的红色库巴
    public static final char RED_KOOPA_WINGED = 'R';
    // 绿色库巴
    public static final char GREEN_KOOPA = 'k';
    // 带有翅膀的绿色库巴
    public static final char GREEN_KOOPA_WINGED = 'K';
    // 尖刺
    public static final char SPIKY = 'y';
    // 带有翅膀的尖刺
    public static final char SPIKY_WINGED = 'Y';

    /**
     * 获取正确代表敌人的相应字符
     *
     * @param enemy  代表敌人的字符
     * @param winged 判断是否有翅膀
     * @return 返回正确类型的敌人
     */
    public static char getWingedEnemyVersion(char enemy, boolean winged) {
        // 判断是否带翅膀
        return switch (enemy) {
            case GOOMBA_WINGED -> winged ? GOOMBA : enemy;
            case GREEN_KOOPA_WINGED -> winged ? GREEN_KOOPA : enemy;
            case RED_KOOPA_WINGED -> winged ? RED_KOOPA : enemy;
            case SPIKY_WINGED -> winged ? SPIKY : enemy;
            default -> enemy;
        };
    }

    // 存储地图的长度和宽度，第一维是地图的高，第二维是地图的长
    private final char[][] map;

    /**
     * create the Level Model
     *
     * @param levelWidth  the width of the level
     * @param levelHeight the height of the level
     */
    public MarioLevelModel(int levelWidth, int levelHeight) {
        // 控制地图的高度和宽度
        this.map = new char[levelWidth][levelHeight];
    }

    /**
     * create a similar clone to the current map
     */
    public MarioLevelModel clone() {
        MarioLevelModel model = new MarioLevelModel(this.getWidth(), this.getHeight());
        for (int x = 0; x < model.getWidth(); x++) {
            for (int y = 0; y < model.getHeight(); y++) {
                model.map[x][y] = this.map[x][y];
            }
        }
        return model;
    }

    /**
     * get map width
     *
     * @return map width
     */
    public int getWidth() {
        // 获取地图的宽度
        return this.map.length;
    }

    /**
     * get map height
     *
     * @return map height
     */
    public int getHeight() {
        // 获取地图的高度
        return this.map[0].length;
    }

    /**
     * get the value of the tile in certain location
     *
     * @param x x tile position
     * @param y y tile position
     * @return the tile value
     */
    public char getBlock(int x, int y) {
        int currentX = x;
        int currentY = y;
        if (x < 0) currentX = 0;
        if (y < 0) currentY = 0;
        if (x > this.map.length - 1) currentX = this.map.length - 1;
        if (y > this.map[0].length - 1) currentY = this.map[0].length - 1;
        return this.map[currentX][currentY];
    }

    /**
     * set a tile on the map with certain value
     *
     * @param x     the x tile position
     * @param y     the y tile position
     * @param value the tile value to be set
     */
    public void setBlock(int x, int y, char value) {
        // 判断是否越界
        if ((x >= 0 && x < getWidth()) && (y >= 0 && y < getHeight())) {
            this.map[x][y] = value;
        }
    }

    /**
     * clear the whole map
     */
    public void clearMap() {
        // 清空地图
        for (int x = 0; x < this.getWidth(); ++x) {
            for (int y = 0; y < this.getHeight(); ++y) {
                this.setBlock(x, y, EMPTY);
            }
        }
    }

    /**
     * get the string value of the map
     *
     * @return the map in form of string
     */
    public String getMap() {
        // 以String的形式返回地图
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < map[0].length; y++) {
            for (char[] chars : map) {
                result.append(chars[y]);
            }
            result.append("\n");
        }
        return result.toString();
    }
}
