package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.oneroom.menu.MenuScreen;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.render.util.ScreenFiller;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;

import static com.redsponge.oneroom.GameScreen.ROOM_HEIGHT;
import static com.redsponge.oneroom.GameScreen.ROOM_WIDTH;

public class TrollEndScreen extends AbstractScreen {

    public static final int WIDTH = GameScreen.WIDTH;
    public static final int HEIGHT = GameScreen.HEIGHT;
    private static final int ROOM_WIDTH = GameScreen.ROOM_WIDTH;
    private static final int ROOM_HEIGHT = GameScreen.ROOM_HEIGHT;

    private FitViewport viewport;
    private FitViewport guiViewport;

    private DelayedRemovalArray<Wall> walls;

    private Texture wood;
    private EndAnimationComputer computer;
    private PhysicsWorld pWorld;
    private float time;

    private Texture destroyedRoom;
    private Texture destroyedRoomWithPlayer;

    private ParticleEffect comeOut;
    private boolean reveal;

    private float revealTime = 16;

    private LightSystem ls;
    private Sound comeOutOfRuins;

    private float instructionTimer;
    private Texture instructions;
    private boolean showInstructions;

    private Music music;
    private Music menuMusic;

    public TrollEndScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        time = -5;
        viewport = new FitViewport(WIDTH, HEIGHT);
        guiViewport = new FitViewport(WIDTH, HEIGHT);

        music = assets.get("music", Music.class);
        music.setVolume(0.5f);

        menuMusic = assets.get("menuMusic", Music.class);
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.5f);

        addSystem(LightSystem.class, WIDTH, HEIGHT, batch);
        ls = getSystem(LightSystem.class);
        ls.registerLightType(LightType.ADDITIVE);

        ((OrthographicCamera)viewport.getCamera()).zoom = 0.5f;
        viewport.getCamera().position.set(WIDTH / 2f - 50, HEIGHT / 4f, 0);

        pWorld = new PhysicsWorld();

        walls = new DelayedRemovalArray<>();
        addWall(-WIDTH * 2, 0, WIDTH * 5, (HEIGHT - ROOM_HEIGHT) / 2);
        addWall(-WIDTH * 2, (ROOM_HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2), WIDTH * 5, (HEIGHT - ROOM_HEIGHT) / 2);

        addWall(-WIDTH, 60, (WIDTH - ROOM_WIDTH) / 2 + WIDTH, HEIGHT);
        addWall((ROOM_WIDTH + (WIDTH - ROOM_WIDTH) / 2), 60, WIDTH + (WIDTH - ROOM_WIDTH), HEIGHT);


        wood = assets.get("backgroundIron", Texture.class);
        destroyedRoom = assets.get("destroyedLab", Texture.class);
        destroyedRoomWithPlayer = assets.get("destroyedLabWithPlayer", Texture.class);
        computer = new EndAnimationComputer(batch, shapeRenderer);

        addEntity(computer);

        comeOut = new ParticleEffect();
        comeOut.load(Gdx.files.internal("particles/come_out_of_ruins.p"), Gdx.files.internal("particles"));
        comeOut.setPosition(WIDTH / 2f, (HEIGHT - ROOM_HEIGHT) / 2f);

        comeOutOfRuins = assets.get("comeOutOfRuins", Sound.class);

        instructions = assets.get("instructions", Texture.class);
    }

    @Override
    public void tick(float v) {
        pWorld.update(v);
        tickEntities(v);
        if(time + v > 0 && time < 0) {
            music.play();
        }
        time += v;

        viewport.getCamera().position.x = Math.min(WIDTH / 2f - (30 * (10 - time)), WIDTH / 2f);
        comeOut.update(v);
        if(time > revealTime && !reveal) {
            computer.setRevealed(true);
            computer.doTalk("Well.. Not really..{WAIT} But I can definitely say that this experiment was a HUGE SUCCESS!!{WAIT=2} You see..{WAIT} There really was...{WAIT=3} No Escape.{WAIT=4} ", null, () -> {
                computer.turnOff();
                menuMusic.play();
                Gdx.input.setInputProcessor(new InputAdapter() {
                    @Override
                    public boolean keyDown(int keycode) {
                        boolean noMagic = keycode == Keys.M;
                        ga.transitionTo(new GameScreen(ga, noMagic), Transitions.sineSlide(1, batch, shapeRenderer));
                        return true;
                    }
                });
            });
            reveal = true;
        }
        if(computer.isOff()) {
            instructionTimer += v;
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float revealProgress = Interpolation.exp5.apply(time < revealTime ? 0 : time < revealTime + 2 ? (time - revealTime) / 2 : 1);
        ((OrthographicCamera)viewport.getCamera()).zoom = revealProgress * 0.5f + 0.5f;
        viewport.getCamera().position.y = HEIGHT / 4f + revealProgress * HEIGHT / 4f;

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        int w = (WIDTH * 5) / wood.getWidth();
        int h = (HEIGHT * 5) / wood.getHeight();
        for(int i = (-WIDTH * 2) / wood.getWidth(); i < w; i++) {
            for(int j = (-HEIGHT * 2) / wood.getHeight(); j < h; j++) {
                batch.draw(wood, i * wood.getWidth(), j * wood.getHeight());
            }
        }
        renderEntities();
        batch.end();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(GameScreen.WALLS_COLOR);
        for (Wall wall : walls) {
            shapeRenderer.rect(wall.pos.x, wall.pos.y, wall.size.x, wall.size.y);
        }
        shapeRenderer.end();
        ScreenFiller.fillScreen(shapeRenderer, 0, 0, 0, 1 - revealProgress);
        viewport.apply();

        batch.begin();

        float wi = destroyedRoom.getWidth();
        float he = destroyedRoom.getHeight();
        batch.draw(destroyedRoom, WIDTH / 2f - wi / 2f, (HEIGHT - ROOM_HEIGHT) / 2f, wi, he);
        comeOut.draw(batch);
        batch.end();

        ls.prepareMap(LightType.ADDITIVE, viewport);
        ls.renderToScreen(LightType.ADDITIVE);

        guiViewport.apply();
        batch.setProjectionMatrix(guiViewport.getCamera().combined);
        batch.begin();
        computer.renderToUI();
        float progress = Math.min(instructionTimer / 5f, 1);
        batch.setColor(1, 1, 1, progress);
        batch.draw(instructions, WIDTH / 2f - instructions.getWidth() / 2f, HEIGHT / 8f);
        batch.setColor(Color.WHITE);
        batch.end();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        guiViewport.update(width, height, true);
        ls.resize(width, height);
    }

    public Wall addWall(int x, int y, int w, int h) {
        Wall wa = new Wall(pWorld, x, y, w, h);
        pWorld.addSolid(wa);
        walls.add(wa);
        return wa;
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return TrollEndAssets.class;
    }

    public void spawnPlayer() {
        comeOut.start();
        destroyedRoom = destroyedRoomWithPlayer;
        comeOutOfRuins.play();
    }
}
