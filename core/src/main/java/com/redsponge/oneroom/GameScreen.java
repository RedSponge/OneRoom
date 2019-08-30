package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.oneroom.computer.Computer;
import com.redsponge.oneroom.computer.ComputerState;
import com.redsponge.oneroom.mechanics.Lever;
import com.redsponge.oneroom.mechanics.SelfDestructButton;
import com.redsponge.oneroom.player.Player;
import com.redsponge.oneroom.player.PortalLink;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.physics.PhysicsDebugRenderer;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.screen.ScreenEntity;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;
import com.redsponge.redengine.utils.Logger;

import java.lang.reflect.Field;

public class GameScreen extends AbstractScreen {

    public static final float ASPECT_RATIO = 16 / 9f;

    public static final int ROOM_WIDTH = 480;
    public static final int ROOM_HEIGHT = 320;

    public static final int WIDTH = 640;
    public static final int HEIGHT = (int) (WIDTH / ASPECT_RATIO);

    private static final Color WALLS_COLOR = new Color(0.7f, 0.7f, 0.7f, 1);
    private static final Color BACKGROUND_COLOR = new Color(237 / 255f, 237 / 255f, 237 / 255f, 1);

    private PhysicsDebugRenderer pdr;
    private PhysicsWorld pWorld;

    private FitViewport viewport;
    private Texture roomTexture;

    private DelayedRemovalArray<Wall> walls;
    private DelayedRemovalArray<PortalLink> portals;

    private Player player;
    private Computer computerScreen;
    private Texture wood;
    private Vector3 camTarget = new Vector3();

    private FitViewport guiViewport;

    private LightSystem ls;

    private Lever bottomTopLever;
    private Wall[] binaryHints;

    private String currentGuessedPassword;
    private Wall centralPlatform;

    private GradualMusicHandler gmh;

    public GameScreen(GameAccessor ga) {
        super(ga);
    }

