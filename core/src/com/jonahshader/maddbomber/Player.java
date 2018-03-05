package com.jonahshader.maddbomber;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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

    final static double FRICTION_REGULAR = 15;
    //Center of player sprite
    //in world pixels
    protected double x, y, xSpeed, ySpeed;
    private double maxSpeedCurrent;
    final static double INITIAL_MAX_SPEED = 225;
    private static double width = 26;
    private static double height = 26;
    final static double MOVE_AUTO_CORRECT_THRESHOLD = (TILE_SIZE - width) / 2;
//    final static double MOVE_AUTO_CORRECT_THRESHOLD = 0;
    private int bombsDeployed = 0;
    private int maxDeployedBombs;
    private int explosionSize;
    private int score = 0;

    private Sprite sprite;
    private Color playerColor;
    private TextureAtlas itemAtlas;
    private Rectangle hitbox;
    private TiledMap map;
    private MapProperties prop;
    protected GameWorld gameWorld;
    protected MaddBomber game;
    private PlayerSpawner spawner;
    private int mapTileWidth;
    private int mapTileHeight;
    private boolean spawned = true;
    private int playerId;

    private ArrayList<Bomb> bombs;
    private ArrayList<Bomb> bombsIgnored; //bombs are put into here until the player moves off of them

    //controls
    private ControlProfile controlProfile;
    protected boolean upKeyDown, downKeyDown, leftKeyDown, rightKeyDown, placeKeyDown, activateKeyDown;

    public Player(int tileX, int tileY, ControlProfile controlProfile, GameWorld gameWorld, MaddBomber game, int playerId, Color playerColor) {
        this.playerId = playerId;
        this.controlProfile = controlProfile;
        this.map = gameWorld.getMap();
        this.itemAtlas = game.assets.manager.get(game.assets.itemAtlas, TextureAtlas.class);
        this.gameWorld = gameWorld;
        this.game = game;
        this.playerColor = playerColor;
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
        game.assets.manager.get(game.assets.death, Sound.class).play(0.27f, 1, (float) ((((x / TILE_SIZE) / (gameWorld.getMapProperties().get("width", Integer.class))) - 0.5f) * 1f));
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

        int directionalKeysPressed = 0;
        if (leftKeyDown) directionalKeysPressed++;
        if (rightKeyDown) directionalKeysPressed++;
        if (upKeyDown) directionalKeysPressed++;
        if (downKeyDown) directionalKeysPressed++;

        if (directionalKeysPressed == 1) {
            boolean[][] movementKernel = new boolean[3][3];
            boolean[][] collidables = GameWorld.getCollidables(map);

            int tempTileX = (int) (x / TILE_SIZE);
            int tempTileY = (int) (y / TILE_SIZE);

            for (int kernelX = -1; kernelX < 2; kernelX++) {
                for (int kernelY = -1; kernelY < 2; kernelY++) {
                    int sampleX = tempTileX + kernelX;
                    int sampleY = tempTileY + kernelY;
                    if (sampleX >= 0 && sampleX < mapTileWidth && sampleY >= 0 && sampleY < mapTileHeight) {
                        movementKernel[kernelX + 1][kernelY + 1] = collidables[sampleX][sampleY];
                    } else {
                        movementKernel[kernelX + 1][kernelY + 1] = true;
                    }
                }
            }

            double xBias = x - ((tempTileX * TILE_SIZE) + TILE_SIZE / 2);
            double yBias = y - ((tempTileY * TILE_SIZE) + TILE_SIZE / 2);

//            System.out.println("x bias: " + xBias);
//            System.out.println("y bias: " + yBias);


            if (leftKeyDown) {
                if (movementKernel[0][1]) { //if the spot directly to the left of the player is solid...
                    if (yBias > MOVE_AUTO_CORRECT_THRESHOLD) { //if the player is on the upper half of the tile...
                        if (!movementKernel[0][2]) { //if the block directly to the top left of the player is not solid...
                            tempYSpd = 1; //move up (should already be moving left)
//                            System.out.println("correction upwards");
                        }
                    } else if (yBias < -MOVE_AUTO_CORRECT_THRESHOLD) { //if the player is on the lower half of the tile...
                        if (!movementKernel[0][0]) { //if the block directly to the bottom left of the player is not solid...
                            tempYSpd = -1; //move down (should already be moving left)
//                            System.out.println("correction downwards");
                        }
                    }
                }
                if (!movementKernel[0][1]) { //left is clear
                    if (movementKernel[0][2]) { //top left is solid
                        if (yBias > MOVE_AUTO_CORRECT_THRESHOLD) { //leaning towards top
                            tempYSpd = -1; //move down
                        }
                    }
                    if (movementKernel[0][0]) { //bottom left is clear
                        if (yBias < -MOVE_AUTO_CORRECT_THRESHOLD) { //leaning torwards bottom
                            tempYSpd = 1; //move up
                        }
                    }
                }

            } else if (rightKeyDown) {
                if (movementKernel[2][1]) { //if the spot directly to the right of the player is solid...
                    if (yBias > MOVE_AUTO_CORRECT_THRESHOLD) { //if the player is on the upper half of the tile...
                        if (!movementKernel[2][2]) { //if the block directly to the top right of the player is not solid...
                            tempYSpd = 1; //move up (should already be moving left)
//                            System.out.println("correction upwards");
                        }
                    } else if (yBias < -MOVE_AUTO_CORRECT_THRESHOLD) { //if the player is on the lower half of the tile...
                        if (!movementKernel[2][0]) { //if the block directly to the bottom right of the player is not solid...
                            tempYSpd = -1; //move down (should already be moving left)
//                            System.out.println("correction downwards");
                        }
                    }
                }
                if (!movementKernel[2][1]) { //right is clear
                    if (movementKernel[2][2]) { //top right is solid
                        if (yBias > MOVE_AUTO_CORRECT_THRESHOLD) { //leaning towards top
                            tempYSpd = -1; //move down
                        }
                    }
                    if (movementKernel[2][0]) { //bottom right is clear
                        if (yBias < -MOVE_AUTO_CORRECT_THRESHOLD) { //leaning torwards bottom
                            tempYSpd = 1; //move up
                        }
                    }
                }
            } else if (upKeyDown) {
                if (movementKernel[1][2]) { //if the spot directly to the top of the player is solid...
                    if (xBias > MOVE_AUTO_CORRECT_THRESHOLD) { //if the player is on the right half of the tile...
                        if (!movementKernel[2][2]) { //if the block directly to the top right of the player is not solid...
                            tempXSpd = 1; //move right (should already be moving up)
//                            System.out.println("correction right");
                        }
                    } else if (xBias < -MOVE_AUTO_CORRECT_THRESHOLD) { //if the player is on the left half of the tile...
                        if (!movementKernel[0][2]) { //if the block directly to the top left of the player is not solid...
                            tempXSpd = -1; //move left (should already be moving up)
//                            System.out.println("correction left");
                        }
                    }
                }
                if (!movementKernel[1][2]) { //up is clear
                    if (movementKernel[2][2]) { //top right is solid
                        if (xBias > MOVE_AUTO_CORRECT_THRESHOLD) { //leaning towards right
                            tempXSpd = -1; //move left
                        }
                    }
                    if (movementKernel[0][2]) { //top left is clear
                        if (xBias < -MOVE_AUTO_CORRECT_THRESHOLD) { //leaning towards right
                            tempXSpd = 1; //move right
                        }
                    }
                }
            } else {
                if (movementKernel[1][0]) { //if the spot directly to the bottom of the player is solid...
                    if (xBias > MOVE_AUTO_CORRECT_THRESHOLD) { //if the player is on the right half of the tile...
                        if (!movementKernel[2][0]) { //if the block directly to the bottom right of the player is not solid...
                            tempXSpd = 1; //move right (should already be moving up)
//                            System.out.println("correction right");
                        }
                    } else if (xBias < -MOVE_AUTO_CORRECT_THRESHOLD) { //if the player is on the left half of the tile...
                        if (!movementKernel[0][0]) { //if the block directly to the bottom left of the player is not solid...
                            tempXSpd = -1; //move left (should already be moving up)
//                            System.out.println("correction left");
                        }
                    }
                }
                if (!movementKernel[1][0]) { //down is clear
                    if (movementKernel[2][0]) { //bottom right is solid
                        if (xBias > MOVE_AUTO_CORRECT_THRESHOLD) { //leaning towards right
                            tempXSpd = -1; //move left
                        }
                    }
                    if (movementKernel[0][0]) { //bottom left is clear
                        if (xBias < -MOVE_AUTO_CORRECT_THRESHOLD) { //leaning towards right
                            tempXSpd = 1; //move right
                        }
                    }
                }
            }
        }

        //add temp speed from keyboard input to the player's actual speed
        xSpeed += tempXSpd * FRICTION_REGULAR * dt * 2;
        ySpeed += tempYSpd * FRICTION_REGULAR * dt * 2;

        //clamp values to max speed
        xSpeed = limit(xSpeed, -(maxSpeedCurrent * dt), (maxSpeedCurrent * dt));
        ySpeed = limit(ySpeed, -(maxSpeedCurrent * dt), (maxSpeedCurrent * dt));
//        xSpeed = limit(xSpeed, -(maxSpeedCurrent), (maxSpeedCurrent));
//        ySpeed = limit(ySpeed, -(maxSpeedCurrent), (maxSpeedCurrent));

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

//        //apply friction to x
//        if (xSpeed > 0) {
//            if ((xSpeed - (FRICTION_REGULAR)) > 0) {
//                xSpeed -= (FRICTION_REGULAR);
//            } else {
//                xSpeed = 0;
//            }
//        } else if (xSpeed < 0) {
//            if ((xSpeed + (FRICTION_REGULAR)) < 0) {
//                xSpeed += (FRICTION_REGULAR);
//            } else {
//                xSpeed = 0;
//            }
//        }

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
//        x += (xSpeed * dt) * TILE_SIZE;
//        y += (ySpeed * dt) * TILE_SIZE;
        x += xSpeed * TILE_SIZE * (1.0/144);
        y += ySpeed * TILE_SIZE * (1.0/144);

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
            xSpeed = 0;
            ySpeed = 0;
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

    protected void resetStats() {
        maxSpeedCurrent = INITIAL_MAX_SPEED;
        maxDeployedBombs = 1;
        explosionSize = 1;
    }

    public void draw(SpriteBatch batch) {
        if (spawned) {
            sprite.setColor(playerColor);
            sprite.draw(batch);
        }
    }

    public void addBombToIgnore(Bomb bomb) {
        bombsIgnored.add(bomb);
    }

    protected void createBomb() {
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

    public boolean isSpawned() {
        return spawned;
    }

    public int getBombsDeployed() {
        return bombsDeployed;
    }
}
