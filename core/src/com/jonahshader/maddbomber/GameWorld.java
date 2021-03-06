package com.jonahshader.maddbomber;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.jonahshader.maddbomber.GameItems.Bomb;
import com.jonahshader.maddbomber.GameItems.Explosion;
import com.jonahshader.maddbomber.GameItems.Pickups.Pickup;
import com.jonahshader.maddbomber.NonObjects.ExplosionPropagator;

import java.awt.*;
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
        for (Pickup pickup : pickups) {
            pickup.run(deltaTime);
        }
        explosions.removeIf(Explosion::isUsed);
        bombs.removeIf(Bomb::isUsed);
        pickups.removeIf(Pickup::isUsed);
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

        for (Pickup pickup : pickups) {
            pickup.draw(batch);
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

    /**
     *
     * @return true = collidable, false = free space
     */
    public boolean[][] getCollidables() {
        TiledMapTileLayer walls = (TiledMapTileLayer) map.getLayers().get("Walls");
        TiledMapTileLayer explodable = (TiledMapTileLayer) map.getLayers().get("Explodable");
        int width = map.getProperties().get("width", Integer.class);
        int height = map.getProperties().get("height", Integer.class);
        boolean[][] collidables = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                collidables[x][y] = walls.getCell(x, y) != null || explodable.getCell(x, y) != null;
            }
        }

        for (Bomb bomb : bombs) {
            collidables[bomb.getTileX()][bomb.getTileY()] = true;
        }

        return collidables;
    }

    public boolean[][] getExplodables() {
        TiledMapTileLayer explodable = (TiledMapTileLayer) map.getLayers().get("Explodable");
        int width = map.getProperties().get("width", Integer.class);
        int height = map.getProperties().get("height", Integer.class);
        boolean[][] collidables = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                collidables[x][y] = explodable.getCell(x, y) != null;
            }
        }

        return collidables;
    }

    public ArrayList<Point> getWalkableSpace() {
        boolean[][] spawnableArea = getCollidables();
        ArrayList<Point> spawnableLocations = new ArrayList<>();

        for (int x = 0; x < spawnableArea.length; x++) {
            for (int y = 0; y < spawnableArea[0].length; y++) {
                if (!spawnableArea[x][y]) {
                    spawnableLocations.add(new Point(x, y));
                }
            }
        }
        return spawnableLocations;
    }

    /**
     *
     * @return true = safe, false = unsafe
     */
    public boolean[][] findSafeZones() {
        boolean[][] safeZones = getCollidables(); //Start off with all of the collidables in the game, with true being a collidable.
        ArrayList<Point> futureExplosions = new ArrayList<>(); //arraylist to store all future explosion coordinates
        for (Bomb bomb : getBombs()) { //loop through all bombs in the game
            futureExplosions.addAll(ExplosionPropagator.getExplosionPattern(bomb.getTileX(), bomb.getTileY(), bomb.getExplosionSize(), this)); //add each bomb's explosion propagation to the arraylist
            safeZones[bomb.getTileX()][bomb.getTileY()] = true; //also add the bomb coordinate as an unsafe location
        }
        for (Point point : futureExplosions) { //convert the arraylist of coordinates and add it to the 2d boolean array
            safeZones[(int) point.getX()][(int) point.getY()] = true;
        }

        for (Explosion explosion : explosions) {
            safeZones[explosion.getTileX()][explosion.getTileY()] = true;
        }

        //Invert the array so that true = safe and false = unsafe. (this makes this method mesh better with the path finder)
        for (int i = 0; i < safeZones.length; i++) {
            for (int j = 0; j < safeZones[i].length; j++) {
                safeZones[i][j] = !safeZones[i][j];
            }
        }

        return safeZones;
    }

    @Override
    public void dispose() {
        map.dispose();
        //TODO: dispose items n stuff
    }
}
