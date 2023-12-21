package engine.core;

public class MarioForwardModel {
    private static final int OBS_SCENE_SHIFT = 16;

    // Common between scene detail level 0 and scene detail level 1
    public static final int OBS_BRICK = OBS_SCENE_SHIFT + 6;
    public static final int OBS_QUESTION_BLOCK = OBS_SCENE_SHIFT + 8;

    private final MarioWorld world;

    // stats

    /**
     * Create a forward model object
     *
     * @param world the current level world that is being used. This class hides the
     *              world object so the agents won't cheat.
     */
    public MarioForwardModel(MarioWorld world) {
        this.world = world;
    }
}
