package com.redsponge.oneroom;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.atlas.AtlasAnimation;
import com.redsponge.redengine.assets.atlas.AtlasFrame;

public class GameAssets extends AssetSpecifier {

    public GameAssets(AssetManager am) {
        super(am);
    }

    @Asset("room/room_background.png")
    private Texture roomTexture;

    @Asset("lever/open_1.png")
    private Texture leverOnTexture;

    @Asset("lever/close.png")
    private Texture leverOffTexture;

    @Asset("textures/textures.atlas")
    private TextureAtlas textures;

    @AtlasFrame(frameName = "lever/on", atlas = "textures")
    private TextureRegion leverOn;

    @AtlasFrame(frameName = "lever/off", atlas = "textures")
    private TextureRegion leverOff;

    @Asset("hints/press_x.png")
    private Texture hintPressX;

    @Asset("player/player.png")
    private Texture player;

    @Asset("computer/computer_off.png")
    private Texture computerOff;

    @Asset("computer/computer_loading.png")
    private Texture computerLoading;

    @Asset("computer/computer_happy.png")
    private Texture computerHappy;

    @Asset("computer/computer_enter_password.png")
    private Texture computerEnterPassword;

    @Asset("room/iron.png")
    private Texture backgroundTile;
}
