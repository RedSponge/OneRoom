package com.redsponge.oneroom.computer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.atlas.AtlasFrame;

public class ComputerPWScreenAssets extends AssetSpecifier {

    public ComputerPWScreenAssets(AssetManager am) {
        super(am);
    }

    @Asset("textures/textures.atlas")
    private TextureAtlas textures;

    @AtlasFrame(atlas = "textures", frameName = "computer/computer_enter_password_fullscreen")
    private Texture background;

    @Asset("sounds/computer_interact.ogg")
    private Sound interactSound;
}
