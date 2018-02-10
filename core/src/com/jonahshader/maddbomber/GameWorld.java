package com.jonahshader.maddbomber;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.jonahshader.maddbomber.GameItems.Bomb;
import com.jonahshader.maddbomber.GameItems.Explosion;
import com.jonahshader.maddbomber.GameItems.Pickups.Pickup;

import java.util.ArrayList;

public class GameWorld implements Disposable {
    //Game map stuff
    private TiledMap map;
    private MapProperties mapProperties;

    //Game objects/items
    private ArrayList<Bomb> bombs;
    private ArrayList<Explosion> explosions;
    private ArrayList<Pickup> pickups;
    private ArrayList<Player> players;

    public GameWorld(String mapFileName) {
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        pickups = new ArrayList<>();
        players = new ArrayList<>();

        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapFileName);
        mapProperties = map.getProperties();
    }

    public void update(float deltaTime) {
        for (Player player : players) {
            player.run(deltaTime);
        }
        for (Bomb bomb : bombs) {
            bomb.run(deltaTime);
        }
        for (Explosion explosion : explosions) {
            explosion.run(deltaTime);
        }
        explosions.removeIf(Explosion::isUsed);
        bombs.removeIf(Bomb::isUsed);
        //TODO: update other stuff
    }

    public void draw(SpriteBatch batch) {
        for (Bomb bomb : bombs) {
            bomb.draw(batch);
        }

        for (Player player : players) {
            player.draw(batch);
        }

        for (Explosion explosion : explosions) {
            explosion.draw(batch);
        }
    }

    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
        for (Player player : players) {
            player.addBombToIgnore(bomb);
        }
    }

    public TiledMap getMap() {
        return map;
    }

    public MapProperties getMapProperties() {
        return mapProperties;
    }

    public ArrayList<Bomb> getBombs() {
        return bombs;
    }

    public ArrayList<Explosion> getExplosions() {
        return explosions;
    }

    public ArrayList<Pickup> getPickups() {
        return pickups;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    @Override
    public void dispose() {
        map.dispose();
        //TODO: dispose items n stuff
    }
}
