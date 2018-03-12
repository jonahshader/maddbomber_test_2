package com.jonahshader.maddbomber.NonObjects;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/*
targets array: true = target, false = not target
obstacles array: true = obstacle, false = clear
 */
public class PathFinder {
    public static ArrayList<Point> findPath(boolean[][] targets, boolean[][] obstacles) {
        ArrayList<Point> path = new ArrayList<>();

        return path;
    }

    //Contains the cellular automata process
    class CellularAutomata{
        private ArrayList<Cell> cells;
        private ArrayList<Point> path;
        private final boolean[][] targets, obstacles;
        private boolean[][] usedCells;

        public CellularAutomata(final boolean[][] targets, final boolean[][] obstacles) {
            this.targets = targets;
            this.obstacles = obstacles;
            cells = new ArrayList<>();
            path = new ArrayList<>();
//            usedCells = Arrays.copyOf(obstacles, );
            //TODO: copy obstacles to usedCells
        }

        class Cell{
            int x, y;
            Cell parentCell;
            CellularAutomata container;
            boolean canSpread;

            public Cell(int x, int y, Cell parentCell, CellularAutomata container) {
                this.x = x;
                this.y = y;
                this.parentCell = parentCell;
                this.container = container;

                if (targets[x][y]) {
                    path.add(new Point(x, y));
                    parentCell.addToPath();
                }
            }

            private void addToPath() {
                path.add(new Point(x, y));
                if (parentCell != null) {
                    parentCell.addToPath();
                } else {
                    //???? done
                    System.out.println("Finished path");
                }
            }

        }
    }

//    private class PathCell {
//        private int tileX, tileY;
//        private PathCell parentCell;
//        private boolean[][] safeZones, walls;
//        ArrayList<PathCell> otherCells, finalPath;
//        boolean canSpread;
//
//        private PathCell(int tileX, int tileY, PathCell parentCell, final boolean[][] safeZones, final boolean[][] walls) {
//            this.tileX = tileX;
//            this.tileY = tileY;
//            this.parentCell = parentCell;
//            this.safeZones = safeZones;
//            this.walls = walls;
//            canSpread = true;
//        }
//
//        public PathCell(int tileX, int tileY, final boolean[][] safeZones, final boolean[][] walls) {
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
