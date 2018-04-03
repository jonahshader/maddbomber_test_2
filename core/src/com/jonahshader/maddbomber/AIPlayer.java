package com.jonahshader.maddbomber;

import com.badlogic.gdx.graphics.Color;
import com.jonahshader.maddbomber.GameItems.Bomb;
import com.jonahshader.maddbomber.GameItems.Explosion;
import com.jonahshader.maddbomber.GameItems.Pickups.Pickup;
import com.jonahshader.maddbomber.PathFinder.PathFinder;
import com.jonahshader.maddbomber.PathFinder.PointInt;

import java.util.ArrayList;

import static com.jonahshader.maddbomber.MaddBomber.TILE_SIZE;
import static com.jonahshader.maddbomber.MatchSystems.Match.invertBooleanArray;

public class AIPlayer extends Player {

    public final int TILE_MOVE_THRESHOLD = 4;
    private double bombSpawnTimeout;

    AIState state;

    public enum AIState {
        PURSUING_PLAYER,
        PURSUING_PICKUP,
        AVOIDING_DEATH,
        BLOWING_UP_STUFF
    }

    public AIPlayer(int tileX, int tileY, ControlProfile controlProfile, GameWorld gameWorld, MaddBomber game, int playerId, Color playerColor) {
        super(tileX, tileY, controlProfile, gameWorld, game, playerId, playerColor);
        bombSpawnTimeout = 0;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void kill(Player killer, String cause) {
        System.out.println(state);
        super.kill(killer, cause);
    }

    @Override
    public void run(float dt) {
        Player target = null;
        double lowestDistance = 999999999;
        for (Player player : gameWorld.getPlayers()) {
            if (player != this && player.isSpawned()) {
                double distance = Math.sqrt(Math.pow(player.x - x, 2) + Math.pow(player.y - y, 2));
                if (distance < lowestDistance) {
                    lowestDistance = distance;
                    target = player;
                }
            }
        }

        boolean pickupClosest = false;
        state = AIState.PURSUING_PLAYER;
        Pickup closestPickup = null;
        for (Pickup pickup : gameWorld.getPickups()) {
            double pickupX = pickup.getTileX() * TILE_SIZE + (TILE_SIZE / 2);
            double pickupY = pickup.getTileY() * TILE_SIZE + (TILE_SIZE / 2);
            double distance = Math.sqrt(Math.pow(pickupX - x, 2) + Math.pow(pickupY - y, 2));
            if (distance < lowestDistance) {
                pickupClosest = true;
                state = AIState.PURSUING_PICKUP;
                closestPickup = pickup;
                lowestDistance = distance;
            }
        }

        if (lowestDistance < 1.4 * TILE_SIZE && !pickupClosest) {
            createBomb();
        }

        if (getBombsDeployed() > 0) {
            state = AIState.AVOIDING_DEATH;
        }


        ArrayList<PointInt> path = null;
        boolean[][] collidables = gameWorld.getCollidables();
//        invertBooleanArray(collidables);
        for (Explosion explosion : gameWorld.getExplosions()) {
            collidables[explosion.getTileX()][explosion.getTileY()] = true;
        }
        switch (state) {
            case PURSUING_PLAYER:
                path = PathFinder.findPath(new PointInt((int) (x / TILE_SIZE), (int) (y / TILE_SIZE)), new PointInt((int) (target.x / TILE_SIZE), (int) (target.y / TILE_SIZE)), collidables, mapTileWidth, mapTileHeight);
                break;
            case PURSUING_PICKUP:
                path = PathFinder.findPath(new PointInt((int) (x / TILE_SIZE), (int) (y / TILE_SIZE)), new PointInt(closestPickup.getTileX(), closestPickup.getTileY()), collidables, mapTileWidth, mapTileHeight);
                break;
            case AVOIDING_DEATH:
                boolean[][] tempCollidables = gameWorld.getCollidables();
                for (Explosion explosion : gameWorld.getExplosions()) {
                    tempCollidables[explosion.getTileX()][explosion.getTileY()] = true;
                }
                path = PathFinder.findPath(new PointInt((int) (x / TILE_SIZE), (int) (y / TILE_SIZE)), gameWorld.findSafeZones(), tempCollidables, mapTileWidth, mapTileHeight);
                break;
            default:
                break;
        }

        if (path == null && state != AIState.AVOIDING_DEATH) {
            state = AIState.BLOWING_UP_STUFF;
            boolean[][] explodables = gameWorld.getExplodables();
            boolean[][] spotsToBomb = new boolean[mapTileWidth][mapTileHeight];
            for (int x = 1; x < mapTileWidth - 1; x++) {
                for (int y = 1; y < mapTileHeight - 1; y++) {
                    spotsToBomb[x][y] = explodables[x][y] || explodables[x + 1][y] || explodables[x - 1][y] || explodables[x][y + 1] || explodables[x][y - 1];
                }
            }

            boolean[][] safeSpots = gameWorld.findSafeZones();
            invertBooleanArray(safeSpots);
            for (Explosion explosion : gameWorld.getExplosions()) {
                safeSpots[explosion.getTileX()][explosion.getTileY()] = false;
            }
            path = PathFinder.findPath(new PointInt((int) (x / TILE_SIZE), (int) (y / TILE_SIZE)), spotsToBomb, safeSpots, mapTileWidth, mapTileHeight);
        }

        double targetX = 0;
        double targetY = 0;
        boolean dontMove = false;

        double closestPathPointDistance = 999999;
        int closestPathPointIndex = 0;
        if (path != null) {
            for (int i = 0; i < path.size(); i++) {
                PointInt pointInt = path.get(i);
                double cellX = pointInt.x * TILE_SIZE + TILE_SIZE / 2;
                double cellY = pointInt.y * TILE_SIZE + TILE_SIZE / 2;
                double tempDist = Math.pow(cellX - x, 2);
                tempDist += Math.pow(cellY - y, 2);
                tempDist = Math.sqrt(tempDist);
                if (tempDist < closestPathPointDistance) {
                    closestPathPointDistance = tempDist;
                    closestPathPointIndex = i;
                }
            }
            if (closestPathPointIndex > 0) {
                targetX = (path.get(closestPathPointIndex - 1).x * TILE_SIZE) + (TILE_SIZE / 2);
                targetY = (path.get(closestPathPointIndex - 1).y * TILE_SIZE) + (TILE_SIZE / 2);
                double tempTargetDist = Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2));
                if (tempTargetDist > TILE_SIZE) {
                    targetX = (path.get(closestPathPointIndex).x * TILE_SIZE) + (TILE_SIZE / 2);
                    targetY = (path.get(closestPathPointIndex).y * TILE_SIZE) + (TILE_SIZE / 2);
                }
            } else {
                targetX = (path.get(closestPathPointIndex).x * TILE_SIZE) + (TILE_SIZE / 2);
                targetY = (path.get(closestPathPointIndex).y * TILE_SIZE) + (TILE_SIZE / 2);

                double tempTargetDist = Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2));
                if (tempTargetDist < TILE_MOVE_THRESHOLD) {
                    dontMove = true;
                }

