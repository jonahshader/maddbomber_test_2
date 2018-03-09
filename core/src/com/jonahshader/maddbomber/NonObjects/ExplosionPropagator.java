package com.jonahshader.maddbomber.NonObjects;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.jonahshader.maddbomber.GameWorld;

import java.util.ArrayList;

public class ExplosionPropagator {
    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        ALL
    }

    public class ExplosionSpreader{

        ExplosionSpreader(int tileX, int tileY, int explosionsRemaining, Direction direction, GameWorld gameWorld, ArrayList<ExplosionSpreader> spreaders) {
            TiledMapTileLayer explodables = (TiledMapTileLayer) gameWorld.getMap().getLayers().get("Explodable");
            TiledMapTileLayer walls = (TiledMapTileLayer) gameWorld.getMap().getLayers().get("Walls");

            boolean hitExplodable = explodables.getCell(tileX, tileY) != null;

            //create additional explosions
            if (!hitExplodable) {
                if (explosionsRemaining > 0) {
                    switch (direction) {
                        case LEFT:
                            if (walls.getCell(tileX - 1, tileY) == null) {
                                spreaders.add(new ExplosionSpreader(tileX - 1, tileY, explosionsRemaining - 1, direction, gameWorld, spreaders));
                            }
                            break;
                        case RIGHT:
                            if (walls.getCell(tileX + 1, tileY) == null) {
                                spreaders.add(new ExplosionSpreader(tileX + 1, tileY, explosionsRemaining - 1, direction, gameWorld, spreaders));
                            }
                            break;
                        case UP:
                            if (walls.getCell(tileX, tileY + 1) == null)
                                spreaders.add(new ExplosionSpreader(tileX, tileY + 1, explosionsRemaining - 1, direction, gameWorld, spreaders));
                            break;
                        case DOWN:
                            if (walls.getCell(tileX, tileY - 1) == null)
                                spreaders.add(new ExplosionSpreader(tileX, tileY - 1, explosionsRemaining - 1, direction, gameWorld, spreaders));
                            break;
                        case ALL:
                            if (walls.getCell(tileX - 1, tileY) == null)
                                spreaders.add(new ExplosionSpreader(tileX - 1, tileY, explosionsRemaining - 1, Direction.LEFT, gameWorld, spreaders));
                            if (walls.getCell(tileX + 1, tileY) == null)
                                spreaders.add(new ExplosionSpreader(tileX + 1, tileY, explosionsRemaining - 1, Direction.RIGHT, gameWorld, spreaders));
                            if (walls.getCell(tileX, tileY + 1) == null)
                                spreaders.add(new ExplosionSpreader(tileX, tileY + 1, explosionsRemaining - 1, Direction.UP, gameWorld, spreaders));
                            if (walls.getCell(tileX, tileY - 1) == null)
                                spreaders.add(new ExplosionSpreader(tileX, tileY - 1, explosionsRemaining - 1, Direction.DOWN, gameWorld, spreaders));
                            break;
                    }
                }
            }
        }
    }
}
