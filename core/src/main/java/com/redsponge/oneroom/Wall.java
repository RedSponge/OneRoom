package com.redsponge.oneroom;

import com.redsponge.redengine.physics.PSolid;
import com.redsponge.redengine.physics.PhysicsWorld;

public class Wall extends PSolid {

    public Wall(PhysicsWorld worldIn, int x, int y, int width, int height) {
        super(worldIn);
        pos.set(x, y);
        size.set(width, height);
    }

    @Override
    public void remove() {
        super.remove();
    }
}
