package com.redsponge.oneroom;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.redsponge.redengine.input.SimpleInputTranslator;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.screen.ScreenEntity;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.Logger;

public class Player extends ScreenEntity {

    private PActor actor;
    private PhysicsWorld pWorld;

    private IntVector2 vel;
    private SimpleInputTranslator input;

    public Player(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        input = new SimpleInputTranslator();

        pWorld = ((GameScreen) screen).getPhysicsWorld();
        actor = new PActor(pWorld);
        pWorld.addActor(actor);

        actor.size.set(16, 16);
        actor.pos.set(100, 100);

        vel = new IntVector2();
    }

    @Override
    public void tick(float v) {
        vel.add(0, -5);
        vel.x = (int) (input.getHorizontal() * 200);

        if(input.isJustJumping()) {
            vel.y = 60;
        }

        actor.moveX(vel.x * v, null);
        actor.moveY(vel.y * v, () -> vel.y = 0);

        Vector3 camPos = ((GameScreen)screen).getCamPos();
        if(actor.pos.x >= GameScreen.WIDTH + GameScreen.WIDTH * 0.5f) {
            float dst = actor.pos.x - camPos.x;
            actor.pos.x = (int) (-GameScreen.WIDTH * 0.4f);
            camPos.x = actor.pos.x - dst;
            ((GameScreen)(screen)).changeToStageTwo();
        }
        if(actor.pos.x <= -GameScreen.WIDTH * 0.5f) {
            float dst = actor.pos.x - camPos.x;
            actor.pos.x = (int) (GameScreen.WIDTH + GameScreen.WIDTH * 0.4f);
            camPos.x = actor.pos.x - dst;
            ((GameScreen)(screen)).changeToStageTwo();
        }
    }

    @Override
    public void render() {

    }

    @Override
    public void removed() {

    }

    public IntVector2 getPos() {
        return actor.pos;
    }
}
