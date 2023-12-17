package engine.core;

import engine.sprites.Mario;

import java.util.Random;

public class LevelGenerator {
    private static final int ODDS_STRAIGHT = 0;
    private static final int ODDS_HILL_STRAIGHT = 1;
    private static final int ODDS_TUBES = 2;
    private static final int ODDS_JUMP = 3;
    private static final int ODDS_CANNONS = 4;
    // 控制各种地图静态数据的概率
    private final int[] odds = new int[5];
    // 总概率
    private int totalOdds;
    // 控制难度
    private final int difficulty;
    // 控制地形类型
    private final int type;
    private final Random random;

    /** 初始化随机数生成器 */
    public LevelGenerator() {
        random = new Random();
        this.type = random.nextInt(3);
        this.difficulty = random.nextInt(5);
    }

    /** 根据随机数生成地图类型
     * @param model 地图模型
     * @param x 地形的横向起始位置
     * @param maxLength 表示当前地形的最大长度
     * @return 生成地形的长度 */
    private int buildZone(MarioLevelModel model, int x, int maxLength) {
        int t = random.nextInt(totalOdds);
        int type = 0;
        // 根据概率随机选择地形
        for (int i = 0; i < odds.length; ++i) {
            if (odds[i] <= t) {
                type = i;
            }
        }

        // Java 12 以上版本支持的enhanced switch 语法糖,如果版本过低换成普通的switch 语法
        return switch (type) {
            // 生成直线地形
            case ODDS_STRAIGHT -> buildStraight(model, x, maxLength, false);
            // 生成有坡度的地形
            case ODDS_HILL_STRAIGHT -> buildHillStraight(model, x, maxLength);
            // 生成管道
            case ODDS_TUBES -> buildTubes(model, x, maxLength);
            // 生成悬崖地形
            case ODDS_JUMP -> buildJump(model, x);
            // 生成炮台
            case ODDS_CANNONS -> buildCannons(model, x, maxLength);
            default -> 0;
        };
    }

    /**
     * 生成悬崖地形
     * @param model 地图模型
     * @param xo 当前地形的起始位置
     * @return 当前地形的长度
     */
    private int buildJump(MarioLevelModel model, int xo) {
        // 跳跃两边的长度
        int js = random.nextInt(4) + 2;
        // 跳跃的长度
        int jl = random.nextInt(2) + 2;
        // 悬崖的总长度
        int length = js * 2 + jl;

        // 有1/3的概率生成阶梯
        boolean hasStairs = random.nextInt(3) == 0;

        // 随机生成跳跃地形的底部高度
        int floor = model.getHeight() - 1 - random.nextInt(4);

        // 在悬崖两边设置方块
        // 设置悬崖左边的方块
        for (int x = xo; x < xo + js; ++x) {
            for (int y = 0; y < model.getHeight(); ++y) {
                // 大于底部的高度或者有阶梯并且满足一定的阶梯条件
                if (y >= floor || (hasStairs && (y >= floor - (x - xo) + 1))) {
                    model.setBlock(x, y, MarioLevelModel.GROUND);
                }
            }
        }

        // 设置悬崖右边的方块
        for (int x = xo + length - js; x < xo + length; ++x) {
            for (int y = 0; y < model.getHeight(); y++) {
                // 大于底部的高度或者有阶梯并且满足一定的阶梯条件
                if (y >= floor || (hasStairs && (y >= floor - ((xo + length) - x) + 2))) {
                    model.setBlock(x, y, MarioLevelModel.GROUND);
                }
            }
        }

        return length;
    }

