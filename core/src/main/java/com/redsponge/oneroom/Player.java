package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.redsponge.redengine.input.SimpleInputTranslator;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.LightTextures;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.screen.ScreenEntity;
import com.redsponge.redengine.utils.IntVector2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    private boolean isJumping;
    private float jumpTime;
    private float minJumpTime = 0.05f;
    private float maxJumpTime = 0.3f;

    private boolean onGround;

    private Method collideFirst;

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
        try {
            collideFirst = actor.getClass().getDeclaredMethod("collideFirst", IntVector2.class);
            collideFirst.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAssets() {
        texture = assets.get("player", Texture.class);
        tr = new TextureRegion(texture);
    }

    IntVector2 tmpPos = new IntVector2();

    @Override
    public void tick(float v) {
        try {
            onGround = collideFirst.invoke(actor, tmpPos.set(actor.pos).add(0, -1)) != null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        progressJump(v);
        if(isJumping) {
            float jumpProgress = 1 - Interpolation.circleOut.apply(0, maxJumpTime, jumpTime);
            vel.y = (int) (200 * jumpProgress);
        } else {
            vel.add(0, -20);
        }

        vel.x = (int) (input.getHorizontal() * 200);
        if(input.getHorizontal() != 0) {
            boolean left = input.getHorizontal() < 0;
            if(left != this.left) notifyScreen(Notifications.PLAYER_TURN);
            this.left = left;
        }

        if(Gdx.input.isKeyJustPressed(Keys.X)) {
            notifyScreen(Notifications.PLAYER_INTERACT);
        }

        if(vel.y < -500) vel.y = -500;

        actor.moveX(vel.x * v, null);
        actor.moveY(vel.y * v, () -> {
            if(vel.y > 0) isJumping = false;
            vel.y = 0;
        });
    }

    private void progressJump(float delta) {
        boolean jumpPressed = input.isJumping();
        if(isJumping) {
            jumpTime += delta;
            if(jumpTime > minJumpTime && !jumpPressed || jumpTime > maxJumpTime) {
                isJumping = false;
            }
        } else {
            if(jumpPressed && onGround) {
                isJumping = true;
                onGround = false;
                jumpTime = 0;
            }
        }
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
