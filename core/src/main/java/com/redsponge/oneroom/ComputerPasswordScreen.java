package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ComputerPasswordScreen extends AbstractScreen implements InputProcessor {

    private TextureRegion[] font;
    private int[] input;
    private int selector;

    private Texture background;
    private FitViewport viewport;

    private GameScreen gs;

    public ComputerPasswordScreen(GameAccessor ga, GameScreen gs) {
        super(ga);
        this.gs = gs;
    }

    @Override
    public void show() {
        font = NumberPixelFont.getNumbers(assets);
        viewport = new FitViewport(64, 36);
        background = assets.get("background", Texture.class);
        input = new int[4];
        selector = 0;

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void tick(float v) {
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        for (int i = 0; i < input.length; i++) {
            batch.setColor(i == selector ? Color.WHITE : Color.GREEN);
            batch.draw(font[input[i]], 12 + 12 * i, 8);
        }
        batch.setColor(Color.WHITE);
        batch.end();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        NumberPixelFont.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return ComputerPWScreenAssets.class;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        if(c >= '0' && c <= '9') {
            input[selector++] = c - '0';
            selector %= input.length;
        }

        if(c == 'x' || c == 'X') {
            testAndExit();
        }
        return false;
    }

    private void testAndExit() {
        if(String.join("", Arrays.stream(input).mapToObj(i -> ("" + i)).toArray(String[]::new)).equals("4173")) {
            gs.computerPasswordIsCorrect();
        }
        ga.transitionTo(gs, Transitions.linearFade(1, batch, shapeRenderer));
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}
