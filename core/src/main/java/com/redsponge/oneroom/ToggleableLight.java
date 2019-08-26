package com.redsponge.oneroom;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.PointLight;

public class ToggleableLight extends PointLight {

    private boolean active;

    public ToggleableLight(float x, float y, float radius, Texture texture) {
        super(x, y, radius, texture);
    }

    @Override
    public void render(LightSystem ls, SpriteBatch batch, Viewport viewport) {
        if(active) super.render(ls, batch, viewport);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
