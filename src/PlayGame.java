import engine.core.MarioGame;
import engine.core.MarioLevelModel;
import engine.core.LevelGenerator;

public class PlayGame {
    /** 主程序入口 */
    public static void main(String[] args) {
        // 游戏主流程
        LevelGenerator generator = new LevelGenerator();
        // 获取String形式的地图
        String level = generator.getGeneratedLevel(new MarioLevelModel(300, 16));
        // 创建Mario游戏
        MarioGame game = new MarioGame();
        game.playGame(level, 200, 2);
    }
}
