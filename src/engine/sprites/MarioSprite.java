package engine.sprites;

import java.awt.*;

import engine.core.MarioWorld;
import engine.helper.SpriteType;

public abstract class MarioSprite {
    public SpriteType type;

    public String initialCode;
    public float x, y, xa, ya;
    public int width, height, facing;
    public boolean alive;
    public MarioWorld world;

    public MarioSprite(float x, float y, SpriteType type) {
        this.initialCode = "";
        this.x = x;
        this.y = y;
        this.xa = 0;
        this.ya = 0;
        this.facing = 1;
        this.alive = true;
        this.world = null;
        this.width = 16;
        this.height = 16;
        this.type = type;
    }

    public MarioSprite clone() {
        return null;
    }

    public void added() {

    }

    public void removed() {

    }

    public void render(Graphics og) {

    }

    public void update() {

    }

    public void collideCheck() {
    }

    public void bumpCheck(int xTile, int yTile) {
    }

    public boolean shellCollideCheck(Shell shell) {
        return false;
    }

    public boolean fireballCollideCheck(Fireball fireball) {
        return false;
    }
}
