package com.redsponge.oneroom;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;

public class ComputerPWScreenAssets extends AssetSpecifier {

    public ComputerPWScreenAssets(AssetManager am) {
        super(am);
    }

    @Asset("textures/textures.atlas")
    private TextureAtlas textures;

    @Asset("computer/computer_enter_password_fullscreen.png")
    private Texture background;
}
