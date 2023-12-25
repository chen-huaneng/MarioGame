package engine.core;

import engine.helper.MarioActions;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Agent extends KeyAdapter {
    private boolean[] actions = null;

    /**
     * 初始化
     */
    public void initialize() {
        actions = new boolean[MarioActions.numberOfActions()];
    }

    /**
     * 获取按键的状态
     *
     * @return 按键的状态
     */
    public boolean[] getActions() {
        return actions;
    }

    /**
     * 按下按键的事件
     */
    public void keyPressed(KeyEvent e) {
        toggleKey(e.getKeyCode(), true);
    }

    /**
     * 释放按键的事件
     */
    public void keyReleased(KeyEvent e) {
        toggleKey(e.getKeyCode(), false);
    }

    /**
     * 根据事件触发
     */
    private void toggleKey(int keyCode, boolean isPressed) {
        // 如果事件为空则返回
        if (this.actions == null) {
            return;
        }
        // 判断按下的按键的类型
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                this.actions[MarioActions.LEFT.getValue()] = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                this.actions[MarioActions.RIGHT.getValue()] = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                this.actions[MarioActions.DOWN.getValue()] = isPressed;
                break;
            case KeyEvent.VK_S:
                this.actions[MarioActions.JUMP.getValue()] = isPressed;
                break;
            case KeyEvent.VK_A:
                this.actions[MarioActions.SPEED.getValue()] = isPressed;
                break;
        }
    }
}
