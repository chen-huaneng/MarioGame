package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RegisterWindow extends JDialog {
    private final JPasswordField inputAccount;
    private final JPasswordField inputName;
    private final JPasswordField inputStudentID;
    private final JPasswordField inputPassword;
    private final JPasswordField inputConfirmPassword;
    // 窗口的字体大小
    private final int size = 32;

    public RegisterWindow(PassWindow pwin) throws IOException {
        super(pwin, "请先注册马里奥账号吧！", true);

        // 窗口的缩放比例
        double scale = 2.5;

        // 窗口的宽度
        int scaledHeight = (int) Math.round(256 * scale);

        // 窗口的高度
        int scaledWidth = (int) Math.round(256 * scale);

        // 设置窗口大小
        this.setSize(scaledWidth, scaledHeight);

        // 设置窗口位置为居中
        this.setLocationRelativeTo(null);

        //窗口居中设置窗口长宽
        JPanel panel = new BackgroundPanel();

        // 置空布局
        panel.setLayout(null);

        // 设置参数
        int width = 160;
        int height = 40;
        int x = scaledWidth / 8;
        int y = scaledHeight / 8;

        JTextArea labelAccount = createLabel("注册账号：", x, y, width, height);
        inputAccount = createPasswordField(x + width, y, width * 2, height, (char) 0);

        JTextArea labelPassword = createLabel("输入密码：", x, y + height * 2, width, height);
        inputPassword = createPasswordField(x + width, y + height * 2, width * 2, height, '*');

        JTextArea labelConfirmPassword = createLabel("确认密码：", x, y + height * 4, width, height);
        inputConfirmPassword = createPasswordField(x + width, y + height * 4, width * 2, height, '*');

        JTextArea labelName = createLabel("真实姓名：", x, y + height * 6, width, height);
        inputName = createPasswordField(x + width, y + height * 6, width * 2, height, (char) 0);

        JTextArea labelStudentID = createLabel("学号：", x, y + height * 8, width, height);
        inputStudentID = createPasswordField(x + width, y + height * 8, width * 2, height, (char) 0);

        JButton buttonRegister = new JButton("确认");
        buttonRegister.setBounds(x + width, y + height * 10, width, height);

        buttonRegister.addActionListener(e -> {
            String account = inputAccount.getText();
            String password = new String(inputPassword.getPassword());
            String confirmPassword = new String(inputConfirmPassword.getPassword());
            String name = inputName.getText();
            String studentID = inputStudentID.getText();

            // 检查是否存在空字段
            if (account.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || studentID.isEmpty()) {
                JOptionPane.showMessageDialog(RegisterWindow.this, "请填写所有信息！", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 检查密码是否一致
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(RegisterWindow.this, "密码不匹配，请重新输入！", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 将注册信息写入文件中
            try {
                File file = new File("credentials.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.write(account + "," + password);
                writer.newLine();
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(RegisterWindow.this, "注册成功！", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });


        add(labelAccount);
        add(inputAccount);
        add(labelPassword);
        add(inputPassword);
        add(labelConfirmPassword);
        add(inputConfirmPassword);
        add(labelName);
        add(inputName);
        add(labelStudentID);
        add(inputStudentID);
        add(buttonRegister);

        add("Center", panel);

        this.setVisible(true);
    }

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
    private JTextArea createLabel(String text, int x, int y, int width, int height) {
        JTextArea label = new JTextArea(text);
        label.setEditable(false);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("微软雅黑", Font.BOLD, size));
        label.setOpaque(false);
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
    private JPasswordField createPasswordField(int x, int y, int width, int height, char echoChar) {
        JPasswordField passwordField = new JPasswordField(size);
        passwordField.setBounds(x, y, width, height);
        passwordField.setFont(new Font("微软雅黑", Font.BOLD, size));
        passwordField.setBackground(null);
        passwordField.setEchoChar(echoChar);
        return passwordField;
    }
}
