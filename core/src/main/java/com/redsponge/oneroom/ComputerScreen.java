package com.redsponge.oneroom;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.redengine.screen.ScreenEntity;

public class ComputerScreen extends ScreenEntity {

    private Texture off, loading, happy;
    private ComputerState state;

    private float timeOn;

    public ComputerScreen(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void loadAssets() {
        off = assets.get("computerOff", Texture.class);
        loading = assets.get("computerLoading", Texture.class);
        happy = assets.get("computerHappy", Texture.class);
    }

    @Override
    public void added() {
        state = ComputerState.OFF;
    }

    @Override
    public void tick(float v) {
        if(state != ComputerState.OFF) {
            timeOn += v;
        } else {
            timeOn = 0;
        }

        if(state == ComputerState.LOADING) {
            if(timeOn > 5) {
                state = ComputerState.HAPPY;
            }
        }
    }

    @Override
    public void render() {
        Texture t;
        switch(state) {
            case OFF:
                t = off;
                break;
            case LOADING:
                t = loading;
                break;
            case HAPPY:
                t = happy;
                break;
            default:
                throw new RuntimeException();
        }

        batch.draw(t, GameScreen.WIDTH / 2f - 64, GameScreen.HEIGHT / 3f * 2 - 48, 128, 96);
    }

    @Override
    public void removed() {

    }

    @Override
    public int getZ() {
        return -1;
    }

    public void setState(ComputerState state) {
        this.state = state;
    }

    public ComputerState getState() {
        return state;
    }
}
