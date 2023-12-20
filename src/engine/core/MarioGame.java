package engine.core;

import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.KeyAdapter;

import javax.swing.JFrame;

import engine.helper.GameStatus;
import engine.helper.MarioActions;

public class MarioGame {
    // 屏幕的宽度
    public static final int width = 256;
    // 屏幕的高度
    public static final int height = 256;

    /**
     * pauses the whole game at any moment
     */
    // 暂停游戏
    public boolean pause = false;

    // 可视化
    private JFrame window = null;
    private MarioRender render = null;
    private Agent agent = null;
    private MarioWorld world = null;

    /**
     * 创建Mario游戏
     */
    public MarioGame() {

    }

    private int getDelay(int fps) {
        if (fps <= 0) {
            return 0;
        }
        return 1000 / fps;
    }

    /**
     * 设置游戏的主角
     *
     * @param agent 游戏的主角
     */
    private void setAgent(Agent agent) {
        this.agent = agent;
        // 为主角增加键盘监听器，用于操控人物移动事件
        if (agent != null) {
            this.render.addKeyListener(this.agent);
        }
    }

    /**
     * 以指定的地图开始Mario游戏
     *
     * @param level 以String形式展示的Mario地图
     * @param timer 游戏内的时间计时器，如果设置为0则变成无限制
     * @param marioState Mario的初始状态，0表示最小的Mario，1表示大个的Mario，2表示发射火球的Mario
     */
    public void playGame(String level, int timer, int marioState) {
        this.runGame(new Agent(), level, timer, marioState, true, 30, 2.5);
    }

    /**
     * 在指定的地图上运行Mario游戏
     *
     * @param level 以String形式展示的Mario地图
     * @param timer 游戏内的计时器，如果设置为0则变成无限制
     * @param marioState Mario的初始状态，0表示最小的Mario，1表示大个的Mario，2表示发射火球的Mario
     * @param visuals 控制游戏界面是否可视化
     * @param fps the number of frames per second that the update function is following
     * @param scale 界面比例大小，会乘以界面相应的长度和宽度
     */
    public void runGame(Agent agent, String level, int timer, int marioState, boolean visuals, int fps, double scale) {
        // 控制可视化的界面
        if (visuals) {
            // 创建游戏窗口
            this.window = new JFrame("Mario Game");
            // 控制界面大小
            this.render = new MarioRender(scale);
            // 将内容面板设置为render
            this.window.setContentPane(this.render);
            // 调整窗口的大小以适应内容面板的大小
            this.window.pack();
            // 设置为不能改变窗口的大小
            this.window.setResizable(false);
            // 将窗口设置到屏幕中央
            this.window.setLocationRelativeTo(null);
            // 设置窗口关闭的操作，当关闭窗口的时候，终值程序
            this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // 获取图像
            this.render.init();
            // 设置窗口为可见
            this.window.setVisible(true);
        }

        // 设置游戏主角
        this.setAgent(agent);

        // 进入游戏主循环
        this.gameLoop(level, timer, marioState, visuals, fps);
    }

    /**
     *
     *
     * @param level 游戏地图
     * @param timer 游戏内的时间
     * @param marioState 初始Mario的状态
     * @param visual 是否可视化
     * @param fps
     */
    private void gameLoop(String level, int timer, int marioState, boolean visual, int fps) {
        // 初始化
        this.world = new MarioWorld();

        // 控制可视化界面
        this.world.visuals = visual;
        // 初始化游戏主要的设置
        this.world.initializeLevel(level, 1000 * timer);
        // 控制可视化
        if (visual) {
            // 设置游戏的背景贴图
            this.world.initializeVisuals(this.render.getGraphicsConfiguration());
        }

        // 判断Mario的状态
        this.world.mario.isLarge = marioState > 0;
        this.world.mario.isFire = marioState > 1;

        // 更新游戏的状态
        this.world.update(new boolean[MarioActions.numberOfActions()]);

        // 获取当前的时间
        long currentTime = System.currentTimeMillis();

        // 初始化图像
        VolatileImage renderTarget = null;
        Graphics backBuffer = null;
        Graphics currentBuffer = null;
        if (visual) {
            renderTarget = this.render.createVolatileImage(MarioGame.width, MarioGame.height);
            backBuffer = this.render.getGraphics();
            currentBuffer = renderTarget.getGraphics();
            this.render.addFocusListener(this.render);
        }

        // 初始化动作
        this.agent.initialize();

        // 获取游戏的事件
        ArrayList<MarioEvent> gameEvents = new ArrayList<>();

        // 判断游戏是否结束
        while (this.world.gameStatus == GameStatus.RUNNING) {
            // 判断游戏是否暂停
            if (!this.pause) {
                // 获取动作
                boolean[] actions = this.agent.getActions();
                // 更新游戏世界
                this.world.update(actions);
                gameEvents.addAll(this.world.lastFrameEvents);
            }

            // 渲染世界
            if (visual) {
                this.render.renderWorld(this.world, renderTarget, backBuffer, currentBuffer);
            }

            //check if delay needed
            if (this.getDelay(fps) > 0) {
                try {
                    currentTime += this.getDelay(fps);
                    Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
