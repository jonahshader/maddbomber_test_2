package com.jonahshader.maddbomber.PathFinder;

import java.util.ArrayList;

public class PathFinder {
    private static boolean reachedTarget, noPath;
    private static boolean[][] exploredArea;
    private static ArrayList<Cell> cells;
    private static PointInt[][] parents;
    private static int previousCellArraySize;
    private static PointInt pathEnd;



    public static ArrayList<PointInt> findPath(PointInt start, PointInt target, boolean[][] world, int width, int height) {
        boolean[][] targets = new boolean[width][height];
        targets[target.x][target.y] = true;
        return findPath(start, targets, world, width, height);
    }
    /**
     * @param start   the starting coordinate for the path
     * @param targets false: not target. true: target.
     * @param world   false: not an obstacle: true: is an obstacle
     * @param width   width of world
     * @param height  height of world
     * @return returns the path, or an empty arraylist if no path exists
     */
    public static ArrayList<PointInt> findPath(PointInt start, boolean[][] targets, boolean[][] world, int width, int height) {
        cells = new ArrayList<>();
        exploredArea = new boolean[width][height];
        parents = new PointInt[width][height];
        reachedTarget = false;
        noPath = false;
        previousCellArraySize = 1;
        cells.add(new Cell(start.x, start.y, start.x, start.y));
        exploredArea[start.x][start.y] = true;

        while (!reachedTarget && !noPath) {
            spread(width, height, world, targets);
        }

        if (noPath) {
            return null;
        } else {
            ArrayList<PointInt> path = new ArrayList<>();
            PointInt parent = parents[pathEnd.x][pathEnd.y];
            boolean foundStart = false;
            path.add(new PointInt(pathEnd));
            while (!foundStart) {
                if (parent == null) {
                    foundStart = true;
                } else {
                    path.add(new PointInt(parent.x, parent.y));
                    parent = parents[parent.x][parent.y];
                }
            }
            return path;
        }
    }

    private static void spread(int width, int height, boolean[][] world, boolean[][] targets) {
        boolean[][] newArea = new boolean[width][height];
        for (int i = cells.size() - 1; i >= 0; i--) {
            Cell tempCell = cells.get(i);
            int x = tempCell.x;
            int y = tempCell.y;
            if (x > 0 && x < width - 1 && y > 0 && y < height - 1) {
                if (tempCell.spreaded) {
                    break;
                }
                if (targets[x][y] && exploredArea[x][y]) {
                    //DONE!
                    pathEnd = new PointInt(x, y);
//                    System.out.println("Reached target.");
                    reachedTarget = true;
                } else {
                    if (exploredArea[x][y] && !newArea[x][y]) {
                        if (!exploredArea[x + 1][y]) {
                            if (!world[x + 1][y]) {
                                exploredArea[x + 1][y] = true;
                                newArea[x + 1][y] = true;
                                parents[x + 1][y] = new PointInt(x, y);
                                cells.add(new Cell(x + 1, y, x, y));
                            }
                        }
                        if (!exploredArea[x - 1][y]) {
                            if (!world[x - 1][y]) {
                                exploredArea[x - 1][y] = true;
                                newArea[x - 1][y] = true;
                                parents[x - 1][y] = new PointInt(x, y);
                                cells.add(new Cell(x - 1, y, x, y));
                            }
                        }
                        if (!exploredArea[x][y + 1]) {
                            if (!world[x][y + 1]) {
                                exploredArea[x][y + 1] = true;
                                newArea[x][y + 1] = true;
                                parents[x][y + 1] = new PointInt(x, y);
                                cells.add(new Cell(x, y + 1, x, y));
                            }
                        }
                        if (!exploredArea[x][y - 1]) {
                            if (!world[x][y - 1]) {
                                exploredArea[x][y - 1] = true;
                                newArea[x][y - 1] = true;
                                parents[x][y - 1] = new PointInt(x, y);
                                cells.add(new Cell(x, y - 1, x, y));
                            }
                        }
                    }
                }
            }
            tempCell.spreaded = true;
        }
        if (cells.size() == previousCellArraySize && !reachedTarget) {
            noPath = true;
//            System.out.println("No path to target.");
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    System.out.print((targets[x][y] ? 1 : 0) + " ");
//                }
//                System.out.println();
//            }
//            System.out.println();
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    System.out.print((world[x][y] ? 1 : 0) + " ");
//                }
//                System.out.println();
//            }
//            System.out.println();
        }
        previousCellArraySize = cells.size();
    }

}
