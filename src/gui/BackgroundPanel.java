package gui;

import engine.helper.Assets;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class BackgroundPanel extends JPanel {
    ImageIcon icon;
    final static String curDir = System.getProperty("user.dir");
    // img 是一个相对于当前工作目录的路径，指定了存放图像资源的文件夹
    // 通过拼接 img 和图像文件名，可以构建出完整的图像资源文件的路径
    final static String img = curDir + "/img/";
    Image temp;

    /**
     * 构造方法，创建游戏起始菜单界面。
     *
     * @throws IOException 当读取图像文件失败时抛出。*/
    public BackgroundPanel() throws IOException {
        String imageName = "background.jpg";
        // 从类路径中获取图像资源
        BufferedImage source = null;

        try {
            // class用于获取类的元数据，尝试从类路径中获取图像资源。如果获取失败，不抛出异常，而是捕获并忽略异常
            source = ImageIO.read(Objects.requireNonNull(Assets.class.getResourceAsStream(imageName)));
        } catch (Exception ignored) {
        }

        // 检查第一个尝试获取的source是否为null，如果为null，则第二次尝试从文件系统中读取图像资源
        if (source == null) {
            imageName = img + imageName;
            File file = new File(imageName);
            source = ImageIO.read(file);
        }

        // 创建 ImageIcon 对象
        icon = new ImageIcon(source);
        temp = icon.getImage();
    }

    /**
     * 重写 paintComponent 方法，绘制背景图像。
     *
     * @param g 绘制图像的画笔
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(temp, 0, 0, this.getWidth(), this.getHeight(), null);
    }
}