    /**
     * 生成炮台地形
     * @param model 地图模型
     * @param xo 地形的起始位置
     * @param maxLength 地形的最大长度
     * @return 生成地形的长度
     */
    private int buildCannons(MarioLevelModel model, int xo, int maxLength) {
        // 随机生成地形的长度
        int length = random.nextInt(10) + 2;
        // 如果大于最大长度就设为最大长度
        if (length > maxLength) {
            length = maxLength;
        }

        // 设置地形的底部高度
        int floor = model.getHeight() - 1 - random.nextInt(4);
        // 初始化炮台的起始位置
        int xCannon = xo + 1 + random.nextInt(4);
        for (int x = xo; x < xo + length; ++x) {
            if (x > xCannon) {
                xCannon += 2 + random.nextInt(4);
            }
            if (xCannon == xo + length - 1) {
                xCannon += 10;
            }
            // 随机生成炮台的高度
            int cannonHeight = floor - random.nextInt(4) - 1;

            for (int y = 0; y < model.getHeight(); ++y) {
                if (y >= floor) {
                    model.setBlock(x, y, MarioLevelModel.GROUND);
                } else if (x == xCannon && y >= cannonHeight){
                    // 在炮塔的位置生成发射子弹的炮塔块
                    model.setBlock(x, y, MarioLevelModel.BULLET_BILL);
                }
            }
        }

        return length;
    }

    /**
     *
     * @param model 地图模型
     * @param xo 当前地形的起始位置
     * @param maxLength 当前地形的最大长度
     * @return 返回生成的地形的长度
     */
    private int buildHillStraight(MarioLevelModel model, int xo, int maxLength) {
        // 随机生成长度
        int length = random.nextInt(10) + 10;
        // 如果长度大于最大值就设为最大值
        if (length > maxLength) {
            length = maxLength;
        }

        // 地板的高度
        int floor = model.getHeight() - 1 - random.nextInt(4);
        // 在对应的高度和长度之内设置地板
        for (int x = xo; x < xo + length; ++x) {
            for (int y = floor; y < model.getHeight(); ++y) {
                model.setBlock(x, y, MarioLevelModel.GROUND);
            }
        }

        // 添加敌人
        addEnemyLine(model, xo + 1, xo + length - 1, floor - 1);

        int h = floor;

        // 判断是否继续生成
        boolean keepGoing = true;

        boolean[] occupied = new boolean[length];
        // 在坡度上生成地形
        while (keepGoing) {
            // 随机减少坡度的高度
            h = h - 2 - random.nextInt(3);

            // 坡度高度小于0则停止生成
            if (h <= 0) {
                keepGoing = false;
            } else {
                // 随机生成坡度的长度和起始位置
                int l = random.nextInt(5) + 3;
                int xxo = random.nextInt(length - l - 2) + xo + 1;

                // 检查新生成的坡度是否与现有的坡度重合
                if (occupied[xxo - xo] || occupied[xxo - xo + l] || occupied[xxo - xo - 1]
                        || occupied[xxo - xo + l + 1]) {
                    keepGoing = false;
                } else {
                    // 标记已生成的坡度位置
                    occupied[xxo - xo] = true;
                    occupied[xxo - xo + l] = true;
                    // 添加敌人
                    addEnemyLine(model, xxo, xxo + l, h - 1);
                    // 有1/4的概率生成砖块和金币等
                    if (random.nextInt(4) == 0) {
                        decorate(model, xxo - 1, xxo + l + 1, h);
                        keepGoing = false;
                    }
                    // 在新的坡度内设置地形块
                    for (int x = xxo; x < xxo + l; ++x) {
                        for (int y = h; y < floor; ++y) {
                            int yy = y == h ? 8 : 9;
                            if (model.getBlock(x, y) == MarioLevelModel.EMPTY) {
                                model.setBlock(x, y, yy == 8 ? MarioLevelModel.PLATFORM : MarioLevelModel.PLATFORM_BACKGROUND);
                            }
                        }
                    }
                }
            }
        }

        return length;
    }

