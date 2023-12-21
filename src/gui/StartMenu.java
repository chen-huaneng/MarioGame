package gui;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.im.InputContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import engine.core.LevelGenerator;
import engine.core.MarioGame;
import engine.core.MarioLevelModel;
import engine.helper.Assets;

/**
 * StartMenu 类用于创建游戏的起始菜单界面。
 * 它扩展了 JFrame 类并实现了 KeyListener 接口，用于处理键盘事件。
 */
public class StartMenu extends JFrame implements KeyListener{
	private JLabel menuLabel;
    private ImageIcon option1,option2, option3, introduce, about,timeout,gameover,win;
    private int currentOption = 0;
    private boolean isMainScreen = true;  // 用于标记是否处于主菜单
    public static boolean start = false;
    private final double scale;  // 窗口尺寸倍数
    final static String curDir = System.getProperty("user.dir");
    //img 是一个相对于当前工作目录的路径，指定了存放图像资源的文件夹
    //通过拼接 img 和图像文件名，可以构建出完整的图像资源文件的路径
    final static String img = curDir + "/img/";
    
    /**
     * 构造方法，创建游戏起始菜单界面。
     * 
     * @param scale 窗口尺寸倍数。
     * @throws IOException 当读取图像文件失败时抛出。
     */
    public StartMenu(double scale,int status) throws IOException {
    	
    	this.scale = scale;  // 设置窗口尺寸倍数
    	
    	
    	option1 = new ImageIcon(getBufferedImage("Selection1.png"));
    	option2 = new ImageIcon(getBufferedImage("Selection2.png"));
    	 option3 = new ImageIcon(getBufferedImage("Selection3.png"));
         introduce = new ImageIcon(getBufferedImage("Introduce.png"));
         about = new ImageIcon(getBufferedImage("About.png"));
         timeout = new ImageIcon(getBufferedImage("TimeOut.png"));
         gameover=new ImageIcon(getBufferedImage("GameOver.png"));
         win=new ImageIcon(getBufferedImage("Win.png"));
         menuLabel = new JLabel(option1);
         
         
         

        // 添加组件到 JFrame
        add(menuLabel);
        // 设置默认选项
        if(status==0) menuLabel = new JLabel(option1);
        if(status==1) {
        	isMainScreen=false;
        	menuLabel = new JLabel(timeout);
        }
        if(status==2) {
        	isMainScreen=false;
        	menuLabel = new JLabel(gameover);
        }
        if(status==3) {
        	isMainScreen=false;
        	menuLabel = new JLabel(win);
        }

        // 添加组件到 JFrame
        add(menuLabel);
        setFocusable(true);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);

     // 设置窗口属性
        setTitle("游戏准备界面");
        // 设置窗口的大小为原始尺寸乘以倍数
        // 使用 Math.round 确保得到整数
        int scaledWidth = (int) Math.round(256 * scale);
        int scaledHeight = (int) Math.round(256 * scale);
        setSize(scaledWidth, scaledHeight);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
       
        setVisible(true);
    }

    /**
     * 从图像文件获取 BufferedImage 对象。
     * 
     * @param imageName 图像文件名。
     * @return BufferedImage 对象。
     * @throws IOException 当读取图像文件失败时抛出。
     */
	private BufferedImage getBufferedImage(String imageName) throws IOException  {
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
	

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
     * 内部类，用于启动游戏。
     * 继承 SwingWorker，以异步方式运行游戏。
     */
	private class GameStarter extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            LevelGenerator generator = new LevelGenerator();
            String level = generator.getGeneratedLevel(new MarioLevelModel(150, 16));
            MarioGame game = new MarioGame();
            game.playGame(level, 200, 2);
            return null;
        }
    }

	@Override
	 public void keyPressed(KeyEvent e) {
        if (isMainScreen) {
            // 主菜单界面的键盘逻辑
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                currentOption = (currentOption + 1) % 3;
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                currentOption = (currentOption - 1 + 3) % 3;
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            	SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        switch (currentOption) {
                            case 0:  // 开始游戏
                                isMainScreen = false;
                                start = true;
                                dispose();
                                new GameStarter().execute();
                                break;
                            case 1: // 游戏介绍
                                menuLabel.setIcon(introduce);
                                isMainScreen = false;
                                break;
                            case 2: // 关于我们
                                menuLabel.setIcon(about);
                                isMainScreen = false;
                                break;
                        }
                        revalidate();
                        repaint();
                    }
                });
            }else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            	int confirmed = JOptionPane.showConfirmDialog(null, 
                        "确认退出游戏？", "退出确认",
                        JOptionPane.YES_NO_OPTION);

                // Check user's decision
                if (confirmed == JOptionPane.YES_OPTION) {
                    dispose(); // Dispose the frame
                }
            }
            updateMenu();
        } else {
            // 游戏介绍或关于我们界面的键盘逻辑
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                isMainScreen = true;
                updateMenu();
            }
        }
    }



	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
     * 更新菜单显示的方法。
     * 根据当前选项更改菜单显示的图标。
     */
    private void updateMenu() {
        switch (currentOption) {
            case 0:
                menuLabel.setIcon(option1);
                break;
            case 1:
                menuLabel.setIcon(option2);
                break;
            case 2:
                menuLabel.setIcon(option3);
                break;
        }
        revalidate();
        repaint();
    }
    
}
