package com.jonahshader.maddbomber.MatchSystems;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.jonahshader.maddbomber.GameWorld;
import com.jonahshader.maddbomber.Player;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class PlayerSpawner {
    private final static float RESPAWN_TIME = 1.5f;
    private boolean respawning;
    private float timeRemaining = 1.5f;

    private GameWorld gameWorld; //Where the players are spawned
    private Player parentPlayer;
    private int playerId;

    public PlayerSpawner(GameWorld gameWorld, Player parentPlayer) {
        this.gameWorld = gameWorld;
        this.parentPlayer = parentPlayer;
        respawning = false;
    }

    public void run(float dt) {
        if (respawning) {
            timeRemaining -= dt;
            if (timeRemaining <= 0) {
                timeRemaining = 0;
                respawning = false;
                spawnPlayer();
            }
        }
    }

    public void requestRespawn() {
        respawning = true;
        timeRemaining = RESPAWN_TIME;
    }

    private void spawnPlayer() {
        boolean[][] spawnableArea = GameWorld.getCollidables(gameWorld.getMap());
        ArrayList<Point> spawnableLocations = new ArrayList<>();

        for (int x = 0; x < spawnableArea.length; x++) {
            for (int y = 0; y < spawnableArea[0].length; y++) {
                if (!spawnableArea[x][y]) {
                    spawnableLocations.add(new Point(x, y));
                }
            }
        }
        Point selectedLocation = spawnableLocations.get((int) (Math.random() * spawnableLocations.size()));
        parentPlayer.respawn(selectedLocation.x, selectedLocation.y);
    }
}