package com.redsponge.oneroom.computer;

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
import com.redsponge.oneroom.ActivateableLight;
import com.redsponge.oneroom.GameScreen;
import com.redsponge.oneroom.player.PlayerInteractDetector;
import com.redsponge.redengine.assets.Fonts;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.LightTextures;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.screen.ScreenEntity;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;

import java.util.function.Consumer;

public class Computer extends ScreenEntity {

    private TextureRegion off, loading, enterPassword, happy, movethroughHint, angry;
    private Animation<TextureRegion> happyTalk, happyLaugh, angryTalk;

    private ComputerState state;

    private PlayerInteractDetector pid;

    private GameAccessor ga;
    private ComputerPasswordScreen pwScreen;
    private float timeOn;

    private TypingLabel talk;
    private NinePatch textBox;

    private boolean talking;
    private boolean laughing;

    private float timeAlive;
    private boolean saidWelcome;

    private float timeAngry;
    private boolean saidDudeStop;

    private int ticksTalking;

    private boolean shouldGravityTaunt;
    private float timeUntilGravityTaunt;
    private boolean saidDontDare;

    private Sound loadSound;
    private Sound interactSound;
    private Sound talkSound;
    private Sound wallBlockSound;
    private Sound staticSound;

    private ActivateableLight light;
    private LightSystem ls;
    private boolean saidAnotherWarning;
    private boolean saidEvenMoreWarnings;


    public Computer(SpriteBatch batch, ShapeRenderer shapeRenderer, GameAccessor ga) {
        super(batch, shapeRenderer);
        this.ga = ga;
    }

    @Override
    public void loadAssets() {
        off = assets.getTextureRegion("computerOff");
        loading = assets.getTextureRegion("computerLoading");
        enterPassword = assets.getTextureRegion("computerEnterPassword");
        happy = assets.getTextureRegion("computerHappy");
        textBox = new NinePatch(assets.getTextureRegion("computerTextBox"), 4, 4, 4, 4);
        happyTalk = assets.getAnimation("computerHappyTalk");
        happyLaugh = assets.getAnimation("computerHappyLaugh");
        movethroughHint = assets.getTextureRegion("computerMovethroughHint");
        angry = assets.getTextureRegion("computerAngry");
        angryTalk = assets.getAnimation("computerAngryTalk");

        loadSound = assets.get("computerLoad", Sound.class);
        interactSound = assets.get("computerInteract", Sound.class);
        talkSound = assets.get("computerTalk", Sound.class);
        wallBlockSound = assets.get("wallBlock", Sound.class);
        staticSound = assets.get("staticSound", Sound.class);
    }

    @Override
    public void added() {
        state = ComputerState.OFF;
        pwScreen = new ComputerPasswordScreen(ga, (GameScreen) screen);
        pid = new PlayerInteractDetector(batch, shapeRenderer, ((GameScreen)screen).getPlayer());
        pid.getPos().set(GameScreen.WIDTH / 2, GameScreen.HEIGHT / 2);
        pid.setRadius(48);
        pid.setOnInteract(this::requirePassword);

        screen.addEntity(pid);

//        talking = true;

        talk = new TypingLabel("This is a cool test!{WAIT} This text is just going to go on and on and on and on and on and on and on and on and on and on and on and on and on and on", new LabelStyle(Fonts.pixelMix16, Color.WHITE));
        talk.setFontScale(0.5f);
        talk.setWrap(true);
        talk.setWidth(GameScreen.WIDTH - 75);
        talk.setY(GameScreen.HEIGHT - 42);
        talk.setX(40);

        light = new ActivateableLight(GameScreen.WIDTH / 2f, GameScreen.HEIGHT / 3f * 2, 180, LightTextures.getInstance().featheredPointLight);
        ls = screen.getSystem(LightSystem.class);
        ls.addLight(light, LightType.ADDITIVE);
    }

    private void requirePassword() {
        interactSound.play();
        ga.transitionTo(pwScreen, Transitions.linearFade(1, batch, shapeRenderer));
    }

