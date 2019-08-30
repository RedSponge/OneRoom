package com.redsponge.oneroom.player;

import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PSolid;
import com.redsponge.redengine.physics.PhysicsWorld;

public class GravityFlipActor extends PActor {

    private boolean gravityInverted;

    public GravityFlipActor(PhysicsWorld worldIn) {
        super(worldIn);
    }

    @Override
    public boolean isRiding(PSolid solid) {
        return solid.pos.y + solid.size.y == this.pos.y && !gravityInverted || solid.pos.y - 1 == this.pos.y && gravityInverted;
    }

    public boolean isGravityInverted() {
        return gravityInverted;
    }

    public void setGravityInverted(boolean gravityInverted) {
        this.gravityInverted = gravityInverted;
    }
}
