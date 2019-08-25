package com.redsponge.oneroom;

import com.badlogic.gdx.ApplicationAdapter;
import com.redsponge.redengine.EngineGame;
import com.redsponge.redengine.screen.DefaultScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class OneRoom extends EngineGame {
    @Override
    public void init() {
        setScreen(new GameScreen(ga));
    }
}