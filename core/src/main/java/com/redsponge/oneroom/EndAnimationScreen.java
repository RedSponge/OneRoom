package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.render.util.ScreenFiller;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.FadeTransition;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;
import com.redsponge.redengine.utils.Logger;

public class EndAnimationScreen extends AbstractScreen {
    private Music endMusic;

    private Animation<TextureRegion> animation;
    private float timeRunning;
    private FitViewport viewport;
    private static final int WIDTH = 96;
    private static final int HEIGHT = 54;

    public EndAnimationScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        endMusic = Gdx.audio.newMusic(Gdx.files.internal("music/ka_boom.ogg"));
        endMusic.setOnCompletionListener((m) -> {
            ga.transitionTo(new TrollEndRoom(ga), Transitions.linearFade(4, batch, shapeRenderer));
        });
        animation = assets.getAnimation("endAnimation");

        viewport = new FitViewport(WIDTH, HEIGHT);
        endMusic.play();
    }

    @Override
    public void tick(float v) {
        timeRunning += v;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(animation.getKeyFrame(timeRunning), 0, 0);
        batch.end();

        float progress = timeRunning < 20 ? 0 : timeRunning > 22 ? 1 : (timeRunning - 20) / 2;
        ScreenFiller.fillScreen(shapeRenderer, 1, 1, 1, progress);
    }

    @Override
    public void dispose() {
        endMusic.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return EndAnimationAssets.class;
    }
}
