package com.redsponge.oneroom.mechanics;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.oneroom.GameScreen;
import com.redsponge.oneroom.player.Player;
import com.redsponge.oneroom.player.PlayerInteractDetector;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.LightTextures;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.screen.ScreenEntity;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.Logger;

public class SelfDestructButton extends ScreenEntity {

    private TextureRegion texture;
    private IntVector2 pos;

    public final int WIDTH = 64;
    public final int HEIGHT = 64;

    private PlayerInteractDetector pid;

    private PointLight light;
    private Player player;

    private Sound blipSound;

    public SelfDestructButton(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        pid = new PlayerInteractDetector(batch, shapeRenderer, ((GameScreen)screen).getPlayer());
        pos = new IntVector2();
        screen.addEntity(pid);
        pid.setActive(true);
        texture = assets.getTextureRegion("selfDestructButton");
        pid.setRadius(40);
        pid.setOnInteract(() -> {
            blipSound.play();
            ((GameScreen) screen).playEndAnimation();
        });

        light = new PointLight(pos.x, pos.y, 100, LightTextures.getInstance().featheredPointLight);
        light.getColor().set(0.3f, 0, 0, 1.0f);
        screen.getSystem(LightSystem.class).addLight(light, LightType.ADDITIVE);
        player = ((GameScreen) screen).getPlayer();
    }

    @Override
    public void loadAssets() {
        blipSound = assets.get("computerInteract", Sound.class);
    }

    @Override
    public void tick(float v) {
        pid.getPos().set(pos.x + WIDTH / 2, pos.y);
        light.pos.set(pos.x + WIDTH / 2f, pos.y);
    }

    @Override
    public void render() {
        batch.draw(texture, pos.x, pos.y - HEIGHT / 2f, WIDTH, HEIGHT);
    }

    @Override
    public void removed() {
        pid.remove();
    }

    @Override
    public int getZ() {
        return -3;
    }

    public IntVector2 getPos() {
        return pos;
    }
}
