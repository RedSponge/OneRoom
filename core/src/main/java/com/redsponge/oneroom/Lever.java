package com.redsponge.oneroom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class Lever extends ScreenEntity implements INotified {

    private boolean on;

    private TextureRegion offTex;
    private Animation<TextureRegion> onTex;

    private Texture hintX;

    private IntVector2 pos;

    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;

    private Player player;

    private float timeNearPlayer;
    private float timeAwayFromPlayer;
    private boolean isNearPlayer;

    private boolean inverted;

    private IToggleable toggleable;
    private Runnable onToggle;
    private float activeTime;

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
    }

    @Override
    public void loadAssets() {
        offTex = assets.getTextureRegion("leverOff");
        onTex = assets.getAnimation("leverOn");
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
        float playerDst = Vector2.dst2(pos.x + WIDTH / 2f, pos.y, player.getPos().x + Player.WIDTH / 2f, player.getPos().y);
        isNearPlayer = playerDst < 16*16;
        if(isNearPlayer) {
            timeNearPlayer += v;
            timeAwayFromPlayer = 0;
        } else {
            timeAwayFromPlayer += v;
            timeNearPlayer = 0;
        }
        if(on) {
            activeTime += v;
        } else {
            activeTime = 0;
        }
    }

    @Override
    public void render() {
        TextureRegion tex = on ? onTex.getKeyFrame(activeTime) : offTex;
        batch.draw(tex, pos.x, pos.y, WIDTH, HEIGHT);

        if(isNearPlayer) {
            float progress = Math.min(timeNearPlayer, .5f);
            float x = pos.x + WIDTH / 2f;
            float y = pos.y + HEIGHT / 2f;
            float w = 32;
            float h = 32;

            float a = Interpolation.exp5.apply(progress * 2);
            float addedY = Interpolation.sine.apply(progress * 2) * 10;

            tmpC.a = a;
            batch.setColor(tmpC);
            batch.draw(hintX, x - w / 2, y + addedY, w, h);
            batch.setColor(Color.WHITE);
        }
    }

    @Override
    public void notified(int i) {
        if(i == Notifications.PLAYER_INTERACT && isNearPlayer) {
            toggle();
        }
    }

    @Override
    public void removed() {

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
}
