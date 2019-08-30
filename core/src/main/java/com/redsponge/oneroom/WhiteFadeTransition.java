package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.redsponge.redengine.render.util.ScreenFiller;
import com.redsponge.redengine.transitions.FadeTransition;

public class WhiteFadeTransition extends FadeTransition {

    public WhiteFadeTransition(Interpolation interFrom, Interpolation interTo, SpriteBatch batch, ShapeRenderer shapeRenderer, float length) {
        super(interFrom, interTo, batch, shapeRenderer, length);
    }

    @Override
    public void render(float time) {
        float progress = this.getProgress(time, true);
        ScreenFiller.fillScreen(shapeRenderer, 1, 1, 1, progress);
    }
}
