package com.redsponge.oneroom;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.redengine.screen.ScreenEntity;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;

public class Computer extends ScreenEntity {

    private Texture off, loading, enterPassword, happy;
    private ComputerState state;

    private PlayerInteractDetector pio;

    private GameAccessor ga;
    private ComputerPasswordScreen pwScreen;
    private float timeOn;

    public Computer(SpriteBatch batch, ShapeRenderer shapeRenderer, GameAccessor ga) {
        super(batch, shapeRenderer);
        this.ga = ga;
    }

    @Override
    public void loadAssets() {
        off = assets.get("computerOff", Texture.class);
        loading = assets.get("computerLoading", Texture.class);
        enterPassword = assets.get("computerEnterPassword", Texture.class);
        happy = assets.get("computerHappy", Texture.class);
    }

    @Override
    public void added() {
        state = ComputerState.OFF;
        pwScreen = new ComputerPasswordScreen(ga, (GameScreen) screen);
        pio = new PlayerInteractDetector(batch, shapeRenderer, ((GameScreen)screen).getPlayer());
        pio.getPos().set(GameScreen.WIDTH / 2, GameScreen.HEIGHT / 2);
        pio.setRadius(48);
        pio.setOnInteract(this::requirePassword);

        screen.addEntity(pio);
    }

    private void requirePassword() {
        ga.transitionTo(pwScreen, Transitions.linearFade(1, batch, shapeRenderer));
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
                state = ComputerState.ENTER_PASSWORD;
            }
        }

        pio.setActive(state == ComputerState.ENTER_PASSWORD);
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
            case ENTER_PASSWORD:
                t = enterPassword;
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
