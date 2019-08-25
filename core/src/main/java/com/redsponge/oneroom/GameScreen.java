package com.redsponge.oneroom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.physics.PSolid;
import com.redsponge.redengine.physics.PhysicsDebugRenderer;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.utils.GameAccessor;

public class GameScreen extends AbstractScreen {

    public static final float ASPECT_RATIO = 16 / 9f;

    public static final int ROOM_WIDTH = 240*2;
    public static final int ROOM_HEIGHT = 160*2;

    public static final int WIDTH = 320*2;
    public static final int HEIGHT = (int) (WIDTH / ASPECT_RATIO);

    private static final Color WALLS_COLOR = new Color(0.9f, 0.9f, 0.9f, 1);
    private static final Color BACKGROUND_COLOR = new Color(237 / 255f, 237 / 255f, 237 / 255f, 1);

    private PhysicsDebugRenderer pdr;
    private PhysicsWorld pWorld;

    private FitViewport viewport;
    private Texture roomTexture;

    private DelayedRemovalArray<Wall> walls;

    private Player player;
    private Vector3 camTarget = new Vector3();

    public GameScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        viewport = new FitViewport(WIDTH, HEIGHT);
        roomTexture = assets.get("roomTexture", Texture.class);

        pWorld = new PhysicsWorld();
        walls = new DelayedRemovalArray<>();

        addWall(-WIDTH, 0, WIDTH * 3, (HEIGHT - ROOM_HEIGHT) / 2);
        addWall(-WIDTH, (ROOM_HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2), WIDTH * 3, (HEIGHT - ROOM_HEIGHT) / 2);

        addWall(-WIDTH, 60, (WIDTH - ROOM_WIDTH) / 2 + WIDTH, HEIGHT);
        addWall((ROOM_WIDTH + (WIDTH - ROOM_WIDTH) / 2), 60, WIDTH + (WIDTH - ROOM_WIDTH), HEIGHT);

        pdr = new PhysicsDebugRenderer();

        player = new Player(batch, shapeRenderer);
        addEntity(player);
    }

    private void addWall(int x, int y, int w, int h) {
        Wall wa = new Wall(pWorld, x, y, w, h);
        pWorld.addSolid(wa);
        walls.add(wa);
    }


    @Override
    public void tick(float v) {
        tickEntities(v);
        pWorld.update(v);

        if(player.getPos().x > ROOM_WIDTH + (WIDTH - ROOM_WIDTH) / 2 || player.getPos().x < (WIDTH - ROOM_WIDTH) / 2) {
            camTarget.set(player.getPos().x, HEIGHT / 2f, 0);
        } else {
            camTarget.set(WIDTH / 2f, HEIGHT / 2f, 0);
//            camTarget.set(player.getPos().x, HEIGHT / 2f, 0);
        }
        viewport.getCamera().position.lerp(camTarget, 0.1f);
    }


    boolean stageTwo;
    public void changeToStageTwo() {
        if(stageTwo) return;
        stageTwo = true;

        for (Wall wall : walls) {
            wall.remove();
            walls.removeValue(wall, true);
        }

        addWall(-WIDTH, 0, (int) (WIDTH * 1.4f), (HEIGHT - ROOM_HEIGHT) / 2);
        addWall((int) (WIDTH * 0.6f), 0, (int) (WIDTH * 1.4f), (HEIGHT - ROOM_HEIGHT) / 2);

        addWall(-WIDTH, (ROOM_HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2), WIDTH * 3, (HEIGHT - ROOM_HEIGHT) / 2);

        addWall(-WIDTH, 60, (WIDTH - ROOM_WIDTH) / 2 + WIDTH, HEIGHT);
        addWall((ROOM_WIDTH + (WIDTH - ROOM_WIDTH) / 2), 60, WIDTH + (WIDTH - ROOM_WIDTH), HEIGHT);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect(-WIDTH, -HEIGHT, WIDTH * 3, HEIGHT * 3);
        shapeRenderer.setColor(WALLS_COLOR);
        for (PSolid wall : walls) {
            shapeRenderer.rect(wall.pos.x, wall.pos.y, wall.size.x, wall.size.y);
        }
        shapeRenderer.end();

        renderRoom();
    }

    private void renderRoom() {
        float marginX = (WIDTH - ROOM_WIDTH) / 2f;
        float marginY = (HEIGHT - ROOM_HEIGHT) / 2f;
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(roomTexture, marginX, marginY, ROOM_WIDTH, ROOM_HEIGHT);
        batch.end();

        pdr.render(pWorld, viewport.getCamera().combined);
    }

    public Vector3 getCamPos() {
        return viewport.getCamera().position;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return GameAssets.class;
    }

    public PhysicsWorld getPhysicsWorld() {
        return pWorld;
    }
}
