package engine.core;

import engine.helper.Assets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


public class MarioRender extends JComponent implements FocusListener {
    // 界面大小的比例
    private final double scale;
    private GraphicsConfiguration graphicsConfiguration;

    boolean focused;

    /**
     * 控制游戏界面的大小和交互功能
     *
     * @param scale 界面大小的比例
     */
    public MarioRender(double scale) {
        // 设置可以获得焦点
        this.setFocusable(true);
        // 启用与用户交互的功能
        this.setEnabled(true);
        // 设置界面的大小比例
        this.scale = scale;

        // 设置界面的尺寸大小
        Dimension size = new Dimension((int) (256 * scale), (int) (240 * scale));

        // 确保界面的大小一致性
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
    }

    /**
     * 初始化和图像相关的资源
     */
    public void init() {
        // 获取当前对象的图形配置，例如屏幕分辨率、颜色深度等
        graphicsConfiguration = getGraphicsConfiguration();
        // 初始化
        Assets.init(graphicsConfiguration);
    }

    /**
     * 绘制游戏界面
     * @param world 游戏世界
     * @param image 图像
     * @param g 图形
     * @param og 原始图形
     */
    public void renderWorld(MarioWorld world, Image image, Graphics g, Graphics og) {
        // 清空游戏界面
        og.fillRect(0, 0, 256, 240);
        // 绘制游戏世界
        world.render(og);
        // 绘制游戏界面的信息
        drawStringDropShadow(og, "Lives: " + world.lives, 0, 0, 7);
        drawStringDropShadow(og, "Coins: " + world.coins, 11, 0, 7);
        drawStringDropShadow(og, "Time: " + (world.currentTimer == -1 ? "Inf" : (int) Math.ceil(world.currentTimer / 1000f)), 22, 0, 7);
        // 绘制游戏界面
        if (scale > 1) {
            g.drawImage(image, 0, 0, (int) (256 * scale), (int) (240 * scale), null);
        } else {
            g.drawImage(image, 0, 0, null);
        }
    }

    /**
     * 绘制游戏界面的信息
     * @param g 图形
     * @param text 文本
     * @param x 横坐标
     * @param y 纵坐标
     * @param c 颜色
     */
    public void drawStringDropShadow(Graphics g, String text, int x, int y, int c) {
        drawString(g, text, x * 8 + 5, y * 8 + 5, 0);
        drawString(g, text, x * 8 + 4, y * 8 + 4, c);
    }

    /**
     * 绘制游戏界面的信息
     * @param g 图形
     * @param text 文本
     * @param x 横坐标
     * @param y 纵坐标
     * @param c 颜色
     */
    private void drawString(Graphics g, String text, int x, int y, int c) {
        // 将字符串转换为字符数组
        char[] ch = text.toCharArray();
        // 绘制游戏界面的信息
        for (int i = 0; i < ch.length; i++) {
            g.drawImage(Assets.font[ch[i] - 32][c], x + i * 8, y, null);
        }
    }

    public void focusGained(FocusEvent arg0) {
        focused = true;
    }

    public void focusLost(FocusEvent arg0) {
        focused = false;
    }
}
