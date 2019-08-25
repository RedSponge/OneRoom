package com.redsponge.oneroom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.redsponge.redengine.utils.Logger;

public class PortalLink {

    private Vector2 first, second;
    private boolean active;

    private Vector3 camPos;

    private float radius;
    private Runnable onMoveThrough;

    public PortalLink(Vector2 first, Vector2 second, Vector3 camPos, float radius) {
        this.first = first;
        this.second = second;
        this.camPos = camPos;
        this.radius = radius;
    }

    private boolean inCircle(float x, float y, float cx, float cy, float cr) {
        return Vector2.dst2(x, y, cx, cy) <= cr * cr;
    }

    public void processPlayer(Player player) {
        boolean inFirst = inCircle(first.x, first.y, player.getPos().x + Player.WIDTH / 2f, player.getPos().y + Player.HEIGHT / 2f, radius);
        boolean inSecond = inCircle(second.x, second.y, player.getPos().x + Player.WIDTH / 2f, player.getPos().y + Player.HEIGHT / 2f, radius);
        if(inFirst || inSecond) {
            if(active) {
                return;
            }
            active = true;
            float cdstX = player.getPos().x - camPos.x;
            float cdstY = player.getPos().y - camPos.y;


            float dstX = second.x - first.x;
            float dstY = second.y - first.y;
            Logger.log(this, dstX, dstY);

            if(inSecond) {
                player.getPos().x -= dstX;
                player.getPos().y -= dstY;
            } else {
                player.getPos().x += dstX;
                player.getPos().y += dstY;
            }

            camPos.x = player.getPos().x - cdstX;
            camPos.y = player.getPos().y - cdstY;

            if(onMoveThrough != null) onMoveThrough.run();
        } else {
            active = false;
        }
    }

    public Vector2 getFirst() {
        return first;
    }

    public Vector2 getSecond() {
        return second;
    }

    public float getRadius() {
        return radius;
    }

    public void setOnMoveThrough(Runnable onMoveThrough) {
        this.onMoveThrough = onMoveThrough;
    }
}
