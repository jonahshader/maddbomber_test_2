package com.jonahshader.maddbomber;

import com.badlogic.gdx.graphics.Color;
import com.jonahshader.maddbomber.GameItems.Bomb;
import com.jonahshader.maddbomber.GameItems.Explosion;
import com.jonahshader.maddbomber.GameItems.Pickups.Pickup;

import static com.jonahshader.maddbomber.MaddBomber.TILE_SIZE;

public class AIPlayer extends Player {

    double waitingPeriod = 0;

    public AIPlayer(int tileX, int tileY, ControlProfile controlProfile, GameWorld gameWorld, MaddBomber game, int playerId, Color playerColor) {
        super(tileX, tileY, controlProfile, gameWorld, game, playerId, playerColor);
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
        super.kill(killer, cause);
        waitingPeriod = 0;
    }

    @Override
    public void run(float dt) {
        Player target = null;
        double lowestDistance = 999999999;
        double xDist = 0;
        double yDist = 0;
        for (Player player : gameWorld.getPlayers()) {
            if (player != this && player.isSpawned()) {
                double distance = Math.sqrt(Math.pow(player.x - x, 2) + Math.pow(player.y - y, 2));
                if (distance < lowestDistance) {
                    lowestDistance = distance;
                    xDist = player.x - x;
                    yDist = player.y - y;
                    target = player;
                }
            }
        }

        boolean pickupClosest = false;
        Pickup closestPickup = null;
        for (Pickup pickup : gameWorld.getPickups()) {
            double pickupX = pickup.getTileX() * TILE_SIZE + (TILE_SIZE / 2);
            double pickupY = pickup.getTileY() * TILE_SIZE + (TILE_SIZE / 2);
            double distance = Math.sqrt(Math.pow(pickupX - x, 2) + Math.pow(pickupY - y, 2));
            if (distance < lowestDistance) {
                pickupClosest = true;
                closestPickup = pickup;
                lowestDistance = distance;
                xDist = pickupX - x;
                yDist = pickupY - y;
            }
        }

        if (lowestDistance < 2 * TILE_SIZE && !pickupClosest) {
            createBomb();
        }

        if (waitingPeriod <= 0) {
            if (xSpeed == 0 && ySpeed == 0) {
                if (getBombsDeployed() == 0) {
                    if (Math.random() < 0.1 * dt) { //TODO: this sucks. replace it with a timer or something smarter
                        createBomb();
                    }
                } else {
                    resetWaitingPeriod();
                }
            }
        }

        double targetX = 0;
        double targetY = 0;

        if (pickupClosest) {
            targetX = closestPickup.getTileX() * TILE_SIZE + (TILE_SIZE / 2);
            targetY = closestPickup.getTileY() * TILE_SIZE + (TILE_SIZE / 2);
        } else {
            if (target != null) {
                targetX = target.x;
                targetY = target.y;
            }
        }

        //reset all keys beforehand
        upKeyDown = false;
        downKeyDown = false;
        leftKeyDown = false;
        rightKeyDown = false;

        if (getBombsDeployed() == 0) {
            if (Math.abs(yDist) > TILE_SIZE || Math.abs(xDist) < TILE_SIZE) {
                if (targetY > y) {
                    upKeyDown = true;
                    downKeyDown = false;
                } else {
                    upKeyDown = false;
                    downKeyDown = true;
                }
            }
            if (Math.abs(xDist) > TILE_SIZE || Math.abs(yDist) < TILE_SIZE) {
                if (targetX > x) {
                    rightKeyDown = true;
                    leftKeyDown = false;
                } else {
                    rightKeyDown = false;
                    leftKeyDown = true;
                }
            }
        } else {
            if (Math.abs(yDist) > TILE_SIZE || Math.abs(xDist) < TILE_SIZE) {
                if (targetY > y) {
                    upKeyDown = false;
                    downKeyDown = true;
                } else {
                    upKeyDown = true;
                    downKeyDown = false;
                }
            }
            if (Math.abs(xDist) > TILE_SIZE || Math.abs(yDist) < TILE_SIZE) {
                if (targetX > x) {
                    rightKeyDown = false;
                    leftKeyDown = true;
                } else {
                    rightKeyDown = true;
                    leftKeyDown = false;
                }
            }
        }

        //Wait for explosion to finish
        if (waitingPeriod >= 0) {
            upKeyDown = false;
            downKeyDown = false;
            leftKeyDown = false;
            rightKeyDown = false;
            waitingPeriod -= dt;
        }
        super.run(dt);
    }

    private void resetWaitingPeriod() {
        waitingPeriod = Bomb.FUSE_TIME_MAX + Explosion.EXPLOSION_TIME;
    }
}