    boolean first = true;
    @Override
    public void show() {
        if(first) {
            first = false;
            gmh = new GradualMusicHandler("music/1.wav", "music/2.wav", "music/4.wav", "music/5.wav", "music/6.wav");

            viewport = new FitViewport(WIDTH, HEIGHT);
            viewport.apply(true);
            guiViewport = new FitViewport(WIDTH, HEIGHT);
            addSystem(LightSystem.class, WIDTH, HEIGHT, batch);

            ls = getSystem(LightSystem.class);
            ls.registerLightType(LightType.MULTIPLICATIVE);
            ls.setAmbianceColor(Color.LIGHT_GRAY, LightType.MULTIPLICATIVE);

            ls.registerLightType(LightType.ADDITIVE);


            pWorld = new PhysicsWorld();
            walls = new DelayedRemovalArray<>();
            portals = new DelayedRemovalArray<>();

            pdr = new PhysicsDebugRenderer();

            player = new Player(batch, shapeRenderer);
            addEntity(player);

            computerScreen = new Computer(batch, shapeRenderer, ga);
            addEntity(computerScreen);
            setupFirstStage();
            setupSecondStage();
//            setupThirdStage();
            computerPasswordIsCorrect();
            summonTrollExitSwitch();
            setupGravityFlipPlatforms();
            didTroll = true;
            didThatWasFun = true;
            thirdStage = true;
            computerScreen.getTalk().skipToTheEnd();
            centralPlatform = addWall(WIDTH / 2 - 60, HEIGHT / 2, 120, 10);
            currentGuessedPassword = "2143";
            computerScreen.setState(ComputerState.MOVE_THROUGH_HINT);
        } else {
            try {
                Field f = AbstractScreen.class.getDeclaredField("entities");
                f.setAccessible(true);
                for (ScreenEntity screenEntity : (DelayedRemovalArray<ScreenEntity>) f.get(this)) {
                    screenEntity.setAssets(assets);
                    System.out.println(screenEntity.getClass());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        roomTexture = assets.get("roomTexture", Texture.class);
        wood = assets.get("backgroundTile", Texture.class);
    }

    private void setupFirstStage() {
        gmh.transitionTo(0);
        addWall(-WIDTH * 2, 0, WIDTH * 5, (HEIGHT - ROOM_HEIGHT) / 2);
        addWall(-WIDTH * 2, (ROOM_HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2), WIDTH * 5, (HEIGHT - ROOM_HEIGHT) / 2);

        addWall(-WIDTH, 60, (WIDTH - ROOM_WIDTH) / 2 + WIDTH, HEIGHT);
        addWall((ROOM_WIDTH + (WIDTH - ROOM_WIDTH) / 2), 60, WIDTH + (WIDTH - ROOM_WIDTH), HEIGHT);

        PortalLink portal = addPortal(WIDTH * 1.6f, 40, WIDTH * -0.4f, 40, 25, true);
        portal.setOnMoveThrough(() -> {
            if(computerScreen.getState() == ComputerState.MOVE_THROUGH_HINT) {
                if(player.getPos().x < 0) {
                    currentGuessedPassword += "2";
                } else {
                    currentGuessedPassword += "4";
                }
                checkMoveThroughPuzzle();
            }
            setupSecondStage();
        });
    }

    private PortalLink addPortal(float x1, float y1, float x2, float y2, float r, boolean onXAxis) {
        PortalLink pl = new PortalLink(new Vector2(x1, y1), new Vector2(x2, y2), viewport.getCamera().position, r, onXAxis);
        portals.add(pl);
        return pl;
    }

    public Wall addWall(int x, int y, int w, int h) {
        Wall wa = new Wall(pWorld, x, y, w, h);
        pWorld.addSolid(wa);
        walls.add(wa);
        return wa;
    }


    @Override
    public void tick(float v) {
        for (PortalLink portal : portals) {
            portal.processPlayer(player);
        }
        pWorld.update(v);
        tickEntities(v);

        if(player.getPos().x > ROOM_WIDTH + (WIDTH - ROOM_WIDTH) / 2 || player.getPos().x < (WIDTH - ROOM_WIDTH) / 2) {
            camTarget.x = player.getPos().x;
        } else {
            camTarget.x = WIDTH / 2f;
        }

        if(player.getPos().y > ROOM_HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2 || player.getPos().y < (HEIGHT - ROOM_HEIGHT) / 2) {
            camTarget.y = player.getPos().y;
        } else {
            camTarget.y = HEIGHT / 2f;
        }

        Vector3 camPos = viewport.getCamera().position;
        camPos.set(camTarget);
    }


    boolean stageTwo;
    public void setupSecondStage() {
        if(stageTwo) return;
        stageTwo = true;

        for (Wall wall : walls) {
            wall.remove();
        }
        walls.clear();

        addWall(-WIDTH * 2, -HEIGHT * 2, (int) (WIDTH * 2.45f), 2 * HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2);
        addWall((int) (WIDTH * 0.55f), -HEIGHT * 2, (int) (WIDTH * 2.4f), 2 * HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2);
        Wall toggleableWall = addWall((int) (WIDTH * 0.45f), 0, (int) (WIDTH * 0.1f), (HEIGHT - ROOM_HEIGHT) / 2);

        addWall(-WIDTH, ROOM_HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2, (int) (WIDTH * 1.45f), 2 * HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2);
        addWall((int) (WIDTH * 0.55f), ROOM_HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2, (int) (WIDTH * 1.4f), 2 * HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2);

        addWall(-WIDTH, 60, (WIDTH - ROOM_WIDTH) / 2 + WIDTH, HEIGHT);
        addWall((ROOM_WIDTH + (WIDTH - ROOM_WIDTH) / 2), 60, WIDTH + (WIDTH - ROOM_WIDTH), HEIGHT);

        PortalLink pl = addPortal(WIDTH / 2f, - HEIGHT, WIDTH / 2f, HEIGHT * 2, 30, false);
        pl.setOnMoveThrough(() -> {
            setupThirdStage();
            if(computerScreen.getState() != ComputerState.OFF) {
                setupFourthStage();
            }
            if(computerScreen.getState() == ComputerState.MOVE_THROUGH_HINT) {
                if(player.getPos().y < 0) {
                    currentGuessedPassword += "1";
                } else {
                    currentGuessedPassword += "3";
                }
                checkMoveThroughPuzzle();
            }
        });
        Lever lever = new Lever(batch, shapeRenderer);
        lever.setInverted(true);
        lever.setAttachedToggleable(toggleableWall);

        addEntity(lever);
        lever.getPos().set(WIDTH / 4, (HEIGHT - ROOM_HEIGHT) / 2);
        bottomTopLever = lever;
    }

    private void checkMoveThroughPuzzle() {
        Logger.log(this, currentGuessedPassword);
        if(currentGuessedPassword.contains("2143")) {
            setupLastStage();
        }
    }

    boolean lastStage;
    private void setupLastStage() {
        if(lastStage) return;
        gmh.transitionTo(2);
        lastStage = true;
        for (Wall gravityWall : gravityWalls) {
            gravityWall.remove();
            walls.removeValue(gravityWall, true);
        }
        computerScreen.setState(ComputerState.ANGRY);
        SelfDestructButton sdb = new SelfDestructButton(batch, shapeRenderer);
        addEntity(sdb);
        sdb.getPos().set((WIDTH - ROOM_WIDTH) / 2, HEIGHT / 2);
        addWall((WIDTH - ROOM_WIDTH) / 2, HEIGHT / 2 - 48, 60, 10);
        addWall((WIDTH - ROOM_WIDTH) / 2, HEIGHT / 2 + 48, 60, 10);
        Wall opened = addWall((WIDTH - ROOM_WIDTH) / 2 + 60, HEIGHT / 2 - 48, 10, 106);
        Lever l = new Lever(batch, shapeRenderer);
        l.setAttachedToggleable(opened);
        l.setInverted(true);
        addEntity(l);
        l.getPos().set(WIDTH - (WIDTH - ROOM_WIDTH) / 2 - Lever.WIDTH, HEIGHT / 2);

        centralPlatform.remove();
        walls.removeValue(centralPlatform, true);

        MovingWall[] movingWalls = new MovingWall[]{
                new MovingWall(pWorld, (int) (WIDTH / 4 * 2.5f), HEIGHT / 2 - 80, 50, 10),
                new MovingWall(pWorld, (int) (WIDTH / 4 * 3f), HEIGHT / 2 - 80, 50, 10),
                new MovingWall(pWorld, (int) (WIDTH / 4 * 2f), HEIGHT / 2 - 80, 50, 10),
                new MovingWall(pWorld, (int) (WIDTH / 4 * 1.5f), HEIGHT / 2 - 80, 50, 10),
        };
        for (MovingWall movingWall : movingWalls) {
            walls.add(movingWall);
            pWorld.addSolid(movingWall);
        }

        l.setOnToggle(() -> {
            computerScreen.shoutDudeStop();
            for (MovingWall movingWall : movingWalls) {
                movingWall.setActive(true);
            }
        });
    }

    boolean thirdStage;
    private void setupThirdStage() {
        if(thirdStage) return;
        thirdStage = true;

        centralPlatform = addWall(WIDTH / 2 - 50, HEIGHT / 2, 100, 10);


        Wall[] left = {
                addWall(WIDTH / 4 - 25, HEIGHT / 4, 50, 10),
                addWall(WIDTH / 4 - 25 - 50, (int) (HEIGHT / 4 * 1.5f), 50, 10),
                addWall(WIDTH / 4 - 25, HEIGHT / 2, 50, 10),
                addWall(WIDTH / 4 - 25 - 50, (int) (HEIGHT / 4 * 2.5f), 50, 10),
                addWall(WIDTH / 4 - 25, HEIGHT / 4 * 3, 50, 10),
        };

        Wall[] right = {
                addWall(WIDTH / 4 * 3 - 25, HEIGHT / 4, 50, 10),
                addWall(WIDTH / 4 * 3 - 25 + 50, (int) (HEIGHT / 4 * 1.5f), 50, 10),
                addWall(WIDTH / 4 * 3 - 25, HEIGHT / 2, 50, 10),
                addWall(WIDTH / 4 * 3 - 25 + 50, (int) (HEIGHT / 4 * 2.5f), 50, 10),
                addWall(WIDTH / 4 * 3 - 25, HEIGHT / 4 * 3, 50, 10),
        };

        Lever l = new Lever(batch, shapeRenderer);
        addEntity(l);
        l.getPos().set(WIDTH / 4-Lever.WIDTH / 2, HEIGHT / 4 * 3+10);

        Lever l2 = new Lever(batch, shapeRenderer);
        addEntity(l2);
        l2.getPos().set(WIDTH / 4 * 3 - Lever.WIDTH / 2, HEIGHT / 4 * 3+10);

        Runnable onToggle = () -> {
            if(computerScreen.getState() == ComputerState.OFF && l.isOn() && l2.isOn()) {
                computerScreen.setState(ComputerState.LOADING);
                removeEntity(l);
                removeEntity(l2);
                //TODO: Summon explosions on removal
            }
        };

        l.setOnToggle(() -> {
            for (Wall wall : left) {
                wall.remove();
                walls.removeValue(wall, true);
            }
            onToggle.run();
        });
        l2.setOnToggle(() -> {
            for (Wall wall : right) {
                wall.remove();
                walls.removeValue(wall, true);
            }
            onToggle.run();
        });

    }

    boolean fourthStage;
    private void setupFourthStage() {
        if(fourthStage || computerScreen.getState() != ComputerState.ENTER_PASSWORD && computerScreen.getState() != ComputerState.LOADING) return;
        fourthStage = true;

        this.binaryHints = new Wall[]{
                addWall(WIDTH / 4 - 60, HEIGHT / 4 * 3, 10, 20),
                addWall(WIDTH / 4 - 30, HEIGHT / 4 * 3, 20, 20),
                addWall(WIDTH / 4, HEIGHT / 4 * 3, 20, 20),

                addWall(WIDTH / 4 * 3, HEIGHT / 4 * 3, 20, 20),
                addWall(WIDTH / 4 * 3 + 30, HEIGHT / 4 * 3, 10, 20),
                addWall(WIDTH / 4 * 3 + 60, HEIGHT / 4 * 3, 10, 20),

                addWall(WIDTH / 4 - 60, HEIGHT / 4, 10, 20),
                addWall(WIDTH / 4 - 30, HEIGHT / 4, 10, 20),
                addWall(WIDTH / 4, HEIGHT / 4, 10, 20),

                addWall(WIDTH / 4 * 3, HEIGHT / 4, 20, 20),
                addWall(WIDTH / 4 * 3 + 30, HEIGHT / 4, 20, 20),
                addWall(WIDTH / 4 * 3 + 60, HEIGHT / 4, 10, 20),
        };
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        int w = (WIDTH * 5) / wood.getWidth();
        int h = (HEIGHT * 5) / wood.getHeight();
        for(int i = (-WIDTH * 2) / wood.getWidth(); i < w; i++) {
            for(int j = (-HEIGHT * 2) / wood.getHeight(); j < h; j++) {
                batch.draw(wood, i * wood.getWidth(), j * wood.getHeight());
            }
        }
        batch.end();

//        renderRoom();

        batch.begin();
        renderEntities();
        batch.end();

        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(WALLS_COLOR);
        for (Wall wall : walls) {
            if(wall.isEnabled()) {
                shapeRenderer.rect(wall.pos.x, wall.pos.y, wall.size.x, wall.size.y);
            }
        }
        shapeRenderer.end();

        ls.prepareMap(LightType.MULTIPLICATIVE, viewport);
        ls.prepareMap(LightType.ADDITIVE, viewport);

        ls.renderToScreen(LightType.MULTIPLICATIVE);
        ls.renderToScreen(LightType.ADDITIVE);

        renderGUI();
//        pdr.render(pWorld, viewport.getCamera().combined);
    }

    private void renderGUI() {
        guiViewport.apply();
        batch.setProjectionMatrix(guiViewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(guiViewport.getCamera().combined);

        batch.begin();
        computerScreen.renderToUI();
        batch.end();
    }

    private void renderRoom() {
        float marginX = (WIDTH - ROOM_WIDTH) / 2f;
        float marginY = (HEIGHT - ROOM_HEIGHT) / 2f;
        batch.begin();
        batch.draw(roomTexture, marginX, marginY, ROOM_WIDTH, ROOM_HEIGHT);
        batch.end();
    }

    public Vector3 getCamPos() {
        return viewport.getCamera().position;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        guiViewport.update(width, height, true);
        ls.resize(width, height);
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return GameAssets.class;
    }

    public PhysicsWorld getPhysicsWorld() {
        return pWorld;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void notified(int notification) {
        super.notified(notification);
        for (PortalLink portal : portals) {
            portal.notified(notification);
        }
    }

    @Override
    public boolean shouldDispose() {
        return false;
    }

    @Override
    public void dispose() {
        gmh.dispose();
    }

    public void computerPasswordIsCorrect() {
        computerScreen.setState(ComputerState.HAPPY);
        gmh.transitionTo(1);
        if(binaryHints != null) {
            for (Wall binaryHint : binaryHints) {
                binaryHint.remove();
                walls.removeValue(binaryHint, true);
            }
        }
    }

    boolean didTroll = false;
    boolean didThatWasFun = false;

    public void summonTrollExitSwitch() {
        Lever l = new Lever(batch, shapeRenderer);
        addEntity(l);
        l.getPos().set(WIDTH / 4 * 3, (HEIGHT - ROOM_HEIGHT) / 2);
        l.setOnToggle(() -> {
            player.toggleGravity();
            if(!didTroll) {
                computerScreen.doTalk("SIKE!!!{WAIT} THIS SWITCH JUST FLIPS GRAVITY! {WAIT=1} ", true, null, null);
                computerScreen.setShouldGravityTaunt(true);
                computerScreen.setTimeUntilGravityTaunt(10);
                didTroll = true;
            } else if(!didThatWasFun) {
                computerScreen.doTalk("Alright, that was fun{WAIT=3} ", true, null, () ->
                        computerScreen.doTalk("Hey can I real-talk with you for a second?{WAIT=1} <ACTIVATING REAL-TALK MODE...>{WAIT=3} ", false, null, () ->
                            computerScreen.doTalk("If you see a big red button called 'SELF-DESTRUCT'..{WAIT=2} DON'T PRESS IT{WAIT}.{WAIT}.{WAIT}.{WAIT=3} please{WAIT=2} ", false, null, () -> {
                                computerScreen.doTalk("<DEACTIVATING REAL-TALK MODE...>{WAIT=2} Anyways real talk over. Time to go back to being annoying{WAIT=2} If you could completely ignore the thing that I'm about to show you it will be very appreciated, Thanks!{WAIT=3} ", null, () -> {
                                    computerScreen.setState(ComputerState.MOVE_THROUGH_HINT);
                                });
                            })
                                ));
                didThatWasFun = true;
            }
        });
    }

    Wall[] gravityWalls;

    public void setupGravityFlipPlatforms() {
        gravityWalls = new Wall[] {
                addWall(150, HEIGHT / 3, 50, 10)
        };
    }

    public void playEndAnimation() {
        gmh.dispose();
        ga.transitionTo(new EndAnimationScreen(ga), Transitions.linearFade(5, batch, shapeRenderer));
    }
}
