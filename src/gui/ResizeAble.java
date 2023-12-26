package gui;

import javax.swing.*;
import java.awt.*;

public interface ResizeAble {

    // 窗口的缩放比例
    double scale = 2.5;

    // 窗口的宽度
    int scaledHeight = (int) Math.round(256 * scale);

    // 窗口的高度
    int scaledWidth = (int) Math.round(256 * scale);

    // 窗口的字体大小
    int size = 32;
    /**
     * 创建标签
     *
     * @param text   标签的文本
     * @param x      x坐标
     * @param y      y坐标
     * @param width  宽度
     * @param height 高度
     * @return 标签
     */
    default JTextArea createLabel(String text, int x, int y, int width, int height) {
        JTextArea label = new JTextArea(text);
        // 设置标签不可编辑
        label.setEditable(false);
        // 设置标签的位置和大小
        label.setBounds(x, y, width, height);
        // 设置标签的字体
        label.setFont(new Font("微软雅黑", Font.BOLD, size));
        // 设置标签为透明
        label.setOpaque(false);
        // 设置字体颜色为粉色
        label.setForeground(Color.PINK);
        return label;
    }

    /**
     * 创建密码输入框
     *
     * @param x        x坐标
     * @param y        y坐标
     * @param width    宽度
     * @param height   高度
     * @param echoChar 回显字符
     * @return 密码输入框
     */
    default JPasswordField createPasswordField(int x, int y, int width, int height, char echoChar) {
        JPasswordField passwordField = new JPasswordField(size);
        passwordField.setBounds(x, y, width, height);
        passwordField.setFont(new Font("微软雅黑", Font.BOLD, size));
        passwordField.setBackground(null);
        passwordField.setEchoChar(echoChar);
        return passwordField;
    }
}
