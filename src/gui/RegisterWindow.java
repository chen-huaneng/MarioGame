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

    JPasswordField inputAccount;
    JPasswordField inputName;
    JPasswordField inputStudentID;
    JPasswordField inputPassword, inputConfirmPassword;
    JButton buttonRegister;

    public RegisterWindow(PassWindow pwin) throws IOException {
        super(pwin, "请先注册马里奥账号吧！", true);
        this.setSize(450, 450);
        this.setLocationRelativeTo(null);
        //窗口居中设置窗口长宽
        JPanel panel = new BackgroundPanel();
        panel.setLayout(null);
        //置空布局

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 用于放置注册账号
        JLabel labelAccount = new JLabel("注册账号：");
        labelAccount.setBounds(100, 50, 80, 25);
        labelAccount.setFont(new Font("微软雅黑", 1, 16));
        labelAccount.setOpaque(false);

        inputAccount = new JPasswordField(14);
        inputAccount.setBounds(200, 50, 100, 25);
        inputAccount.setBackground(null);
        inputAccount.setEchoChar((char) 0);
        panel1.add(labelAccount);
        panel1.add(inputAccount);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 用于放置输入密码
        JLabel labelPassword = new JLabel("输入密码：");
        labelPassword.setBounds(100, 100, 80, 25);
        labelPassword.setFont(new Font("微软雅黑", 1, 16));
        labelPassword.setOpaque(false);

        inputPassword = new JPasswordField(10);
        inputPassword.setBounds(200, 100, 100, 25);
        inputPassword.setBackground(null);
        inputPassword.setEchoChar('*');
        panel2.add(labelPassword);
        panel2.add(inputPassword);

        JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 用于放置确认密码
        JLabel labelConfirmPassword = new JLabel("确认密码：");
        labelConfirmPassword.setBounds(100, 150, 80, 25);
        labelConfirmPassword.setFont(new Font("微软雅黑", 1, 16));
        labelConfirmPassword.setOpaque(false);

        inputConfirmPassword = new JPasswordField(10);
        inputConfirmPassword.setBounds(200, 150, 100, 25);
        inputConfirmPassword.setBackground(null);
        inputConfirmPassword.setEchoChar('*');
        panel3.add(labelConfirmPassword);
        panel3.add(inputConfirmPassword);

        JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 用于放置真实姓名
        JLabel labelName = new JLabel("真实姓名：");
        labelName.setBounds(100, 200, 80, 25);
        labelName.setFont(new Font("微软雅黑", 1, 16));
        labelName.setOpaque(false);

        inputName = new JPasswordField(10);
        inputName.setBounds(200, 200, 100, 25);
        inputName.setBackground(null);
        inputName.setEchoChar((char) 0);
        panel4.add(labelName);
        panel4.add(inputName);

        JPanel panel5 = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 用于放置学号
        JLabel labelStudentID = new JLabel("学号：");
        labelStudentID.setBounds(100, 250, 80, 25);
        labelStudentID.setFont(new Font("微软雅黑", 1, 16));
        labelStudentID.setOpaque(false);

        inputStudentID = new JPasswordField(10);
        inputStudentID.setBounds(200, 250, 100, 25);
        inputStudentID.setBackground(null);
        inputStudentID.setEchoChar((char) 0);
        panel5.add(labelStudentID);
        panel5.add(inputStudentID);

        buttonRegister = new JButton("确认");
        buttonRegister.setBounds(200, 350, 60, 25);

        buttonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String account = inputAccount.getText();
                String password = new String(inputPassword.getPassword());
                String confirmPassword = new String(inputConfirmPassword.getPassword());
                String name = inputName.getText();
                String studentID = inputStudentID.getText();

                // Check if any of the fields is empty
                if (account.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || studentID.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterWindow.this, "请填写所有信息！", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterWindow.this, "密码不匹配，请重新输入！", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

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
            }
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
}
