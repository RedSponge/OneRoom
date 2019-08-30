package com.redsponge.oneroom;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
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


    @AtlasFrame(atlas = "textures", frameName = "computer/computer_off")
    private TextureAtlas computerOff;

    @AtlasFrame(atlas = "textures", frameName = "computer/computer_loading")
    private Texture computerLoading;

    @AtlasFrame(atlas = "textures", frameName = "computer/computer_happy")
    private Texture computerHappy;

    @AtlasAnimation(atlas = "textures", animationName = "computer/computer_happy_talking", length = 2, frameDuration = 0.3f)
    private Animation<TextureRegion> computerHappyTalk;

    @AtlasAnimation(atlas = "textures", animationName = "computer/computer_happy_laugh", length = 2, frameDuration = 0.3f)
    private Animation<TextureRegion> computerHappyLaugh;

    @AtlasFrame(atlas = "textures", frameName = "computer/computer_passthrough_hint")
    private TextureRegion computerMovethroughHint;

    @AtlasFrame(atlas = "textures", frameName = "computer/computer_angry")
    private TextureRegion computerAngry;

    @AtlasAnimation(atlas = "textures", animationName = "computer/computer_angry_talk", length = 2, frameDuration = 0.3f)
    private Animation<TextureRegion> computerAngryTalk;

    @AtlasFrame(atlas = "textures", frameName = "computer/computer_enter_password")
    private Texture computerEnterPassword;

    @AtlasFrame(atlas = "textures", frameName = "computer/textbox")
    private Texture computerTextBox;

    @Asset("room/iron.png")
    private Texture backgroundTile;

    @AtlasFrame(atlas = "textures", frameName = "lever/self_destruct")
    private TextureRegion selfDestructButton;

    @Asset("sounds/lever_toggle.ogg")
    private Sound leverToggle;

    @Asset("sounds/step.ogg")
    private Sound playerStep;

    @Asset("sounds/jump.ogg")
    private Sound playerJump;

    @Asset("sounds/land.ogg")
    private Sound playerLand;

    @Asset("sounds/computer_load.mp3")
    private Sound computerLoad;

    @Asset("sounds/computer_interact.ogg")
    private Sound computerInteract;

    @Asset("sounds/computer_talk.ogg")
    private Sound computerTalk;

    @Asset("sounds/wall_block.ogg")
    private Sound wallBlock;


}
