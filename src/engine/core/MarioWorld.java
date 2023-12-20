package engine.core;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import engine.effects.*;
import engine.graphics.MarioBackground;
import engine.helper.EventType;
import engine.helper.GameStatus;
import engine.helper.SpriteType;
import engine.helper.TileFeature;
import engine.sprites.*;

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
    //Status
    public int coins, lives;
    public ArrayList<MarioEvent> lastFrameEvents;
    private final ArrayList<MarioSprite> sprites;
    private final ArrayList<Shell> shellsToCheck;
    private final ArrayList<Fireball> fireballsToCheck;
    private final ArrayList<MarioSprite> addedSprites;
    private final ArrayList<MarioSprite> removedSprites;

    private final ArrayList<MarioEffect> effects;

    private final MarioBackground[] backgrounds = new MarioBackground[2];

    /**
     * 初始化世界 */
    public MarioWorld() {
        this.pauseTimer = 0;
        this.gameStatus = GameStatus.RUNNING;
        this.sprites = new ArrayList<>();
        this.shellsToCheck = new ArrayList<>();
        this.fireballsToCheck = new ArrayList<>();
        this.addedSprites = new ArrayList<>();
        this.removedSprites = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.lastFrameEvents = new ArrayList<>();
    }

    /** 初始化游戏可视化 */
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
        backgrounds[1] = new MarioBackground(graphicsConfig, MarioGame.width, tempBackground);
    }

    /**
     * 初始化地图
     *
     * @param level 游戏地图
     * @param timer 游戏内时间
     */
    public void initializeLevel(String level, int timer) {
        this.currentTimer = timer;
        this.level = new MarioLevel(level, this.visuals);

        // 添加Mario
        this.mario = new Mario(this.visuals, this.level.marioTileX * 16, this.level.marioTileY * 16);
        this.mario.alive = true;
        this.sprites.add(this.mario);

        this.mario.world = this;
    }

    public void addEvent(EventType eventType, int eventParam) {
        int marioState = 0;
        if (this.mario.isLarge) {
            marioState = 1;
        }
        if (this.mario.isFire) {
            marioState = 2;
        }
        this.lastFrameEvents.add(new MarioEvent(eventType, eventParam, mario.x, mario.y, marioState, this.currentTick));
    }

    public void addEffect(MarioEffect effect) {
        this.effects.add(effect);
    }

    public void addSprite(MarioSprite sprite) {
        this.addedSprites.add(sprite);
        sprite.alive = true;
        sprite.world = this;
        sprite.added();
        sprite.update();
    }

    public void removeSprite(MarioSprite sprite) {
        this.removedSprites.add(sprite);
        sprite.alive = false;
        sprite.removed();
        sprite.world = null;
    }

    public void checkShellCollide(Shell shell) {
        shellsToCheck.add(shell);
    }

    public void checkFireballCollide(Fireball fireball) {
        fireballsToCheck.add(fireball);
    }

    public void win() {
        this.addEvent(EventType.WIN, 0);
        this.gameStatus = GameStatus.WIN;
    }

    public void lose() {
        this.addEvent(EventType.LOSE, 0);
        this.gameStatus = GameStatus.LOSE;
        this.mario.alive = false;
    }

    public void timeout() {
        this.gameStatus = GameStatus.TIME_OUT;
        this.mario.alive = false;
    }

    private boolean isEnemy(MarioSprite sprite) {
        return sprite instanceof Enemy || sprite instanceof FlowerEnemy || sprite instanceof BulletBill;
    }

    /** 更新游戏的数据 */
    public void update(boolean[] actions) {
        // 判断游戏是否正在运行
        if (this.gameStatus != GameStatus.RUNNING) {
            return;
        }

        if (this.pauseTimer > 0) {
            --this.pauseTimer;
            // 更新图像
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
                this.timeout();
                return;
            }
        }
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

    private void updateGameStatus() {
        // 统计页面上的火球数量
        this.fireballsOnScreen = 0;

        // 更新关卡的状态，传递相机的位置
        for (MarioSprite sprite : sprites) {
            if (sprite.x < cameraX - 64 || sprite.x > cameraX + MarioGame.width + 64 || sprite.y > this.level.height + 32) {
                if (sprite.type == SpriteType.MARIO) {
                    this.lose();
                }
                this.removeSprite(sprite);
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

    /** 处理精灵图块的生成和删除 */
    private void generateSprites() {
        // 处理图块的生成和删除，以当前相机为基准，每个图块的像素大小为16 * 16
        for (int x = (int) cameraX / 16 - 1; x <= (int) (cameraX + MarioGame.width) / 16 + 1; ++x) {
            for (int y = (int) cameraY / 16 - 1; y <= (int) (cameraY + MarioGame.height) / 16 + 1; ++y) {
                // 确定生成图块的位置和Mario的相对位置，用于处理一些需要根据方向生成的精灵
                int dir = 0;
                if (x * 16 + 8 > mario.x + 16)
                    dir = -1;
                if (x * 16 + 8 < mario.x - 16)
                    dir = 1;

                // 获取当前精灵的图块
                SpriteType type = level.getSpriteType(x, y);
                // 判断是否有需要新生成的精灵
                if (type != SpriteType.NONE) {
                    String spriteCode = level.getSpriteCode(x, y);
                    boolean found = false;
                    // 查找是否已经存在相同的精灵
                    for (MarioSprite sprite : sprites) {
                        if (sprite.initialCode.equals(spriteCode)) {
                            found = true;
                            break;
                        }
                    }
                    // 生成新的精灵
                    if (!found) {
                        if (this.level.getLastSpawnTick(x, y) != this.currentTick - 1) {
                            MarioSprite sprite = type.spawnSprite(this.visuals, x, y, dir);
                            sprite.initialCode = spriteCode;
                            this.addSprite(sprite);
                        }
                    }
                    this.level.setLastSpawnTick(x, y, this.currentTick);
                }

                if (dir != 0) {
                    ArrayList<TileFeature> features = TileFeature.getTileType(this.level.getBlock(x, y));
                    if (features.contains(TileFeature.SPAWNER)) {
                        if (this.currentTick % 100 == 0) {
                            addSprite(new BulletBill(this.visuals, x * 16 + 8 + dir * 8, y * 16 + 15, dir));
                        }
                    }
                }
            }
        }
    }

    /** 检测乌龟壳的碰撞和处理被碰撞的精灵 */
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

    /** 检测火球的碰撞和处理被碰撞的精灵 */
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

    /** 更新相机的位置 */
    private void updateCameraPosition() {
        // 控制相机的位置，使得相机的中心对准Mario
        this.cameraX = this.mario.x - MarioGame.width / 2;
        if (this.cameraX + MarioGame.width > this.level.width) {
            this.cameraX = this.level.width - MarioGame.width;
        }
        // 限制相机的位置不会超过屏幕边界
        if (this.cameraX < 0) {
            this.cameraX = 0;
        }

        this.cameraY = this.mario.y - MarioGame.height / 2;
        if (this.cameraY + MarioGame.height > this.level.height) {
            this.cameraY = this.level.height - MarioGame.height;
        }
        if (this.cameraY < 0) {
            this.cameraY = 0;
        }
    }

    public void bump(int xTile, int yTile, boolean canBreakBricks) {
        int block = this.level.getBlock(xTile, yTile);
        ArrayList<TileFeature> features = TileFeature.getTileType(block);

        if (features.contains(TileFeature.BUMPABLE)) {
            bumpInto(xTile, yTile - 1);
            this.addEvent(EventType.BUMP, MarioForwardModel.OBS_QUESTION_BLOCK);
            level.setBlock(xTile, yTile, 14);
            level.setShiftIndex(xTile, yTile, 4);

            if (features.contains(TileFeature.SPECIAL)) {
                if (!this.mario.isLarge) {
                    addSprite(new Mushroom(this.visuals, xTile * 16 + 9, yTile * 16 + 8));
                } else {
                    addSprite(new FireFlower(this.visuals, xTile * 16 + 9, yTile * 16 + 8));
                }
            } else if (features.contains(TileFeature.LIFE)) {
                addSprite(new LifeMushroom(this.visuals, xTile * 16 + 9, yTile * 16 + 8));
            } else {
                mario.collectCoin();
                if (this.visuals) {
                    this.addEffect(new CoinEffect(xTile * 16 + 8, (yTile) * 16));
                }
            }
        }

        if (features.contains(TileFeature.BREAKABLE)) {
            bumpInto(xTile, yTile - 1);
            if (canBreakBricks) {
                this.addEvent(EventType.BUMP, MarioForwardModel.OBS_BRICK);
                level.setBlock(xTile, yTile, 0);
                if (this.visuals) {
                    for (int xx = 0; xx < 2; xx++) {
                        for (int yy = 0; yy < 2; yy++) {
                            this.addEffect(new BrickEffect(xTile * 16 + xx * 8 + 4, yTile * 16 + yy * 8 + 4,
                                    (xx * 2 - 1) * 4, (yy * 2 - 1) * 4 - 8));
                        }
                    }
                }
            } else {
                level.setShiftIndex(xTile, yTile, 4);
            }
        }
    }

    public void bumpInto(int xTile, int yTile) {
        int block = level.getBlock(xTile, yTile);
        if (TileFeature.getTileType(block).contains(TileFeature.PICKABLE)) {
            this.addEvent(EventType.COLLECT, block);
            this.mario.collectCoin();
            level.setBlock(xTile, yTile, 0);
            if (this.visuals) {
                this.addEffect(new CoinEffect(xTile * 16 + 8, yTile * 16 + 8));
            }
        }

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
