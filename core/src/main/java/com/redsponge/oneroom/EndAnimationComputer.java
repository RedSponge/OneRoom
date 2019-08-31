package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.redsponge.redengine.assets.Fonts;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.LightTextures;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.screen.ScreenEntity;

import java.util.function.Consumer;

public class EndAnimationComputer extends ScreenEntity {

    private TextureRegion happy, menu;
    private Animation<TextureRegion> happyTalk;

    private NinePatch talkBox;
    private TypingLabel talk;

    private boolean talking;
    private float time;
    private boolean off;
    private TextureRegion offTexture;

    private PointLight light;

    private Sound talkSound;
    private int timeRevealed;
    private boolean revealed;

    public EndAnimationComputer(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void loadAssets() {
        if(!off) {
            happy = assets.getTextureRegion("computerHappy");
            happyTalk = assets.getAnimation("computerHappyTalk");
            talkBox = new NinePatch(assets.getTextureRegion("computerTextBox"), 4, 4, 4, 4);
        }
        offTexture = assets.getTextureRegion("computerOff");
        menu = assets.getTextureRegion("computerMenu");
        talkSound = assets.get("computerTalk", Sound.class);
    }

    @Override
    public void added() {
        talk = new TypingLabel("This is a cool test!{WAIT} This text is just going to go on and on and on and on and on and on and on and on and on and on and on and on and on and on", new LabelStyle(Fonts.pixelMix16, Color.WHITE));
        talk.setFontScale(0.5f);
        talk.setWrap(true);
        talk.setWidth(GameScreen.WIDTH - 75);
        talk.setY(GameScreen.HEIGHT - 42);
        talk.setX(40);

        light = new PointLight(GameScreen.WIDTH / 2f, GameScreen.HEIGHT / 3f * 2, 180, LightTextures.getInstance().featheredPointLight);
        light.getColor().set(0, 0.2f, 0, 1.0f);

        screen.getSystem(LightSystem.class).addLight(light, LightType.ADDITIVE);
    }

    boolean startedMonologue;

    @Override
    public void tick(float v) {
        if(off) return;
        time += v;
        if(time > 8 && !startedMonologue) {
            doTalk("And thus..{WAIT} the player decided to do it..{WAIT=2} They destroyed the lab..{WAIT} And finally...{WAIT=2} they were free{EVENT=summon_player}.{WAIT=3} ", (m) -> ((TrollEndScreen)screen).spawnPlayer(), null);
            startedMonologue = true;
        }
        talk.act(v);
        if(revealed) timeRevealed++;
        if(talking) {
            if(revealed && timeRevealed % 15 == 0) {
                talkSound.play(0.3f, MathUtils.random(0.9f, 1.1f), 0);
            }
        }
    }

    @Override
    public void render() {
        TextureRegion tr = off ? menu : talking ? happyTalk.getKeyFrame(time) : happy;
        batch.draw(tr, GameScreen.WIDTH / 2f - 64, GameScreen.HEIGHT / 3f * 2 - 48, 128, 96);
    }

    public void renderToUI() {
        if(talking) {
            talkBox.draw(batch, 30, GameScreen.HEIGHT - 50, GameScreen.WIDTH - 60, 30);
            talk.draw(batch, 1);
        }
    }

    @Override
    public void removed() {

    }

    public void doTalk(String text, Consumer<String> onEvent, Runnable onEnd) {
        talk.skipToTheEnd();
        talk.setTypingListener(new TypingAdapter() {
            @Override
            public void event(String event) {
                if(onEvent != null) onEvent.accept(event);
            }

            @Override
            public void end() {
                talking = false;
                if(onEnd != null) onEnd.run();
            }
        });
        talk.restart(text);
        talking = true;
    }

    public void turnOff() {
        off = true;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isOff() {
        return off;
    }
}
