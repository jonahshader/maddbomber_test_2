package com.jonahshader.maddbomber.MatchSystems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jonahshader.maddbomber.GameWorld;
import com.jonahshader.maddbomber.MaddBomber;
import com.jonahshader.maddbomber.Player;
import com.jonahshader.maddbomber.Scenes.Hud;

public class Match implements Disposable{
    //Game systems stuff
    private MaddBomber game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
    private InputMultiplexer multiplexer;
    private int playerCount;

    //Map stuff
    private GameWorld gameWorld;
    private OrthogonalTiledMapRenderer mapRenderer;

    //TODO: put more stuff in this constructor, like a ControlProfile array, map, playerTexture array, etc.
    public Match(MaddBomber game, int playerCount) {
        this.game = game;
        this.playerCount = playerCount;
        gameCam = new OrthographicCamera();
        hud = new Hud(game.batch);
        gameWorld = new GameWorld("Maps/Sandstone.tmx");
        gamePort = new FitViewport(gameWorld.getMapProperties().get("width", Integer.class) * MaddBomber.TILE_SIZE, gameWorld.getMapProperties().get("height", Integer.class) * MaddBomber.TILE_SIZE, gameCam);
        mapRenderer = new OrthogonalTiledMapRenderer(gameWorld.getMap());
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //Use InputMultiplexer for input handling
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);

        //temp: add a player to the map
        addPlayer(new Player(
                1,
                1,
                game.controls.getControlProfile(0),
                gameWorld,
                game,
                0));

        addPlayer(new Player(
                2,
                1,
                game.controls.getControlProfile(1),
                gameWorld,
                game,
                1));

    }

    //first thing that runs in render method
    private void update(float dt) {
        gameWorld.update(dt);
        gameCam.update();
        mapRenderer.setView(gameCam);
    }

    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        gameWorld.draw(game.batch);
        game.batch.end();
    }

    public void resize(int width, int height) {
        //viewport needs to be updated
        gamePort.update(width, height);
    }

    public void addPlayer(Player player) {
        multiplexer.addProcessor(player);
        gameWorld.getPlayers().add(player);
    }

    public void removePlayer(Player player) {
        multiplexer.removeProcessor(player);
        gameWorld.getPlayers().remove(player);
    }

    private Vector3 getMousePosInGameWorld() {
        return gamePort.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    @Override
    public void dispose() {
    }

}