    @Override
    public void tick(float v) {
        light.setActive(state != ComputerState.OFF);
        timeAlive += v;
        if(timeOn == 0 && state == ComputerState.LOADING) {
            loadSound.play();
        }
        if(state != ComputerState.OFF) {
            timeOn += v;
        } else {
            timeOn = 0;
        }

        if(state == ComputerState.LOADING) {
            if(timeOn > 4.2f) {
                state = ComputerState.ENTER_PASSWORD;
            }
        }
        if(state == ComputerState.HAPPY && !saidWelcome) {
            doTalk("Welcome Test Subject {SPEED=0.1}#5555515{NORMAL} to experiment {SPEED=0.1}#112435782{NORMAL}: \"No Escape\".{WAIT} I hope you find this place cozy, since,{WAIT} as you might have noticed,{WAIT} you cannot leave it.{WAIT=4} ",
                    false, true,null, () -> {
                        ((GameScreen)screen).getGradualMusicHandler().transitionTo(1, false);
                doTalk("Yup{SPEED=0.5}...{NORMAL}{WAIT} Cool right?{WAIT} You can never leave this place.{WAIT} Just you and me...{WAIT} Forever...{WAIT} In this one,{WAIT} singular{WAIT} room...{WAIT=5} ",
                                null, () -> doTalk("Ok nevermind, I'm bored...{WAIT} If you want to leave the exit switch{EVENT=summon_troll_exit_switch} will be over there :){WAIT=3} ",
                                        (s) -> {if(s.equals("summon_troll_exit_switch")) ((GameScreen)screen).summonTrollExitSwitch();},
                                        () -> {
                                            talking = false;
                                        }
                                ));
                    });
            saidWelcome = true;
        }

        if(shouldGravityTaunt) {
            timeUntilGravityTaunt -= v;
            if(timeUntilGravityTaunt <= 0){
                    doTalk("Hahahah you look so helpless stuck like that..{WAIT=3} ", true,null,
          () -> doTalk("I can't believe you actually fell for that!{WAIT} How stupid can one be?!{WAIT} Hahahahahhaha{WAIT=3} ", true, null,
          () -> doTalk("Hey, don't give me that look!{WAIT} I am just saying the truth...{WAIT=3} ", null,
          () -> doTalk("{SLOW}{SHAKE=1;1;5}Uuuughhh{ENDSHAKE}{NORMAL} fine..{WAIT} I'll let you climb to your switch{WAIT} hang on...{WAIT=2} There{EVENT=build_gravity_platforms}.{WAIT} ",
                (s) -> ((GameScreen)screen).setupGravityFlipPlatforms(), null))));
                shouldGravityTaunt = false;
            }
        }

        if(state == ComputerState.ANGRY) {
            if(saidDudeStop) timeAngry += v;
            if(((GameScreen)screen).getPlayer().getPos().x < GameScreen.WIDTH / 3 && saidDudeStop && !saidDontDare) {
                doTalk("NO!{WAIT} DON'T YOU DARE PRESS THAT BUTTON!!!{WAIT=2} BAD THINGS ARE GOING TO HAPPEN IF YOU PRESS IT!{WAIT=3} ", null, null);
                ((GameScreen)screen).addWall(GameScreen.WIDTH / 3 - 20, 0, 10, GameScreen.HEIGHT - 40);
                saidDontDare = true;
                wallBlockSound.play(0.5f);
            }
            if(timeAngry > 10 && !saidAnotherWarning) {
                doTalk("YOU WILL BE VERY SORRY IF YOU PRESS THAT BUTTON!{WAIT=3} ", null, null);
                saidAnotherWarning = true;
            }
            if(timeAngry > 25 && !saidEvenMoreWarnings) {
                doTalk("YOU DON'T WANT TO KNOW WHAT I'LL DO IF YOU PRESS THAT BUTTON!!!!{WAIT=4}", null, null);
                saidEvenMoreWarnings = true;
            }
        }

        pid.setActive(state == ComputerState.ENTER_PASSWORD);

        if(talking) {
            ticksTalking++;
            if(ticksTalking % 15 - (laughing ? 10 : 0) == 0) {
                talkSound.play(0.1f, MathUtils.random(.9f, 1.1f) + (laughing ? 0.5f : 0) - (state == ComputerState.ANGRY ? 0.6f : 0), 0);
            }
            talk.act(v);
        }
    }

