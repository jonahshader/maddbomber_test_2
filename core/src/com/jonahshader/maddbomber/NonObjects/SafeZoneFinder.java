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
        boolean[][] safeZones = gameWorld.getCollidables();
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

    public ArrayList<Point> getPathToSafety(int tileX, int tileY, GameWorld gameWorld) {
        ArrayList<Point> finalPath = new ArrayList<>();
//        PathCell masterPathCell = new PathCell(tileX, tileY, findSafeZones(gameWorld), gameWorld.getCollidables());
//        finalPath = masterPathCell.findPath();

        return finalPath;
    }

//    private class PathCell {
//        private int tileX, tileY;
//        private PathCell parentCell;
//        private boolean[][] safeZones, walls;
//        ArrayList<PathCell> otherCells, finalPath;
//        boolean canSpread;
//
//        private PathCell(int tileX, int tileY, PathCell parentCell, boolean[][] safeZones, boolean[][] walls) {
//            this.tileX = tileX;
//            this.tileY = tileY;
//            this.parentCell = parentCell;
//            this.safeZones = safeZones;
//            this.walls = walls;
//            canSpread = true;
//        }
//
//        public PathCell(int tileX, int tileY, boolean[][] safeZones, boolean[][] walls) {
//            this.tileX = tileX;
//            this.tileY = tileY;
//            this.parentCell = null;
//            this.safeZones = safeZones;
//            this.walls = walls;
//            otherCells = new ArrayList<>();
//            finalPath = new ArrayList<>();
//            canSpread = true;
//        }
//
//        public boolean isSafeZone() {
//            return !safeZones[tileX][tileY];
//        }
//
//        public boolean canSpread() {
//            return canSpread;
//        }
//
//        public void addAllParentsToArray(ArrayList<PathCell> cellArray) {
//            cellArray.add(this);
//            parentCell.addAllParentsToArray(cellArray);
//        }
//
//        //Return true if successful, return false if didn't spread (because
//        public void run() {
//            if (!isSafeZone() && canSpread) { //if not safe && we can still spread
//                canSpread = false;
//                if (!walls[tileX - 1][tileY]) {
//                    //Create cell to the left
//                    otherCells.add(new PathCell(tileX - 1, tileY, this, safeZones, walls));
//                    canSpread = true;
//                }
//                if (!walls[tileX + 1][tileY]) {
//                    //Create cell to the right
//                    otherCells.add(new PathCell(tileX + 1, tileY, this, safeZones, walls));
//                    canSpread = true;
//                }
//                if (!walls[tileX][tileY -1]) {
//                    //Create cell to the bottom
//                    otherCells.add(new PathCell(tileX, tileY - 1, this, safeZones, walls));
//                    canSpread = true;
//                }
//                if (!walls[tileX][tileY + 1]) {
//                    //Create cell to the top
//                    otherCells.add(new PathCell(tileX, tileY + 1, this, safeZones, walls));
//                    canSpread = true;
//                }
//            }
//        }
//    }
}
