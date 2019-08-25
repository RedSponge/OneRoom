package com.redsponge.oneroom;

import com.redsponge.redengine.physics.PEntity;
import com.redsponge.redengine.physics.PSolid;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.utils.Logger;

import java.lang.reflect.Field;

public class Wall extends PSolid implements IToggleable {

    private boolean enabled;

    public Wall(PhysicsWorld worldIn, int x, int y, int width, int height) {
        super(worldIn);
        pos.set(x, y);
        size.set(width, height);
        enabled = true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(enabled != this.enabled) {
            if(enabled) {
                try {
                    Field removed = PEntity.class.getDeclaredField("removed");
                    removed.setAccessible(true);
                    removed.set(this, false);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                worldIn.addSolid(this);
                Logger.log(this, "ADDED SELF");
            } else {
                remove();
            }
        }

        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void remove() {
        super.remove();
    }
}