    public void setShouldGravityTaunt(boolean shouldGravityTaunt) {
        this.shouldGravityTaunt = shouldGravityTaunt;
    }

    public void setTimeUntilGravityTaunt(int timeUntilGravityTaunt) {
        this.timeUntilGravityTaunt = timeUntilGravityTaunt;
    }

    public void doTalk(String text, Consumer<String> onEvent, Runnable onEnd) {
        doTalk(text, false, onEvent, onEnd);
    }

    public void doTalk(String text, boolean laughing, Consumer<String> onEvent, Runnable onEnd) {
        doTalk(text, laughing, false, onEvent, onEnd);
    }

    public void doTalk(String text, boolean laughing, boolean stopMusic, Consumer<String> onEvent, Runnable onEnd) {
        if(stopMusic) ((GameScreen)screen).getGradualMusicHandler().getCurrent().pause();
        talk.skipToTheEnd();
        this.laughing = laughing;
        talk.setTypingListener(new TypingAdapter() {
            @Override
            public void event(String event) {
                if(onEvent != null) onEvent.accept(event);
            }

            @Override
            public void end() {
                Computer.this.laughing = false;
                talking = false;
                if(onEnd != null) onEnd.run();
                if(stopMusic) ((GameScreen)screen).getGradualMusicHandler().getCurrent().play();
            }
        });
        talk.restart(text);
        talking = true;
    }

    @Override
    public void render() {
        TextureRegion t;
        switch(state) {
            case OFF:
                t = off;
                break;
            case LOADING:
                t = loading;
                break;
            case ENTER_PASSWORD:
                t = enterPassword;
                break;
            case HAPPY:
                t = happy;
                if(talking) {
                    t = happyTalk.getKeyFrame(timeAlive);
                    if(laughing) {
                        t = happyLaugh.getKeyFrame(timeAlive);
                    }
                }
                break;
            case MOVE_THROUGH_HINT:
                t = movethroughHint;
                break;
            case ANGRY:
                t = angry;
                if(talking) {
                    t = angryTalk.getKeyFrame(timeAlive);
                }
                break;
            default:
                throw new RuntimeException();
        }

        batch.draw(t, GameScreen.WIDTH / 2f - 64, GameScreen.HEIGHT / 3f * 2 - 48, 128, 96);
    }

    public void renderToUI() {
        if(talking) {
            textBox.draw(batch, 30, GameScreen.HEIGHT - 50, GameScreen.WIDTH - 60, 30);
            talk.draw(batch, 1);
        }
    }

    @Override
    public void removed() {
        ls.removeLight(light, LightType.ADDITIVE);
    }

    @Override
    public int getZ() {
        return -1;
    }

    public void setState(ComputerState state) {
        this.state = state;
//        staticSound.play(0.2f);
        if(state == ComputerState.ANGRY) {
            light.getColor().set(.2f, 0, 0, 1);
        } else {
            light.getColor().set(0, .2f, 0, 1);
        }
    }

    public ComputerState getState() {
        return state;
    }

    public TypingLabel getTalk() {
        return talk;
    }


    public void shoutDudeStop() {
        if(saidDudeStop) return;
        saidDudeStop = true;

        doTalk("Dude...{WAIT=2} You're not thinking about pressing this button right..?{WAIT=2} ", null, null);
    }

    public boolean isSaidDontDare() {
        return saidDontDare;
    }

    public boolean isTalking() {
        return talking;
    }
}