                if (state == AIState.BLOWING_UP_STUFF) {
                    if (bombSpawnTimeout <= 0) {
                        createBomb();
                        bombSpawnTimeout = Explosion.EXPLOSION_TIME + Bomb.FUSE_TIME_MAX;
                    }
                }
            }

        } else {
            if (state == AIState.BLOWING_UP_STUFF) {
                System.out.println("bad things");
            }
            dontMove = true;
        }


        //reset all keys beforehand
        upKeyDown = false;
        downKeyDown = false;
        leftKeyDown = false;
        rightKeyDown = false;

        double xDist = targetX - x;
        double yDist = targetY - y;


        if (!dontMove) {
            if (Math.abs(yDist) > TILE_MOVE_THRESHOLD || Math.abs(xDist) < TILE_MOVE_THRESHOLD) {
                if (targetY > y) {
                    upKeyDown = true;
                    downKeyDown = false;
                } else {
                    upKeyDown = false;
                    downKeyDown = true;
                }
            }
            if (Math.abs(xDist) > TILE_MOVE_THRESHOLD || Math.abs(yDist) < TILE_MOVE_THRESHOLD) {
                if (targetX > x) {
                    rightKeyDown = true;
                    leftKeyDown = false;
                } else {
                    rightKeyDown = false;
                    leftKeyDown = true;
                }
            }
        } else {
            upKeyDown = false;
            downKeyDown = false;
            leftKeyDown = false;
            rightKeyDown = false;
        }

//        System.out.println(state);

        if (bombSpawnTimeout > 0) {
            bombSpawnTimeout -= dt;
        }

        super.run(dt);
    }

    @Override
    protected void resetStats() {
        super.resetStats();
    }
}
