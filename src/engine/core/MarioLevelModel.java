package engine.core;

public class MarioLevelModel {
    //start and end of the level
    public static final char EMPTY = '-';

    //game tiles symbols
    public static final char GROUND = 'X';
    public static final char NORMAL_BRICK = 'S';
    public static final char COIN = 'o';
    public static final char PIPE = 't';
    public static final char PIPE_FLOWER = 'T';
    public static final char BULLET_BILL = '*';
    public static final char PLATFORM_BACKGROUND = '|';
    public static final char PLATFORM = '%';

    //enemies that can be in the level
    public static final char GOOMBA = 'g';
    public static final char GOOMBA_WINGED = 'G';
    public static final char RED_KOOPA = 'r';
    public static final char RED_KOOPA_WINGED = 'R';
    public static final char GREEN_KOOPA = 'k';
    public static final char GREEN_KOOPA_WINGED = 'K';
    public static final char SPIKY = 'y';
    public static final char SPIKY_WINGED = 'Y';

    /**
     * Get the correct version of the enemy char
     *
     * @param enemy  the enemy character
     * @param winged boolean to indicate if its a winged enemy
     * @return correct character based on winged
     */
    public static char getWingedEnemyVersion(char enemy, boolean winged) {
        if (!winged) {
            if (enemy == GOOMBA_WINGED) {
                return GOOMBA;
            }
            if (enemy == GREEN_KOOPA_WINGED) {
                return GREEN_KOOPA;
            }
            if (enemy == RED_KOOPA_WINGED) {
                return RED_KOOPA;
            }
            if (enemy == SPIKY_WINGED) {
                return SPIKY;
            }
            return enemy;
        }
        if (enemy == GOOMBA) {
            return GOOMBA_WINGED;
        }
        if (enemy == GREEN_KOOPA) {
            return GREEN_KOOPA_WINGED;
        }
        if (enemy == RED_KOOPA) {
            return RED_KOOPA_WINGED;
        }
        if (enemy == SPIKY) {
            return SPIKY_WINGED;
        }
        return enemy;
    }

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
        // 获取地图的长度
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
        if (x < 0 || y < 0 || x > this.map.length - 1 || y > this.map[0].length - 1) {
            return;
        }
        this.map[x][y] = value;
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
