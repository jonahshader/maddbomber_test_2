package com.jonahshader.maddbomber.NonObjects;

import com.jonahshader.maddbomber.GameItems.Bomb;
import com.jonahshader.maddbomber.GameWorld;

import java.awt.*;
import java.util.ArrayList;

public class SafeZoneFinder {
    /**
     *
     * @param gameWorld
     * @return returns a 2d bool array with danger = true, safe = false
     */
    public static boolean[][] findSafeZones(GameWorld gameWorld) {
        boolean[][] safeZones = GameWorld.getCollidables(gameWorld.getMap());
        ArrayList<Point> futureExplosions = new ArrayList<>();
        for (Bomb bomb : gameWorld.getBombs()) {
            futureExplosions.addAll(ExplosionPropagator.getExplosionPattern(bomb.getTileX(), bomb.getTileY(), bomb.getExplosionSize(), gameWorld));
            safeZones[bomb.getTileX()][bomb.getTileY()] = true;
        }
        for (Point point : futureExplosions) {
            safeZones[(int) point.getX()][(int) point.getY()] = true;
        }

        return safeZones;
    }

    public static ArrayList<Point> getPathToSafety(int tileX, int tileY, GameWorld gameWorld) {
        ArrayList<Point> finalPath = new ArrayList<>();
        //TODO: a* path finder or cellular automata path finder (might be the same thing i froget how a* works)
        return finalPath;
    }
}
