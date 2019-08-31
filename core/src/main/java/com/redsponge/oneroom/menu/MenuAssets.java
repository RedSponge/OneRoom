package com.redsponge.oneroom.menu;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.atlas.AtlasFrame;

public class MenuAssets extends AssetSpecifier {

    public MenuAssets(AssetManager am) {
        super(am);
    }

    @Asset("textures/textures.atlas")
    private TextureAtlas textures;

    @AtlasFrame(atlas = "textures", frameName = "computer/computer_off")
    private TextureRegion computerOff;

    @Asset("room/iron.png")
    private Texture ironBackground;

    @AtlasFrame(atlas = "textures", frameName = "computer/computer_title")
    private TextureRegion computerMenu;

    @Asset("sounds/computer_talk.ogg")
    private Sound computerTalk;

    @Asset("ui/instructions.png")
    private Texture instructions;
}
