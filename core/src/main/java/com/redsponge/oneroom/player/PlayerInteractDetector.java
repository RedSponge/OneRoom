package com.redsponge.oneroom.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.oneroom.Notifications;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.ScreenEntity;
import com.redsponge.redengine.utils.IntVector2;

public class PlayerInteractDetector extends ScreenEntity implements INotified {

    private Player player;
    private IntVector2 pos;
    private float radius;

    private float timeNearPlayer;
    private float timeAwayFromPlayer;
    private boolean nearPlayer;

    private Runnable onInteract;

    private float animationTime;
    private Texture hintTexture;

    private boolean active;

    public PlayerInteractDetector(SpriteBatch batch, ShapeRenderer shapeRenderer, Player player) {
        super(batch, shapeRenderer);
        this.player = player;
        this.pos = new IntVector2();
        this.animationTime = 1;
        this.active = true;
    }

    @Override
    public void loadAssets() {
        hintTexture = assets.get("hintPressX", Texture.class);
    }

    @Override
    public void tick(float v) {
        if(!active) return;
        nearPlayer = Vector2.dst2(player.getPos().x, player.getPos().y, pos.x, pos.y) < radius * radius;
        if(nearPlayer) {
            timeNearPlayer += v;
            timeAwayFromPlayer = 0;
        } else {
            timeNearPlayer = 0;
            timeAwayFromPlayer += v;
        }
    }

    @Override
    public void render() {
        if(!active) return;
        if(nearPlayer) {
            float progress = Interpolation.exp5.apply(Math.min(timeNearPlayer, animationTime)) * (1 / animationTime);
            float addY = progress * 32;
            batch.setColor(1, 1, 1, progress);
            batch.draw(hintTexture, pos.x - 8, pos.y + addY, 16, 16);
            batch.setColor(Color.WHITE);
        }
    }

    @Override
    public void removed() {

    }

    @Override
    public void notified(int i) {
        if(active && nearPlayer && i == Notifications.PLAYER_INTERACT) {
            if(onInteract != null) onInteract.run();
        }
    }

    public IntVector2 getPos() {
        return pos;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public Runnable getOnInteract() {
        return onInteract;
    }

    public void setOnInteract(Runnable onInteract) {
        this.onInteract = onInteract;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
