package engine.core;

import engine.effects.BrickEffect;
import engine.effects.CoinEffect;
import engine.effects.FireballEffect;
import engine.effects.MarioEffect;
import engine.graphics.MarioBackground;
import engine.helper.EventType;
import engine.helper.GameStatus;
import engine.helper.SpriteType;
import engine.helper.TileFeature;
import engine.sprites.BulletBill;
import engine.sprites.Enemy;
import engine.sprites.FireFlower;
import engine.sprites.Fireball;
import engine.sprites.LifeMushroom;
import engine.sprites.Mario;
import engine.sprites.MarioSprite;
import engine.sprites.Mushroom;
import engine.sprites.Shell;

import java.awt.*;
import java.util.ArrayList;

public class MarioWorld {
    // 游戏状态
    public GameStatus gameStatus;
    public int pauseTimer;
    public int fireballsOnScreen = 0;
    public int currentTimer = -1;
    public float cameraX;
    public float cameraY;
    public Mario mario;
    // 游戏地图
    public MarioLevel level;
    // 是否可视化
    public boolean visuals;
    public int currentTick;

    // 是否存活以及金币数量
    public int coins, lives;

    // 上一帧的事件列表
    public ArrayList<MarioEvent> lastFrameEvents;
    private final ArrayList<MarioSprite> sprites;
    private final ArrayList<Shell> shellsToCheck;
    private final ArrayList<Fireball> fireballsToCheck;
    // 待添加的精灵列表
    private final ArrayList<MarioSprite> addedSprites;
    // 要移除的精灵列表
    private final ArrayList<MarioSprite> removedSprites;

    private final ArrayList<MarioEffect> effects;

    private final MarioBackground[] backgrounds = new MarioBackground[2];

    /**
     * 初始化世界
     */
    public MarioWorld() {
        this.pauseTimer = 0;
        // 设置游戏状态
        this.gameStatus = GameStatus.RUNNING;
        // 初始化存储动态相关的链表
        this.sprites = new ArrayList<>();
        this.shellsToCheck = new ArrayList<>();
        this.fireballsToCheck = new ArrayList<>();
        this.addedSprites = new ArrayList<>();
        this.removedSprites = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.lastFrameEvents = new ArrayList<>();
    }

    /**
     * 设置游戏的背景
     *
     * @param graphicsConfig 图片相关的配置
     */
    public void initializeVisuals(GraphicsConfiguration graphicsConfig) {
        int[][] tempBackground = new int[][]{
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42},
                new int[]{42}
        };
        // 设置游戏的背景
        backgrounds[0] = new MarioBackground(graphicsConfig, MarioGame.width, tempBackground);

