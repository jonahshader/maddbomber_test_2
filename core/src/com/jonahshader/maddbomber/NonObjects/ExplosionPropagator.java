package com.jonahshader.maddbomber.NonObjects;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.jonahshader.maddbomber.GameWorld;

import java.awt.*;
import java.util.ArrayList;

public class ExplosionPropagator {
    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        ALL
    }

    public static ArrayList<Point> getExplosionPattern(int tileX, int tileY, int explosionRadius, GameWorld gameWorld) {
        ArrayList<Point> explosionPoints = new ArrayList<>(explosionRadius);
        TiledMapTileLayer explodables = (TiledMapTileLayer) gameWorld.getMap().getLayers().get("Explodable");
        TiledMapTileLayer walls = (TiledMapTileLayer) gameWorld.getMap().getLayers().get("Walls");
        return explosionHelper(tileX, tileY, explosionRadius, Direction.ALL, explosionPoints, explodables, walls);
    }

    private static ArrayList<Point> explosionHelper(int tileX, int tileY, int explosionsRemaining, Direction direction, ArrayList<Point> explosionPts, TiledMapTileLayer explodables, TiledMapTileLayer walls) {
        boolean hitExplodable = explodables.getCell(tileX, tileY) != null;
        //Add this to the list of explosion locations
        explosionPts.add(new Point(tileX, tileY));

        //create additional explosions
        if (!hitExplodable) {
            if (explosionsRemaining > 0) {
                switch (direction) {
                    case LEFT:
                        if (walls.getCell(tileX - 1, tileY) == null) {
                            explosionHelper(tileX - 1, tileY, explosionsRemaining - 1, direction, explosionPts, explodables, walls);
//                            spreaders.add(new ExplosionSpreader(tileX - 1, tileY, explosionsRemaining - 1, direction, gameWorld, spreaders));
                        }
                        break;
                    case RIGHT:
                        if (walls.getCell(tileX + 1, tileY) == null) {
                            explosionHelper(tileX + 1, tileY, explosionsRemaining - 1, direction, explosionPts, explodables, walls);
//                            spreaders.add(new ExplosionSpreader(tileX + 1, tileY, explosionsRemaining - 1, direction, gameWorld, spreaders));
                        }
                        break;
                    case UP:
                        if (walls.getCell(tileX, tileY + 1) == null)
                            explosionHelper(tileX, tileY + 1, explosionsRemaining - 1, direction, explosionPts, explodables, walls);
//                            spreaders.add(new ExplosionSpreader(tileX, tileY + 1, explosionsRemaining - 1, direction, gameWorld, spreaders));
                        break;
                    case DOWN:
                        if (walls.getCell(tileX, tileY - 1) == null)
                            explosionHelper(tileX, tileY - 1, explosionsRemaining - 1, direction, explosionPts, explodables, walls);
//                            spreaders.add(new ExplosionSpreader(tileX, tileY - 1, explosionsRemaining - 1, direction, gameWorld, spreaders));
                        break;
                    case ALL:
                        if (walls.getCell(tileX - 1, tileY) == null)
                            explosionHelper(tileX - 1, tileY, explosionsRemaining - 1, Direction.LEFT, explosionPts, explodables, walls);
//                            spreaders.add(new ExplosionSpreader(tileX - 1, tileY, explosionsRemaining - 1, Direction.LEFT, gameWorld, spreaders));
                        if (walls.getCell(tileX + 1, tileY) == null)
                            explosionHelper(tileX + 1, tileY, explosionsRemaining - 1, Direction.RIGHT, explosionPts, explodables, walls);
//                            spreaders.add(new ExplosionSpreader(tileX + 1, tileY, explosionsRemaining - 1, Direction.RIGHT, gameWorld, spreaders));
                        if (walls.getCell(tileX, tileY + 1) == null)
                            explosionHelper(tileX, tileY + 1, explosionsRemaining - 1, Direction.UP, explosionPts, explodables, walls);
//                            spreaders.add(new ExplosionSpreader(tileX, tileY + 1, explosionsRemaining - 1, Direction.UP, gameWorld, spreaders));
                        if (walls.getCell(tileX, tileY - 1) == null)
                            explosionHelper(tileX, tileY - 1, explosionsRemaining - 1, Direction.DOWN, explosionPts, explodables, walls);
//                            spreaders.add(new ExplosionSpreader(tileX, tileY - 1, explosionsRemaining - 1, Direction.DOWN, gameWorld, spreaders));
                        break;
                }
            }
        }
        return explosionPts;
    }
}
