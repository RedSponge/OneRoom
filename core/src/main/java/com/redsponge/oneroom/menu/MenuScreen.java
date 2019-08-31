package com.redsponge.oneroom.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.oneroom.EndAnimationComputer;
import com.redsponge.oneroom.GameScreen;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;

import static com.redsponge.oneroom.GameScreen.ROOM_HEIGHT;
import static com.redsponge.oneroom.GameScreen.ROOM_WIDTH;
import static com.redsponge.oneroom.GameScreen.WALLS_COLOR;

public class MenuScreen extends AbstractScreen {

    private EndAnimationComputer computer;

    private FitViewport viewport;

    private static final int WIDTH = GameScreen.WIDTH;
    private static final int HEIGHT = GameScreen.HEIGHT;

    private Texture wood;

    private LightSystem ls;

    private Array<Rectangle> walls;

    private Texture instructions;
    private boolean showInstructions = true;
    private float counter;

    private Music menu;

    public MenuScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        viewport = new FitViewport(WIDTH, HEIGHT);
        addSystem(LightSystem.class, WIDTH, HEIGHT, batch);

        ls = getSystem(LightSystem.class);
        ls.registerLightType(LightType.ADDITIVE);

        computer = new EndAnimationComputer(batch, shapeRenderer);
        computer.turnOff();
        addEntity(computer);

        wood = assets.get("ironBackground", Texture.class);
        instructions = assets.get("instructions", Texture.class);

        walls = new Array<>();
        addWall(-WIDTH * 2, 0, WIDTH * 5, (HEIGHT - ROOM_HEIGHT) / 2);
        addWall(-WIDTH * 2, (ROOM_HEIGHT + (HEIGHT - ROOM_HEIGHT) / 2), WIDTH * 5, (HEIGHT - ROOM_HEIGHT) / 2);

        addWall(-WIDTH, 60, (WIDTH - ROOM_WIDTH) / 2 + WIDTH, HEIGHT);
        addWall((ROOM_WIDTH + (WIDTH - ROOM_WIDTH) / 2), 60, WIDTH + (WIDTH - ROOM_WIDTH), HEIGHT);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if(transitioning) return false;
                ga.transitionTo(new GameScreen(ga, false), Transitions.sineSlide(1, batch, shapeRenderer));
                return true;
            }
        });

        menu = assets.get("music", Music.class);
        menu.setLooping(true);
        menu.play();
    }

    private void addWall(int i, int i1, int i2, int i3) {
        walls.add(new Rectangle(i, i1, i2, i3));
    }

    @Override
    public void tick(float v) {
        counter+=v;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        int w = (WIDTH * 5) / wood.getWidth();
        int h = (HEIGHT * 5) / wood.getHeight();
        for(int i = (-WIDTH * 2) / wood.getWidth(); i < w; i++) {
            for(int j = (-HEIGHT * 2) / wood.getHeight(); j < h; j++) {
                batch.draw(wood, i * wood.getWidth(), j * wood.getHeight());
            }
        }
        renderEntities();
        float progress = Math.min(counter / 5f, 1);
        batch.setColor(1, 1, 1, progress);
        batch.draw(instructions, WIDTH / 2f - instructions.getWidth() / 2f, HEIGHT / 8f);
        batch.setColor(Color.WHITE);
        batch.end();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setColor(WALLS_COLOR);
        shapeRenderer.begin(ShapeType.Filled);
        for (Rectangle wall : walls) {
            shapeRenderer.rect(wall.x, wall.y, wall.width, wall.height);
        }
        shapeRenderer.end();

        ls.prepareMap(LightType.ADDITIVE, viewport);
        ls.renderToScreen(LightType.ADDITIVE);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        ls.resize(width, height);
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return MenuAssets.class;
    }
}