        tempBackground = new int[][]{
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{31, 32, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{34, 35, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 31, 32, 33, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 34, 35, 36, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        // 设置游戏的背景
        backgrounds[1] = new MarioBackground(graphicsConfig, MarioGame.width, tempBackground);
    }

    /**
     * 初始化地图中主要的设置
     *
     * @param level 游戏地图
     * @param timer 游戏内时间
     */
    public void initializeLevel(String level, int timer) {
        this.currentTimer = timer;
        this.level = new MarioLevel(level, this.visuals);

        // 添加Mario
        this.mario = new Mario(this.visuals, this.level.marioTileX * 16, this.level.marioTileY * 16);
        // Mario的存活状态
        this.mario.alive = true;
        // 在精灵列表中添加Mario
        this.sprites.add(this.mario);

        this.mario.world = this;
    }

    /**
     * 增加Mario的事件
     *
     * @param eventType  事件类型
     * @param eventParam 事件的参数
     */
    public void addEvent(EventType eventType, int eventParam) {
        // 小Mario
        int marioState = 0;

        // 判断Mario的状态，根据状态设置值
        if (this.mario.isLarge) {
            // 大Mario
            marioState = 1;
        }

        if (this.mario.isFire) {
            //火球Mario
            marioState = 2;
        }

        // 添加事件
        this.lastFrameEvents.add(new MarioEvent(eventType, eventParam, mario.x, mario.y, marioState, this.currentTick));
    }

    /**
     * 添加特效
     *
     * @param effect 特效
     */
    public void addEffect(MarioEffect effect) {
        this.effects.add(effect);
    }

    /**
     * 在地图中添加精灵
     *
     * @param sprite 精灵
     */
    public void addSprite(MarioSprite sprite) {
        this.addedSprites.add(sprite);
        // 设定敌人的初始状态
        sprite.alive = true;
        sprite.world = this;
        sprite.update();
    }

    /**
     * 在地图中移除精灵
     *
     * @param sprite 精灵
     */
    public void removeSprite(MarioSprite sprite) {
        // 将要移除的精灵添加到列表中
        this.removedSprites.add(sprite);
        // 将存活状态设为死亡
        sprite.alive = false;
        sprite.world = null;
    }

    /**
     * 检测乌龟壳的碰撞
     *
     * @param shell 乌龟壳
     */
    public void checkShellCollide(Shell shell) {
        shellsToCheck.add(shell);
    }

    /**
     * 检测火球的碰撞
     *
     * @param fireball 火球
     */
    public void checkFireballCollide(Fireball fireball) {
        fireballsToCheck.add(fireball);
    }

    public void win() {
        this.addEvent(EventType.WIN, 0);
        this.gameStatus = GameStatus.WIN;
    }

    /**
     * 判定Mario失败的情况
     */
    public void lose() {
        this.addEvent(EventType.LOSE, 0);
        this.gameStatus = GameStatus.LOSE;
        this.mario.alive = false;
    }

    /**
     * 超时事件
     */
    public void timeout() {
        // 将游戏状态设置为死亡
        this.gameStatus = GameStatus.TIME_OUT;
        // 将Mario的状态设置为死亡
        this.mario.alive = false;
    }

    /**
     * 判断某个精灵是否为敌人
     *
     * @param sprite 精灵
     * @return 是否为敌人
     */
    private boolean isEnemy(MarioSprite sprite) {
        return sprite instanceof Enemy || sprite instanceof BulletBill;
    }

    /**
     * 更新游戏状态
     *
     * @param actions 动作数组
     */
    public void update(boolean[] actions) {
        // 判断游戏是否正在运行
        if (this.gameStatus != GameStatus.RUNNING) {
            return;
        }

        if (this.pauseTimer > 0) {
            --this.pauseTimer;
            // 更新Mario图像
            if (this.visuals) {
                this.mario.updateGraphics();
            }
            return;
        }

        // 处理计时器的值
        if (this.currentTimer > 0) {
            this.currentTimer -= 30;

            // 如果时间小于0则触发超时事件
            if (this.currentTimer <= 0) {
                this.currentTimer = 0;
                // 触发超时事件
                this.timeout();
                return;
            }
        }

        // 更新当前的时间
        ++this.currentTick;

        // 更新相机的位置，使得Mario始终处于屏幕中央
        updateCameraPosition();

        // 清空上一帧的事件列表
        this.lastFrameEvents.clear();

        // 更新游戏状态
        updateGameStatus();

        // 处理精灵的图块生成和删除
        generateSprites();

        // 设置Mario的动作
        this.mario.actions = actions;

        // 更新精灵的状态
        for (MarioSprite sprite : sprites) {
            if (!sprite.alive) {
                continue;
            }
            sprite.update();
        }

        // 处理碰撞
        for (MarioSprite sprite : sprites) {
            if (!sprite.alive) {
                continue;
            }
            sprite.collideCheck();
        }

        checkShellCollide();
        checkFireballCollide();

        sprites.addAll(0, addedSprites);
        sprites.removeAll(removedSprites);

        addedSprites.clear();
        removedSprites.clear();
    }

    /**
     * 更新精灵的状态
     */
    private void updateGameStatus() {
        // 统计页面上的火球数量
        this.fireballsOnScreen = 0;

        // 更新关卡的状态，传递相机的位置
        for (MarioSprite sprite : sprites) {
            // 判断精灵的位置是否超出当前屏幕的位置
            if (sprite.x < cameraX - 64 || sprite.x > cameraX + MarioGame.width + 64 || sprite.y > this.level.height + 32) {

                // 如果Mario不在当前屏幕可见位置表示游戏结束
                if (sprite.type == SpriteType.MARIO) {
                    this.lose();
                }

                // 在游戏中移除该精灵
                this.removeSprite(sprite);

                // 判断是否为敌人并添加掉出世界的事件
                if (this.isEnemy(sprite) && sprite.y > MarioGame.height + 32) {
                    this.addEvent(EventType.FALL_KILL, sprite.type.getValue());
                }
                continue;
            }

            // 更新火球的数量
            if (sprite.type == SpriteType.FIREBALL) {
                this.fireballsOnScreen += 1;
            }
        }
    }

    /**
     * 处理图块的生成和删除
     */
    private void generateSprites() {
        // 处理图块的生成和删除，以当前相机为基准，每个图块的像素大小为16 * 16
        for (int x = (int) cameraX / 16 - 1; x <= (int) (cameraX + MarioGame.width) / 16 + 1; ++x) {
            for (int y = (int) cameraY / 16 - 1; y <= (int) (cameraY + MarioGame.height) / 16 + 1; ++y) {

                // 确定生成图块的位置和Mario的相对位置，用于处理一些需要根据方向生成的精灵
                int dir = 0;
                if (x * 16 + 8 > mario.x + 16) {
                    dir = -1;
                }
                if (x * 16 + 8 < mario.x - 16) {
                    dir = 1;
                }

                // 获取当前精灵的图块
                SpriteType type = level.getSpriteType(x, y);

                // 判断是否有需要新生成的精灵
                if (type != SpriteType.NONE) {
                    // 返回指定精灵的字符串形式贴图
                    String spriteCode = level.getSpriteCode(x, y);

                    boolean found = false;
                    // 查找是否已经存在相同的精灵
                    for (MarioSprite sprite : sprites) {
                        if (sprite.initialCode.equals(spriteCode)) {
                            found = true;
                            break;
                        }
                    }

                    // 如果没有找到则生成新的精灵并且不是上一时刻生成的
                    if (!found && this.level.getLastSpawnTick(x, y) != this.currentTick - 1) {
                        // 生成敌人
                        MarioSprite sprite = type.spawnSprite(this.visuals, x, y, dir);
                        sprite.initialCode = spriteCode;
                        this.addSprite(sprite);
                    }
                    // 设置最后生成的图块的位置
                    this.level.setLastSpawnTick(x, y, this.currentTick);
                }

                // 判断是否有需要删除的精灵
                if (dir != 0) {
                    // 获取当前图块的特征
                    ArrayList<TileFeature> features = TileFeature.getTileType(this.level.getBlock(x, y));
                    if (features.contains(TileFeature.SPAWNER) && this.currentTick % 100 == 0) {
                        // 添加炮弹
                        addSprite(new BulletBill(this.visuals, x * 16 + 8 + dir * 8, y * 16 + 15, dir));
                    }
                }
            }
        }
    }

    /**
     * 检测乌龟壳的碰撞和处理被碰撞的精灵
     */
    private void checkShellCollide() {
        // 碰撞检测和处理
        for (Shell shell : shellsToCheck) {
            for (MarioSprite sprite : sprites) {
                if (sprite != shell && shell.alive && sprite.alive) {
                    if (sprite.shellCollideCheck(shell)) {
                        this.removeSprite(sprite);
                    }
                }
            }
        }
        // 清除需要检查的乌龟壳碰撞数组
        shellsToCheck.clear();
    }

    /**
     * 检测火球的碰撞和处理被碰撞的精灵
     */
    private void checkFireballCollide() {
        // 火球检测和处理
        for (Fireball fireball : fireballsToCheck) {
            for (MarioSprite sprite : sprites) {
                if (sprite != fireball && fireball.alive && sprite.alive) {
                    if (sprite.fireballCollideCheck(fireball)) {
                        if (this.visuals) {
                            this.addEffect(new FireballEffect(fireball.x, fireball.y));
                        }
                        this.removeSprite(fireball);
                    }
                }
            }
        }
        // 清除需要检查的火球检查数组
        fireballsToCheck.clear();
    }

    /**
     * 更新相机的位置，使得Mario位于屏幕中央
     */
    private void updateCameraPosition() {
        // 控制相机的水平位置，使得相机的中心对准Mario
        this.cameraX = this.mario.x - MarioGame.width / 2;

        // 处理相机水平位置超出地图长度的情况
        if (this.cameraX + MarioGame.width > this.level.width) {
            this.cameraX = this.level.width - MarioGame.width;
        }

        // 限制相机的水平位置不会超过屏幕边界
        if (this.cameraX < 0) {
            this.cameraX = 0;
        }

        // 控制相机的高度位置
        this.cameraY = this.mario.y - MarioGame.height / 2;

        // 处理高度超过地图高度的情况
        if (this.cameraY + MarioGame.height > this.level.height) {
            this.cameraY = this.level.height - MarioGame.height;
        }

        // 限制相机的高度位置不会超过屏幕的边界
        if (this.cameraY < 0) {
            this.cameraY = 0;
        }
    }

    /**
     * 处理Mario的跳跃
     *
     * @param xTile          x坐标
     * @param yTile          y坐标
     * @param canBreakBricks 是否可以打破砖块
     */
    public void bump(int xTile, int yTile, boolean canBreakBricks) {
        int block = this.level.getBlock(xTile, yTile);
        // 获取图块的特征
        ArrayList<TileFeature> features = TileFeature.getTileType(block);

        // 判断是否可以被撞击
        if (features.contains(TileFeature.BUMPABLE)) {
            bumpInto(xTile, yTile - 1);
            level.setBlock(xTile, yTile, 14);
            // 设置图块的偏移量
            level.setShiftIndex(xTile, yTile, 4);

            // 判断是否为特殊图块
            if (features.contains(TileFeature.SPECIAL)) {
                // 根据当前Mario的状态生成不同的蘑菇
                if (!this.mario.isLarge) {
                    addSprite(new Mushroom(this.visuals, xTile * 16 + 9, yTile * 16 + 8));
                } else {
                    addSprite(new FireFlower(this.visuals, xTile * 16 + 9, yTile * 16 + 8));
                }
            } else if (features.contains(TileFeature.LIFE)) {
                // 添加生命蘑菇
                addSprite(new LifeMushroom(this.visuals, xTile * 16 + 9, yTile * 16 + 8));
            } else {
                // 添加金币
                mario.collectCoin();
                // 添加金币特效
                if (this.visuals) {
                    this.addEffect(new CoinEffect(xTile * 16 + 8, (yTile) * 16));
                }
            }
        }

        // 判断是否可以被打破
        if (features.contains(TileFeature.BREAKABLE)) {
            // 添加碰撞事件
            bumpInto(xTile, yTile - 1);
            // 判断是否可以打破砖块
            if (canBreakBricks) {
                level.setBlock(xTile, yTile, 0);
                // 添加砖块特效
                if (this.visuals) {
                    for (int xx = 0; xx < 2; ++xx) {
                        for (int yy = 0; yy < 2; ++yy) {
                            this.addEffect(new BrickEffect(xTile * 16 + xx * 8 + 4, yTile * 16 + yy * 8 + 4, (xx * 2 - 1) * 4, (yy * 2 - 1) * 4 - 8));
                        }
                    }
                }
            } else {
                // 设置图块的偏移量
                level.setShiftIndex(xTile, yTile, 4);
            }
        }
    }

    /**
     * 处理Mario的碰撞
     *
     * @param xTile x坐标
     * @param yTile y坐标
     */
    public void bumpInto(int xTile, int yTile) {
        int block = level.getBlock(xTile, yTile);
        // 获取图块的特征
        if (TileFeature.getTileType(block).contains(TileFeature.PICKABLE)) {
            // 添加收集事件
            this.addEvent(EventType.COLLECT, block);
            // 收集金币
            this.mario.collectCoin();
            // 设置图块为空
            level.setBlock(xTile, yTile, 0);
            // 添加金币特效
            if (this.visuals) {
                this.addEffect(new CoinEffect(xTile * 16 + 8, yTile * 16 + 8));
            }
        }

        // 判断是否为敌人
        for (MarioSprite sprite : sprites) {
            sprite.bumpCheck(xTile, yTile);
        }
    }

    public void render(Graphics og) {
        for (int i = 0; i < backgrounds.length; i++) {
            this.backgrounds[i].render(og, (int) cameraX, (int) cameraY);
        }
        for (MarioSprite sprite : sprites) {
            if (sprite.type == SpriteType.MUSHROOM || sprite.type == SpriteType.LIFE_MUSHROOM ||
                    sprite.type == SpriteType.FIRE_FLOWER || sprite.type == SpriteType.ENEMY_FLOWER) {
                sprite.render(og);
            }
        }
        this.level.render(og, (int) cameraX, (int) cameraY);
        for (MarioSprite sprite : sprites) {
            if (sprite.type != SpriteType.MUSHROOM && sprite.type != SpriteType.LIFE_MUSHROOM &&
                    sprite.type != SpriteType.FIRE_FLOWER && sprite.type != SpriteType.ENEMY_FLOWER) {
                sprite.render(og);
            }
        }
        for (int i = 0; i < this.effects.size(); i++) {
            if (this.effects.get(i).life <= 0) {
                this.effects.remove(i);
                i--;
                continue;
            }
            this.effects.get(i).render(og, cameraX, cameraY);
        }
    }
}
