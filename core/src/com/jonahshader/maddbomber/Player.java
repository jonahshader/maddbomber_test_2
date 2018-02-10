package com.jonahshader.maddbomber;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.jonahshader.maddbomber.GameItems.Bomb;
import com.jonahshader.maddbomber.GameItems.Explosion;

import java.util.ArrayList;

public class Player implements InputProcessor{
    final static double SPEED_MAX = 7; // 1 tile per second
    final static double FRICTION_REGULAR = 15;
    //Center of player sprite
    //in world pixels (probably 32 pixels per tile)
    private double x, y, xSpeed, ySpeed;
    private double maxSpeedCurrent = 2.0;
    private double width = 28;
    private double height = 28;
    private int bombsDeployed = 0;
    private int maxDeployedBombs;
    private int explosionSize;

    private Sprite sprite;
    private TextureAtlas itemAtlas;
    private Rectangle hitbox;
    private TiledMap map;
    private MapProperties prop;
    private GameWorld gameWorld;
    private MaddBomber game;
    private int mapTileWidth;
    private int mapTileHeight;
    private int playerId;

    private ArrayList<Bomb> bombs;
    private ArrayList<Bomb> bombsIgnored; //bombs are put into here until the player moves off of them

    //controls
    private ControlProfile controlProfile;
    private boolean upKeyDown, downKeyDown, leftKeyDown, rightKeyDown, placeKeyDown, activateKeyDown;

    public Player(int tileX, int tileY, ControlProfile controlProfile, GameWorld gameWorld, MaddBomber game, int playerId) {
        this.controlProfile = controlProfile;
        this.map = gameWorld.getMap();
        this.itemAtlas = game.assets.manager.get(game.assets.itemAtlas, TextureAtlas.class);
        this.gameWorld = gameWorld;
        this.game = game;
        this.playerId = playerId;
        TextureRegion tr = new TextureRegion(itemAtlas.findRegion("player"));
        prop = map.getProperties();
        maxDeployedBombs = 2;
        explosionSize = 2;
        bombs = new ArrayList<>();
        bombsIgnored = new ArrayList<>();

        mapTileWidth = prop.get("width", Integer.class);
        mapTileHeight = prop.get("height", Integer.class);

        x = (tileX * MaddBomber.TILE_SIZE) + (MaddBomber.TILE_SIZE / 2);
        y = (tileY * MaddBomber.TILE_SIZE) + (MaddBomber.TILE_SIZE / 2);

        hitbox = new Rectangle(tileX * MaddBomber.TILE_SIZE, tileY * MaddBomber.TILE_SIZE, (float) width, (float) height);
        sprite = new Sprite(tr);
        sprite.setCenterX((float) x);
        sprite.setCenterY((float) y);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == controlProfile.upKey) {
            upKeyDown = true;
//            return true;
        } else if (keycode == controlProfile.downKey) {
            downKeyDown = true;
//            return true;
        } else if (keycode == controlProfile.leftKey) {
            leftKeyDown = true;
//            return true;
        } else if (keycode == controlProfile.rightKey) {
            rightKeyDown = true;
//            return true;
        } else if (keycode == controlProfile.placeKey) {
            createBomb();
            placeKeyDown = true;
//            return true;
        } else if (keycode == controlProfile.activateKey) {
            activateKeyDown = true;
//            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == controlProfile.upKey) {
            upKeyDown = false;
//            return true;
        } else if (keycode == controlProfile.downKey) {
            downKeyDown = false;
//            return true;
        } else if (keycode == controlProfile.leftKey) {
            leftKeyDown = false;
//            return true;
        } else if (keycode == controlProfile.rightKey) {
            rightKeyDown = false;
//            return true;
        } else if (keycode == controlProfile.placeKey) {
            placeKeyDown = false;
//            return true;
        } else if (keycode == controlProfile.activateKey) {
            activateKeyDown = false;
//            return true;
        }

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