    /**
     * @param model 地图
     * @param x0 起始位置
     * @param x1 终止位置
     * @param y 生成高度*/
    private void addEnemyLine(MarioLevelModel model, int x0, int x1, int y) {
        // 定义敌人的种类
        char[] enemies = new char[]{MarioLevelModel.GOOMBA,
                MarioLevelModel.GREEN_KOOPA,
                MarioLevelModel.RED_KOOPA,
                MarioLevelModel.SPIKY};
        // 在指定范围内生成敌人
        for (int x = x0; x < x1; ++x) {
            // 判断是否生成敌人
            if (random.nextInt(35) < difficulty + 1) {
                // 选择敌人的类型,根据难度设置不同的敌人
                int type = difficulty < 1 ? 0 : random.nextInt(3) + 1;
                // 在地图上设置敌人,根据难度判断是否给敌人加上翅膀
                model.setBlock(x, y, MarioLevelModel.getWingedEnemyVersion(enemies[type], random.nextInt(35) < difficulty));
            }
        }
    }

    /**
     *
     * @param model 地图模型
     * @param xo 当前地形的横向起始位置
     * @param maxLength 当前地形的最大长度
     * @return 当前地形的长度
     */
    private int buildTubes(MarioLevelModel model, int xo, int maxLength) {
        // 随机生成地形的长度
        int length = random.nextInt(10) + 5;
        // 如果长度大于最大长度则设为最大长度
        if (length > maxLength) {
            length = maxLength;
        }

        // 随机生成地形的地板高度
        int floor = model.getHeight() - 1 - random.nextInt(4);
        // 管道的起始位置
        int xTube = xo + 1 + random.nextInt(4);
        // 管道的高度
        int tubeHeight = floor - random.nextInt(2) - 2;
        for (int x = xo; x < xo + length; ++x) {
            if (x > xTube + 1) {
                xTube += 3 + random.nextInt(4);
                tubeHeight = floor - random.nextInt(2) - 2;
            }
            if (xTube >= xo + length - 2) {
                xTube += 10;
            }

            char tubeType = MarioLevelModel.PIPE;
            // 生成食人花管道的概率
            if (x == xTube && random.nextInt(11) < difficulty + 1) {
                tubeType = MarioLevelModel.PIPE_FLOWER;
            }

            for (int y = 0; y < model.getHeight(); ++y) {
                if (y >= floor) {
                    model.setBlock(x, y, MarioLevelModel.GROUND);
                } else if ((x == xTube || x == xTube + 1) && y >= tubeHeight) {
                    model.setBlock(x, y, tubeType);
                }
            }
        }

        return length;
    }

    /** 生成直线地形
     * @param model 地图
     * @param xo 直线地面的起始坐标
     * @param maxLength 直线地面最大长度
     * @param safe 是否生成安全地面，如果是，则直线地面长度会更长
     * @return 返回直线地面的长度*/
    private int buildStraight(MarioLevelModel model, int xo, int maxLength, boolean safe) {
        // 随机生成直线的长度
        // 如果是安全地面则长度更长
        int length = safe ? 10 + random.nextInt(5) : random.nextInt(10) + 2;

        // 如果直线的长度超过最大长度则设为最大长度
        if (length > maxLength) {
            length = maxLength;
        }

        // 设置地面的高度
        int floor = model.getHeight() - 1 - random.nextInt(4);
        // 从起始位置开始设置地面
        for (int x = xo; x < xo + length; ++x) {
            // 保证地面在一定的高度
            for (int y = floor; y < model.getHeight(); ++y) {
                model.setBlock(x, y, MarioLevelModel.GROUND);
            }
        }

        // 如果不是安全地面并且长度满足一定的要求，给直线上添加一些敌人、金币和砖块
        if (!safe && length > 5) {
            decorate(model, xo, xo + length, floor);
        }

        return length;
    }

