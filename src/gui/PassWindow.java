package gui;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PassWindow extends JFrame implements ResizeAble {
    JTextArea textAccount, textPassword;
    JPasswordField inputAccount;
    JPasswordField inputPassword;
    JButton buttonLogin, buttonRegister, buttonReset;

    /**
     * 构造方法，创建游戏起始菜单界面。
     *
     * @throws IOException 当读取图像文件失败时抛出。
     */
    public PassWindow() throws IOException {
        // 设置窗口标题
        super("MarioXMU");

        this.setSize(scaledWidth, scaledHeight);

        // 设置窗口位置为居中
        this.setLocationRelativeTo(null);

        //窗口居中设置窗口长宽
        JPanel panel = new BackgroundPanel();

        // 置空布局
        panel.setLayout(null);

        int x = scaledWidth / 4, y = scaledHeight / 4, width = 80, height = 50;

        // 创建组件
        textAccount = createLabel("账号：", x, y, width, height);

        textPassword = createLabel("密码：", x, y + height * 2, width, height);

        inputAccount = createPasswordField(x + width, y, width * 3, height, (char) 0);

        inputPassword = createPasswordField(x + width, y + height * 2, width * 3, height, '*');

        int tmp = 40;
        buttonLogin = new JButton("登录");
        buttonLogin.setBounds(x, y + height * 4, width, height);

        buttonRegister = new JButton("注册");
        buttonRegister.setBounds(x + 3 * tmp, y + height * 4, width, height);

        buttonReset = new JButton("退出");
        buttonReset.setBounds(x + 6 * tmp, y + height * 4, width, height);

        // 登录按钮的监听器
        buttonLogin.addActionListener(e -> {
            // 登录逻辑
            String account = new String(inputAccount.getPassword());
            String password = new String(inputPassword.getPassword());

            if (checkCredentials(account, password)) {
                JOptionPane.showMessageDialog(PassWindow.this, "登录成功！ Welcome, " + getLastName(account), "Success", JOptionPane.INFORMATION_MESSAGE);

                dispose();

                try {
                    new StartMenu(2.5, 0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(PassWindow.this, "用户名或密码错误，请重新输入！", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 注册按钮的监听器
        buttonRegister.addActionListener(e -> {
            // 注册逻辑
            RegisterWindow registerWindow = null;
            try {
                registerWindow = new RegisterWindow(PassWindow.this);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            assert registerWindow != null;

            registerWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    // 更新登录信息
                    inputAccount.setText("");
                    inputPassword.setText("");
                }
            });
        });

        // 退出按钮的监听器
        buttonReset.addActionListener(e -> {
            // 重置逻辑
            System.exit(0);
        });

        // 添加组件
        panel.add(textAccount);
        panel.add(inputAccount);
        panel.add(textPassword);
        panel.add(inputPassword);
        panel.add(buttonLogin);
        panel.add(buttonRegister);
        panel.add(buttonReset);

        add("Center", panel);
        // 设置窗口可见
        setVisible(true);
    }

    /**
     * 获取用户的姓氏。
     *
     * @param fullName 用户的全名
     * @return 用户的姓氏
     */
    private String getLastName(String fullName) {
        String[] names = fullName.split(" ");
        return names.length > 0 ? names[0] : fullName;
    }

    /**
     * 检查用户的登录信息是否正确。
     *
     * @param account  用户的账号
     * @param password 用户的密码
     * @return 用户的登录信息是否正确
     */
    private boolean checkCredentials(String account, String password) {
        try {
            File file = new File("credentials.txt");
            if (!file.exists()) {
                return false;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length == 2 && credentials[0].equals(account) && credentials[1].equals(password)) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
