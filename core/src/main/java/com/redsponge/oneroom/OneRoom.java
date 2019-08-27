package com.redsponge.oneroom;

import com.redsponge.redengine.EngineGame;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class OneRoom extends EngineGame {
    @Override
    public void init() {
        setScreen(new GameScreen(ga));
    }
}