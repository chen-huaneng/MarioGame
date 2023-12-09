package engine.core;

import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.KeyAdapter;

import javax.swing.JFrame;

import engine.helper.GameStatus;
import engine.helper.MarioActions;

public class MarioGame {
    /**
     * Screen width
     */
    public static final int width = 256;
    /**
     * Screen height
     */
    public static final int height = 256;

    /**
     * pauses the whole game at any moment
     */
    // 暂停游戏
    public boolean pause = false;

    /**
     * events that kills the player when it happens only care about type and param
     */
    private MarioEvent[] killEvents;

    //visualization
    private JFrame window = null;
    private MarioRender render = null;
    private Agent agent = null;
    private MarioWorld world = null;

    /**
     * Create a mario game to be played
     */
    public MarioGame() {

    }

    private int getDelay(int fps) {
        if (fps <= 0) {
            return 0;
        }
        return 1000 / fps;
    }

    private void setAgent(Agent agent) {
        this.agent = agent;
        if (agent != null) {
            this.render.addKeyListener(this.agent);
        }
    }

    /**
     * Play a certain mario level
     *
     * @param level      a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer      number of ticks for that level to be played. Setting timer to anything &lt;=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
     * @return statistics about the current game
     */
    public MarioResult playGame(String level, int timer, int marioState) {
        if (!GraphicsEnvironment.isHeadless()) {
            // 在非 headless 模式下执行 GUI 相关的代码
            // 例如：创建 JFrame、显示图形界面等
            return this.runGame(new Agent(), level, timer, marioState, false, 30, 2);
        }
        return this.runGame(new Agent(), level, timer, marioState, true, 30, 2);
    }

    /**
     * Run a certain mario level with a certain agent
     *
     * @param agent      the current AI agent used to play the game
     * @param level      a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer      number of ticks for that level to be played. Setting timer to anything &lt;=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
     * @param visuals    show the game visuals if it is true and false otherwise
     * @param fps        the number of frames per second that the update function is following
     * @param scale      the screen scale, that scale value is multiplied by the actual width and height
     * @return statistics about the current game
     */
    public MarioResult runGame(Agent agent, String level, int timer, int marioState, boolean visuals, int fps, float scale) {
        // 控制可视化的界面
        if (visuals) {
            this.window = new JFrame("Mario Game");
            this.render = new MarioRender(scale);
            this.window.setContentPane(this.render);
            this.window.pack();
            this.window.setResizable(false);
            this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.render.init();
            this.window.setVisible(true);
        }
        // 设置游戏主角
        this.setAgent(agent);

        return this.gameLoop(level, timer, marioState, visuals, fps);
    }

    private MarioResult gameLoop(String level, int timer, int marioState, boolean visual, int fps) {
        this.world = new MarioWorld();

        // 控制可视化界面
        this.world.visuals = visual;
        this.world.initializeLevel(level, 1000 * timer);
        if (visual) {
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

        // 返回游戏的结果
        return new MarioResult(this.world, gameEvents);
    }
}
