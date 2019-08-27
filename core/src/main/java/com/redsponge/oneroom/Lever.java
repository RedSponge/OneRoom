package com.redsponge.oneroom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.LightTextures;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.ScreenEntity;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.Logger;

public class Lever extends ScreenEntity {

    private boolean on;

    private TextureRegion offTex;
    private TextureRegion onTex;

    private Texture hintX;

    private IntVector2 pos;

    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;

    private Player player;

    private boolean inverted;

    private IToggleable toggleable;
    private Runnable onToggle;
    private PlayerInteractDetector pid;

    private static Color tmpC = Color.WHITE.cpy();

    public Lever(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    public void setAttachedToggleable(IToggleable toggleable) {
        this.toggleable = toggleable;
    }

    public IntVector2 getPos() {
        return pos;
    }

    @Override
    public void added() {
        pos = new IntVector2();
        player = ((GameScreen)screen).getPlayer();
        pid = new PlayerInteractDetector(batch, shapeRenderer, player);
        pid.setRadius(32);
        pid.setOnInteract(this::toggle);
        screen.addEntity(pid);
    }

    @Override
    public void loadAssets() {
        TextureAtlas atlas = assets.get("textures", TextureAtlas.class);
        offTex = atlas.findRegion("lever/off");
        onTex = atlas.findRegion("lever/on");
        hintX = assets.get("hintPressX", Texture.class);
    }

    public void toggle() {
        on = !on;
        Logger.log(this, toggleable);
        if(toggleable != null) {
            Logger.log(this, inverted != on);
            toggleable.setEnabled(inverted != on);
        }
        if(onToggle != null) {
            onToggle.run();
        }
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

    @Override
    public void tick(float v) {
        pid.getPos().set(pos.x + WIDTH / 2, pos.y);
//        float playerDst = Vector2.dst2(pos.x + WIDTH / 2f, pos.y, player.getPos().x + Player.WIDTH / 2f, player.getPos().y);
//        isNearPlayer = playerDst < 16*16;
//        if(isNearPlayer) {
//            timeNearPlayer += v;
//            timeAwayFromPlayer = 0;
//        } else {
//            timeAwayFromPlayer += v;
//            timeNearPlayer = 0;
//        }
    }

    @Override
    public void render() {
        TextureRegion tex = on ? onTex : offTex;
        batch.draw(tex, pos.x, pos.y, WIDTH, HEIGHT);
    }

    @Override
    public void removed() {
        screen.removeEntity(pid);
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public void setOnToggle(Runnable onToggle) {
        this.onToggle = onToggle;
    }

    @Override
    public int getZ() {
        return -2;
    }
}
