package com.redsponge.oneroom;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.atlas.AtlasAnimation;
import com.redsponge.redengine.assets.atlas.AtlasFrame;

public class TrollEndAssets extends AssetSpecifier {

    public TrollEndAssets(AssetManager am) {
        super(am);
    }

    @Asset("textures/textures.atlas")
    private TextureAtlas inGameTextures;

    @AtlasFrame(frameName = "computer/computer_happy", atlas = "inGameTextures")
    private TextureRegion computerHappy;

    @AtlasAnimation(animationName = "computer/computer_happy_talking", atlas = "inGameTextures", length = 2, frameDuration = 0.3f)
    private Animation<TextureRegion> computerHappyTalk;

    @AtlasFrame(frameName = "computer/textbox", atlas = "inGameTextures")
    private TextureRegion computerTextBox;

    @AtlasFrame(frameName = "computer/computer_off", atlas = "inGameTextures")
    private TextureRegion computerOff;

    @AtlasFrame(atlas = "inGameTextures", frameName = "computer/computer_title")
    private TextureRegion computerMenu;

    @Asset("room/iron.png")
    private Texture backgroundIron;

    @Asset("end/destroyed_room.png")
    private Texture destroyedLab;

    @Asset("end/destroyed_with_player.png")
    private Texture destroyedLabWithPlayer;

    @Asset("sounds/computer_talk.ogg")
    private Sound computerTalk;

    @Asset("sounds/come_out_of_ruins.ogg")
    private Sound comeOutOfRuins;

    @Asset("ui/instructions_magic.png")
    private Texture instructions;

    @Asset("music/very_sad.ogg")
    private Music music;

    @Asset("music/menu.ogg")
    private Music menuMusic;

}
