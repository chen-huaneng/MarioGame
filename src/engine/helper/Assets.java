package engine.helper;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;


public class Assets {
    // 普通Mario贴图数组
    public static Image[][] mario;
    // 小个的Mario贴图数组
    public static Image[][] smallMario;
    // 火球Mario贴图数组
    public static Image[][] fireMario;
    public static Image[][] enemies;
    public static Image[][] items;
    public static Image[][] level;
    public static Image[][] particles;
    public static Image[][] font;
    // 当前图片的路径位置，获取当前工作目录的路径
    final static String img = System.getProperty("user.dir") + "/img/";

    /**
     * 获取游戏中相应的图像
     *
     * @param gc 对象的图形配置
     */
    public static void init(GraphicsConfiguration gc) {
        // 处理异常，如果读取文件发生异常则弹出调用栈
        try {
            mario = cutImage(gc, "mariosheet.png", 32, 32);
            smallMario = cutImage(gc, "smallmariosheet.png", 16, 16);
            fireMario = cutImage(gc, "firemariosheet.png", 32, 32);
            enemies = cutImage(gc, "enemysheet.png", 16, 32);
            items = cutImage(gc, "itemsheet.png", 16, 16);
            level = cutImage(gc, "mapsheet.png", 16, 16);
            particles = cutImage(gc, "particlesheet.png", 16, 16);
            font = cutImage(gc, "font.gif", 8, 8);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取并绘制图像到兼容图像上
     *
     * @param gc 对象的图形配置
     * @param imageName 图像的名称
     * @return 返回读取的文件
     * @throws IOException 抛出读取文件异常
     */
    private static Image getImage(GraphicsConfiguration gc, String imageName) throws IOException {
        // 将图像文件名和一个路径前缀拼接
        imageName = img + imageName;
        // 创建一个拼接后的图像文件路径
        File file = new File(imageName);
        // 从文件系统中读取图像资源
        // source用于存储图像的原始数据
        BufferedImage source = ImageIO.read(file);

        // 创建兼容的图像
        Image image = gc.createCompatibleImage(source.getWidth(), source.getHeight(), Transparency.BITMASK);
        // 获取图像的 Graphics2D 对象，用于进行图形绘制操作
        Graphics2D g = (Graphics2D) image.getGraphics();
        // 设置合成模式
        g.setComposite(AlphaComposite.Src);
        // 将原始图像绘制到兼容图像上
        g.drawImage(source, 0, 0, null);
        // 释放图像的系统资源
        g.dispose();

        return image;
    }

    /**
     * 将图像切割成相应的小图片
     *
     * @param gc 对象的图形配置
     * @param imageName 图像的名称
     * @param xSize 图像的切割宽度
     * @param ySize 图像的切割长度
     * @return 切割完之后的图像
     * @throws IOException 抛出读取文件异常
     */
    private static Image[][] cutImage(GraphicsConfiguration gc, String imageName, int xSize, int ySize) throws IOException {
        // 获取原始的图像
        Image source = getImage(gc, imageName);
        // 用于存储切割后的图像，数组的大小由图像的大小和指定的切割大小决定
        Image[][] images = new Image[source.getWidth(null) / xSize][source.getHeight(null) / ySize];
        // 遍历循环图像的宽度，进行横向切割
        for (int x = 0; x < source.getWidth(null) / xSize; ++x) {
            // 遍历循环图像的长度，进行纵向切割
            for (int y = 0; y < source.getHeight(null) / ySize; ++y) {
                // 为每一小块图像创建一个兼容的图像对象
                Image image = gc.createCompatibleImage(xSize, ySize, Transparency.BITMASK);
                // 获取小块的图像，用于绘制操作
                Graphics2D g = (Graphics2D) image.getGraphics();
                // 设置合成模式
                g.setComposite(AlphaComposite.Src);
                // 将原始图像绘制到小块图像上，通过调整绘制的位置实现切割
                g.drawImage(source, -x * xSize, -y * ySize, null);
                // 释放图形的系统资源
                g.dispose();
                // 将处理后的图像存储到相应的位置
                images[x][y] = image;
            }
        }

        return images;
    }
}