    private void move(float dt) {
        double px = x;
        double py = y;
        double tempXSpd = 0;
        double tempYSpd = 0;

        tempXSpd += leftKeyDown ? -1 : 0;
        tempXSpd += rightKeyDown ? 1 : 0;
        tempYSpd += upKeyDown ? 1 : 0;
        tempYSpd += downKeyDown ? -1 : 0;

        //add temp speed from keyboard input to the player's actual speed
        xSpeed += tempXSpd * FRICTION_REGULAR * dt * 2;
        ySpeed += tempYSpd * FRICTION_REGULAR * dt * 2;

        //clamp values to max speed
        xSpeed = limit(xSpeed, -maxSpeedCurrent, maxSpeedCurrent);
        ySpeed = limit(ySpeed, -maxSpeedCurrent, maxSpeedCurrent);

        //apply friction to x
        if (xSpeed > 0) {
            if ((xSpeed - (dt * FRICTION_REGULAR)) > 0) {
                xSpeed -= (dt * FRICTION_REGULAR);
            } else {
                xSpeed = 0;
            }
        } else if (xSpeed < 0) {
            if ((xSpeed + (dt * FRICTION_REGULAR)) < 0) {
                xSpeed += (dt * FRICTION_REGULAR);
            } else {
                xSpeed = 0;
            }
        }

        //apply friction to y
        if (ySpeed > 0) {
            if ((ySpeed - (dt * FRICTION_REGULAR)) > 0) {
                ySpeed -= (dt * FRICTION_REGULAR);
            } else {
                ySpeed = 0;
            }
        } else if (ySpeed < 0) {
            if ((ySpeed + (dt * FRICTION_REGULAR)) < 0) {
                ySpeed += (dt * FRICTION_REGULAR);
            } else {
                ySpeed = 0;
            }
        }

        //move the player based on this speed
        x += xSpeed * dt * MaddBomber.TILE_SIZE;
        y += ySpeed * dt * MaddBomber.TILE_SIZE;

        if (xSpeed != 0 || ySpeed != 0) {
            sprite.setRotation((float) (180.0 * Math.atan2(ySpeed, xSpeed) / Math.PI) - 90);
        }

        hitbox.setX((float) (x - (width/2)));
        hitbox.setY((float) (y - (height/2)));

        if (getWallColliding()) {
            double newX = x;
            double newY = y;
            x = px;
            hitbox.setX((float) (x - (width/2)));
            if (getWallColliding()) {
                x = newX;
                y = py;
                hitbox.setX((float) (x - (width/2)));
                hitbox.setY((float) (y - (height/2)));
                if (getWallColliding()) {
                    x = px;
                    y = py;
                    hitbox.setX((float) (x - (width/2)));
                    hitbox.setY((float) (y - (height/2)));
                    xSpeed = 0;
                    ySpeed = 0;
                } else {
                    ySpeed = 0;
                }
            } else {
                xSpeed = 0;
            }
        }

        sprite.setCenterX((float) x);
        sprite.setCenterY((float) y);
    }

    public void run(float dt) {
        move(dt);
        checkBombCollision();
        for (Bomb bomb : bombs) {
            if (bomb.isUsed())
                bombsDeployed--;
        }

        bombs.removeIf(Bomb::isUsed);
        bombsIgnored.removeIf(Bomb::isUsed);
    }

    private boolean getWallColliding() {
        TiledMapTileLayer walls = (TiledMapTileLayer) map.getLayers().get("Walls");
        TiledMapTileLayer explodable = (TiledMapTileLayer) map.getLayers().get("Explodable");
        Rectangle rectangleMapObject = new Rectangle(0, 0, MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE);

        for (int tx = 0; tx < mapTileWidth; tx++) {
            for (int ty = 0; ty < mapTileHeight; ty++) {
                if (walls.getCell(tx, ty) != null || explodable.getCell(tx, ty) != null) {
                    rectangleMapObject.setX(tx * MaddBomber.TILE_SIZE);
                    rectangleMapObject.setY(ty * MaddBomber.TILE_SIZE);

                    if (Intersector.overlaps(hitbox, rectangleMapObject))
                        return true;
                }
            }
        }

        for (Bomb bomb : gameWorld.getBombs()) {
            if (!bombsIgnored.contains(bomb)) {
                if (Intersector.overlaps(hitbox, bomb.getHitbox()))
                    return true;
            }
        }

        return false;
    }

    private void checkBombCollision() {
        for (int i = bombsIgnored.size() - 1; i >= 0; i--) {
            if (!Intersector.overlaps(hitbox, bombsIgnored.get(i).getHitbox())) {
                bombsIgnored.remove(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void addBombToIgnore(Bomb bomb) {
        bombsIgnored.add(bomb);
    }

    public static double limit(double value, double lower, double higher) {
        if (value > higher) {
            value = higher;
        } else if (value < lower) {
            value = lower;
        }
        return value;
    }

    private void createBomb() {
        if (bombsDeployed < maxDeployedBombs) {
            int tileX = (int) (x / MaddBomber.TILE_SIZE);
            int tileY = (int) (y / MaddBomber.TILE_SIZE);

            //Check if there is already a bomb on this tile
            boolean bombExists = false;
            for (Bomb bomb : gameWorld.getBombs()) {
                if (bomb.getTileX() == tileX && bomb.getTileY() == tileY) {
                    bombExists = true;
                    break;
                }
            }
            //If not, place a bomb
            if (!bombExists) {
                Bomb newBomb = new Bomb(tileX, tileY, itemAtlas, explosionSize, false, this, gameWorld, game);
                bombs.add(newBomb);
                gameWorld.addBomb(newBomb);
                bombsDeployed++;
            }
        }
    }
}
