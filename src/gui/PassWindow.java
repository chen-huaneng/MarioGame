package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PassWindow extends JFrame {
    JTextArea textAccount, textPassword;
    JPasswordField inputAccount;
    JPasswordField inputPassword;
    JButton buttonLogin, buttonRegister, buttonReset;

    public PassWindow() throws IOException {
        super("欢迎进入马里奥游戏！");
        this.setSize(450, 450);
        this.setLocationRelativeTo(null);
        //窗口居中设置窗口长宽
        JPanel panel = new BackgroundPanel();
        panel.setLayout(null);
        //置空布局

        textAccount = new JTextArea("账号：");
        textAccount.setEditable(false);//可否编辑
        textAccount.setBounds(100, 150, 40, 25);//位置以及大小
        textAccount.setFont(new Font("微软雅黑", 1, 16));//设置字号字体
        textAccount.setOpaque(false);//设置窗体为透明*/

        textPassword = new JTextArea("密码：");
        textPassword.setEditable(false);
        textPassword.setBounds(100, 200, 40, 25);
        textPassword.setFont(new Font("微软雅黑", 1, 16));
        textPassword.setOpaque(false);

        inputAccount = new JPasswordField(14);
        inputAccount.setBounds(150, 150, 150, 25);
        inputAccount.setBackground(null);
        inputAccount.setEchoChar((char) 0);

        inputPassword = new JPasswordField(14);
        inputPassword.setEchoChar('*');
        inputPassword.setBounds(150, 200, 150, 25);
        inputPassword.setBackground(null);

        buttonLogin = new JButton("登录");
        buttonLogin.setBounds(100, 300, 60, 25);
        buttonRegister = new JButton("注册");
        buttonRegister.setBounds(175, 300, 60, 25);
        buttonReset = new JButton("退出");
        buttonReset.setBounds(250, 300, 60, 25);

        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 登录逻辑
                String account = inputAccount.getText();
                String password = new String(inputPassword.getPassword());

                if (checkCredentials(account, password)) {
                    JOptionPane.showMessageDialog(PassWindow.this, "登录成功！ Welcome, " + getLastName(account), "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    try {
                        StartMenu rwin = new StartMenu(2.5, 0);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(PassWindow.this, "用户名或密码错误，请重新输入！", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 注册逻辑
                RegisterWindow registerWindow = null;
                try {
                    registerWindow = new RegisterWindow(PassWindow.this);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                registerWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        refreshCredentials();
                    }
                });
            }
        });

        buttonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 重置逻辑
                System.exit(0);
            }
        });

        panel.add(textAccount);
        panel.add(inputAccount);
        panel.add(textPassword);
        panel.add(inputPassword);
        panel.add(buttonLogin);
        panel.add(buttonRegister);
        panel.add(buttonReset);

        add("Center", panel);
        setVisible(true);
    }

    private String getLastName(String fullName) {

        String[] names = fullName.split(" ");
        return names.length > 0 ? names[0] : fullName;
    }

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

    private void refreshCredentials() {
        // 更新登录信息
        inputAccount.setText("");
        inputPassword.setText("");
    }

}
