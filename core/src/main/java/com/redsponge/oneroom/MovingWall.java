package com.redsponge.oneroom;

import com.badlogic.gdx.math.MathUtils;
import com.redsponge.redengine.physics.IUpdated;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.Logger;

public class MovingWall extends Wall implements IUpdated {

    public MovingWall(PhysicsWorld worldIn, int x, int y, int width, int height) {
        super(worldIn, x, y, width, height);
        speed = MathUtils.random(5, 10);
    }

    private float timeActive;
    private float speed;
    private boolean active;
    private IntVector2 initialPos;

    public void setInitialPos(IntVector2 initialPos) {
        this.initialPos = initialPos;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void update(float v) {
        if(!active) return;
        timeActive += v;
//        Logger.log(this, timeActive, Math.sin(timeActive), speed);
        move(0, (float) (Math.sin(timeActive * speed) * 200)* v);
//        Logger.log(this, pos);
//        move(0, 1);
    }
}
