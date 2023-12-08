package engine.core;

public class MarioForwardModel {
    private static final int OBS_SCENE_SHIFT = 16;

    // Common between scene detail level 0 and scene detail level 1
    public static final int OBS_BRICK = OBS_SCENE_SHIFT + 6;
    public static final int OBS_QUESTION_BLOCK = OBS_SCENE_SHIFT + 8;

    private MarioWorld world;

    // stats
    private int fallKill;
    private int stompKill;
    private int fireKill;
    private int shellKill;
    private int mushrooms;
    private int flowers;
    private int breakBlock;

    /**
     * Create a forward model object
     *
     * @param world the current level world that is being used. This class hides the
     *              world object so the agents won't cheat.
     */
    public MarioForwardModel(MarioWorld world) {
        this.world = world;
    }

    /**
     * Create a clone from the current forward model state
     *
     * @return a clone from the current forward model state
     */
    //public MarioForwardModel clone() {
    //    MarioForwardModel model = new MarioForwardModel(this.world.clone());
    //    model.fallKill = this.fallKill;
    //    model.stompKill = this.stompKill;
    //    model.fireKill = this.fireKill;
    //    model.shellKill = this.shellKill;
    //    model.mushrooms = this.mushrooms;
    //    model.flowers = this.flowers;
    //    model.breakBlock = this.breakBlock;
    //    return model;
    //}
}
