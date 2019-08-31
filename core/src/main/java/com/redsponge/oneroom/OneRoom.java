package com.redsponge.oneroom;

import com.redsponge.oneroom.menu.MenuScreen;
import com.redsponge.redengine.EngineGame;
import com.redsponge.redengine.screen.splashscreen.SplashScreenScreen;
import com.redsponge.redengine.transitions.Transitions;

public class OneRoom extends EngineGame {
    @Override
    public void init() {
        discord.dispose();
        setScreen(new SplashScreenScreen(ga, new MenuScreen(ga), Transitions.linearFade(2, batch, shapeRenderer)));
    }
}