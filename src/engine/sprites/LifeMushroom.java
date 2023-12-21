package engine.sprites;

import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.EventType;
import engine.helper.SpriteType;

public class LifeMushroom extends MarioSprite {
    private int life;

    private static final float GROUND_INERTIA = 0.89f;
    private static final float AIR_INERTIA = 0.89f;

    public LifeMushroom(boolean visuals, float x, float y) {
        super(x, y, SpriteType.LIFE_MUSHROOM);
        this.width = 4;
        this.height = 12;
        this.facing = 1;
        this.life = 0;

        if (visuals) {
            this.graphics = new MarioImage(Assets.items, 3);
            this.graphics.width = 16;
            this.graphics.height = 16;
            this.graphics.originX = 8;
            this.graphics.originY = 15;
        }
    }

    @Override
    public void collideCheck() {
        if (!this.alive) {
            return;
        }

        float xMarioD = world.mario.x - x;
        float yMarioD = world.mario.y - y;
        if (xMarioD > -16 && xMarioD < 16) {
            if (yMarioD > -height && yMarioD < world.mario.height) {
                world.addEvent(EventType.COLLECT, this.type.getValue());
                world.mario.collect1Up();
                world.removeSprite(this);
            }
        }
    }

    @Override
    public void update() {
        if (!this.alive) {
            return;
        }

        if (life < 9) {
            y--;
            life++;
            return;
        }
        float sideWaysSpeed = 1.75f;
        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }

        xa = facing * sideWaysSpeed;

        if (!move(xa, 0)) {
            facing = -facing;
        }
        onGround = false;
        move(0, ya);

        ya *= 0.85f;
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        if (!onGround) {
            ya += 2;
        }
    }

}
