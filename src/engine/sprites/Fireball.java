package engine.sprites;

import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.SpriteType;

public class Fireball extends MarioSprite {

    private static final float GROUND_INERTIA = 0.89f;
    private static final float AIR_INERTIA = 0.89f;

    private int anim = 0;

    public Fireball(boolean visuals, float x, float y, int facing) {
        super(x, y, SpriteType.FIREBALL);
        this.facing = facing;
        this.ya = 4;
        this.width = 4;
        this.height = 8;

        if (visuals) {
            this.graphics = new MarioImage(Assets.particles, 24);
            this.graphics.originX = 8;
            this.graphics.originY = 8;
            this.graphics.width = 16;
            this.graphics.height = 16;
        }
    }

    @Override
    public void update() {
        if (!this.alive) {
            return;
        }

        if (facing != 0) {
            anim++;
        }

        float sideWaysSpeed = 8f;
        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }
        xa = facing * sideWaysSpeed;

        world.checkFireballCollide(this);

        if (!move(xa, 0)) {
            this.world.removeSprite(this);
            return;
        }

        onGround = false;
        move(0, ya);
        if (onGround) {
            ya = -10;
        }

        ya *= 0.95f;
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        if (!onGround) {
            ya += 1.5;
        }

        if (this.graphics != null) {
            this.graphics.flipX = facing == -1;
            this.graphics.index = 24 + anim % 4;
        }
    }

}
