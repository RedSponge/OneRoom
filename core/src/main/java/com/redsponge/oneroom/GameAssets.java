package com.redsponge.oneroom;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;

public class GameAssets extends AssetSpecifier {

    public GameAssets(AssetManager am) {
        super(am);
    }

    @Asset("room/room_background.png")
    private Texture roomTexture;

    @Asset("lever/on.png")
    private Texture leverOnTexture;

    @Asset("lever/off.png")
    private Texture leverOffTexture;

    @Asset("hints/press_x.png")
    private Texture hintPressX;

    @Asset("player/player.png")
    private Texture player;
}
