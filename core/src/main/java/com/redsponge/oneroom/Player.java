package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.redsponge.redengine.input.SimpleInputTranslator;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.screen.ScreenEntity;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.Logger;

public class Player extends ScreenEntity {

    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    private PActor actor;
    private PhysicsWorld pWorld;

    private IntVector2 vel;
    private SimpleInputTranslator input;

    private Texture texture;
    private TextureRegion tr;
    private boolean left;

    public Player(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        input = new SimpleInputTranslator();

        pWorld = ((GameScreen) screen).getPhysicsWorld();
        actor = new PActor(pWorld);
        pWorld.addActor(actor);

        actor.size.set(WIDTH, HEIGHT);
        actor.pos.set(100, 100);

        vel = new IntVector2();
    }

    @Override
    public void loadAssets() {
        texture = assets.get("player", Texture.class);
        tr = new TextureRegion(texture);
    }

    @Override
    public void tick(float v) {
        vel.add(0, -5);
        vel.x = (int) (input.getHorizontal() * 200);
        if(input.getHorizontal() != 0) {
            left = input.getHorizontal() < 0;
        }

        if(input.isJustJumping()) {
            vel.y = 60;
        }
        if(Gdx.input.isKeyJustPressed(Keys.X)) {
            notifyScreen(Notifications.PLAYER_INTERACT);
        }

        if(vel.y < -500) vel.y = -500;

        actor.moveX(vel.x * v, null);
        actor.moveY(vel.y * v, () -> vel.y = 0);
    }

    @Override
    public void render() {
        tr.flip(left, false);
        batch.draw(tr, actor.pos.x, actor.pos.y, actor.size.x, actor.size.y);
        tr.flip(left, false);
    }

    @Override
    public void removed() {

    }

    public IntVector2 getPos() {
        return actor.pos;
    }
}
