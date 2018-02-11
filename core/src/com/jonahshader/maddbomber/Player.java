package com.jonahshader.maddbomber;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
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
import com.jonahshader.maddbomber.MatchSystems.PlayerSpawner;

import java.util.ArrayList;

import static com.jonahshader.maddbomber.MaddBomber.TILE_SIZE;

public class Player implements InputProcessor {
    final static double FRICTION_REGULAR = 20;
    //Center of player sprite
    //in world pixels (probably 32 pixels per tile)
    private double x, y, xSpeed, ySpeed;
    private double maxSpeedCurrent;
    private double width = 26;
    private double height = 26;
    private int bombsDeployed = 0;
    private int maxDeployedBombs;
    private int explosionSize;
    private int score = 0;

    private Sprite sprite;
    private TextureAtlas itemAtlas;
    private Rectangle hitbox;
    private TiledMap map;
    private MapProperties prop;
    private GameWorld gameWorld;
    private MaddBomber game;
    private PlayerSpawner spawner;
    private int mapTileWidth;
    private int mapTileHeight;
    private boolean spawned = true;

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
        TextureRegion tr = new TextureRegion(itemAtlas.findRegion("player"));
        spawner = new PlayerSpawner(gameWorld, this);
        prop = map.getProperties();
        resetStats();
        bombs = new ArrayList<>();
        bombsIgnored = new ArrayList<>();

        mapTileWidth = prop.get("width", Integer.class);
        mapTileHeight = prop.get("height", Integer.class);

        x = (tileX * TILE_SIZE) + (TILE_SIZE / 2);
        y = (tileY * TILE_SIZE) + (TILE_SIZE / 2);

        hitbox = new Rectangle(tileX * TILE_SIZE, tileY * TILE_SIZE, (float) width, (float) height);
        sprite = new Sprite(tr);
        sprite.setCenterX((float) x);
        sprite.setCenterY((float) y);
    }

    public static double limit(double value, double lower, double higher) {
        if (value > higher) {
            value = higher;
        } else if (value < lower) {
            value = lower;
        }
        return value;
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

    public void kill(Player killer, String cause) {
        spawner.requestRespawn();
        if (spawned)
            if (killer == this) {
                killer.givePoints(-1); // you killed your self... -1 point
            } else {
                killer.givePoints(1);
            }
        resetStats();
        spawned = false;
        System.out.println(cause);
        game.assets.manager.get(game.assets.death, Sound.class).play(0.27f);
    }

    public void respawn(int spawnTileX, int spawnTileY) {
        x = (spawnTileX * TILE_SIZE) + TILE_SIZE / 2;
        y = (spawnTileY * TILE_SIZE) + TILE_SIZE / 2;
        spawned = true;
        run(0);
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
        x += xSpeed * dt * TILE_SIZE;
        y += ySpeed * dt * TILE_SIZE;

        if (xSpeed != 0 || ySpeed != 0) {
            sprite.setRotation((float) (180.0 * Math.atan2(ySpeed, xSpeed) / Math.PI) - 90);
        }

        hitbox.setX((float) (x - (width / 2)));
        hitbox.setY((float) (y - (height / 2)));

        if (getWallColliding()) {
            double newX = x;
            double newY = y;
            x = px;
            hitbox.setX((float) (x - (width / 2)));
            if (getWallColliding()) {
                x = newX;
                y = py;
                hitbox.setX((float) (x - (width / 2)));
                hitbox.setY((float) (y - (height / 2)));
                if (getWallColliding()) {
                    x = px;
                    y = py;
                    hitbox.setX((float) (x - (width / 2)));
                    hitbox.setY((float) (y - (height / 2)));
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
        if (spawned) {
            move(dt);
            checkBombCollision();
            checkExplosionCollision();
            checkPickupCollision();
        } else { //if not spawned...
            spawner.run(dt);
        }

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
        Rectangle rectangleMapObject = new Rectangle(0, 0, TILE_SIZE, TILE_SIZE);

        for (int tx = 0; tx < mapTileWidth; tx++) {
            for (int ty = 0; ty < mapTileHeight; ty++) {
                if (walls.getCell(tx, ty) != null || explodable.getCell(tx, ty) != null) {
                    rectangleMapObject.setX(tx * TILE_SIZE);
                    rectangleMapObject.setY(ty * TILE_SIZE);

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

    private void checkExplosionCollision() {
        for (int i = 0; i < gameWorld.getExplosions().size(); i++) {
            if (hitbox.overlaps(gameWorld.getExplosions().get(i).getHitbox())) {
                kill(gameWorld.getExplosions().get(i).getOwner(), "Killed from explosion");
            }
        }
    }

    private void checkPickupCollision() {
        for (int i = 0; i < gameWorld.getPickups().size(); i++) {
            if (hitbox.overlaps(gameWorld.getPickups().get(i).getHitbox())) {
                gameWorld.getPickups().get(i).use(this);
            }
        }
    }

    private void resetStats() {
        maxSpeedCurrent = 2;
        maxDeployedBombs = 2;
        explosionSize = 2;
    }

    public void draw(SpriteBatch batch) {
        if (spawned)
            sprite.draw(batch);
    }

    public void addBombToIgnore(Bomb bomb) {
        bombsIgnored.add(bomb);
    }

    private void createBomb() {
        if (bombsDeployed < maxDeployedBombs && spawned) {
            int tileX = (int) (x / TILE_SIZE);
            int tileY = (int) (y / TILE_SIZE);

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

    public void increaseSpeedByFactor(double v) {
        maxSpeedCurrent *= v;
    }

    public void givePoints(int pointsToAdd) {
        score += pointsToAdd;
    }

    public int getScore() {
        return score;
    }
}