    /** 添加一些装饰，包括敌人、金币和砖块
     * @param model 地图
     * @param x0 起始位置
     * @param x1 终止位置
     * @param floor 地板的高度*/
    private void decorate(MarioLevelModel model, int x0, int x1, int floor) {
        // 如果地板高度不足则返回
        if (floor < 1) {
            return;
        }

        // 添加敌人
        addEnemyLine(model, x0 + 1, x1 - 1, floor - 1);

        // 金币生成的范围
        int begin = x0 + 2 + random.nextInt(4);
        int end = x1 - 2 - random.nextInt(4);

        // 在当前地板的上方添加金币,并且地板的长度要在一定的范围内
        if (floor > 3 && end - begin > 1) {
            for (int x = begin; x < end; ++x) {
                model.setBlock(x, floor - 2, MarioLevelModel.COIN);
            }
        }

        // 更高位置的砖块和金币生成的范围
        int newBegin = x0 + 1 + random.nextInt(4);
        int newEnd = x1 - 1 - random.nextInt(4);

        // 如果地板的高度更高，在当前地板的更上方生成砖块或者金币,并且地板的长度要在一定的范围
        if (floor > 4 && newEnd - newBegin > 2) {
            for (int x = newBegin; x < newEnd; ++x) {
                // type用于存储类型，默认为金币
                char type = MarioLevelModel.COIN;

                // 生成砖块或者金币的概率
                int rate = random.nextInt(10);

                // 如果不在左边界和右边界上，则生成砖块，并且有5/10的概率
                if (x != x0 + 1 && x != x1 - 2 && rate < 5) {
                    type = MarioLevelModel.NORMAL_BRICK;
                } else if (rate < 7) { // 有2/10的概率生成含有金币的方块
                    type = MarioLevelModel.COIN_BRICK;
                } else if (rate == 7) { // 有1/10的概率生成含有蘑菇的方块
                    type = MarioLevelModel.MUSHROOM_BRICK;
                }
                // 剩下2/10的概率生成金币
                model.setBlock(x, floor - 4, type);
            }
        }
    }

    /**
     * @param model 控制地图的高度和长度
     * @return 返回地图*/
    public String getGeneratedLevel(MarioLevelModel model) {
        // 清空地图（初始化地图）
        model.clearMap();

        // 初始化地图静态物体的数据
        odds[ODDS_STRAIGHT] = 20;
        odds[ODDS_HILL_STRAIGHT] = 10;
        // 控制管道的数量
        odds[ODDS_TUBES] = 2 + 1 * difficulty;
        // 控制悬崖的数量
        odds[ODDS_JUMP] = 2 * difficulty;
        // 控制大炮的数量
        odds[ODDS_CANNONS] = -10 + 5 * difficulty;

        // 控制地图类型的生成
        if (type > 0) {
            odds[ODDS_HILL_STRAIGHT] = 0;
        }

        for (int i = 0; i < odds.length; i++) {
            // 概率修正，防止概率小于0
            if (odds[i] < 0) {
                odds[i] = 0;
            }
            // 计算总概率
            totalOdds += odds[i];
            // 计算累积概率，也即odd[i]前的概率之和，使得概率较高的值有较大被选中的概率
            odds[i] = totalOdds - odds[i];
        }

        // 地图的长度
        int length = 0;
        // 起始直线地面的长度
        length += buildStraight(model, 0, model.getWidth(), true);
        // 如果长度小于地图的长度，则继续生成地形
        while (length < model.getWidth()) {
            length += buildZone(model, length, model.getWidth() - length);
        }

        // 生成地图的底部，使得在一定高度以下都是地面
        int floor = model.getHeight() - 1 - random.nextInt(4);

        for (int x = length; x < model.getWidth(); ++x) {
            // 在一定高度以上设置为地板砖块
            for (int y = floor; y < model.getHeight(); ++y) {
                model.setBlock(x, y, MarioLevelModel.GROUND);
            }
        }

        // 生成特殊类型的地图
        if (type > 0) {
            int ceiling = 0;
            int run = 0;
            for (int x = 0; x < model.getWidth(); ++x) {
                if (run-- <= 0 && x > 4) {
                    ceiling = random.nextInt(4);
                    run = random.nextInt(4) + 4;
                }
                for (int y = 0; y < model.getHeight(); ++y) {
                    if ((x > 4 && y <= ceiling) || x < 1) {
                        model.setBlock(x, y, MarioLevelModel.NORMAL_BRICK);
                    }
                }
            }
        }

        return model.getMap();
    }
}
