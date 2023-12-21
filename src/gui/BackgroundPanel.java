package gui;

import engine.helper.Assets;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BackgroundPanel extends JPanel {
    ImageIcon icon;
    final static String curDir = System.getProperty("user.dir");
    //img 是一个相对于当前工作目录的路径，指定了存放图像资源的文件夹
    //通过拼接 img 和图像文件名，可以构建出完整的图像资源文件的路径
    final static String img = curDir + "/img/";
    Image temp;

    public BackgroundPanel() throws IOException {
        icon = new ImageIcon(getBufferedImage("background.jpg"));
        temp = icon.getImage();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(temp, 0, 0, this.getWidth(), this.getHeight(), null);
    }

    private BufferedImage getBufferedImage(String imageName) throws IOException {
        BufferedImage source = null;
        // 创建 ImageIcon 对象
        try {
            //class用于获取类的元数据，尝试从类路径中获取图像资源。如果获取失败，不抛出异常，而是捕获并忽略异常
            source = ImageIO.read(Assets.class.getResourceAsStream(imageName));
        } catch (Exception e) {
        }
        //检查第一个尝试获取的source是否为null，如果为null，则第二次尝试从文件系统中读取图像资源
        if (source == null) {
            imageName = img + imageName;
            File file = new File(imageName);
            source = ImageIO.read(file);
        }
        return source;
    }

}
