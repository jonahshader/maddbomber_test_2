package com.jonahshader.maddbomber.GameItems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.jonahshader.maddbomber.GameItems.Pickups.Pickup;
import com.jonahshader.maddbomber.GameWorld;
import com.jonahshader.maddbomber.MaddBomber;
import com.jonahshader.maddbomber.Player;

public class Explosion {
    public final static float EXPLOSION_TIME = 0.6f;

    public Player getOwner() {
        return owner;
    }

    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        ALL
    }

    private int tileX, tileY;
    private float explosionTime;
    private Direction direction;
    private TextureRegion texture;
    private Rectangle hitbox;
    private Player owner;
    private GameWorld gameWorld;
    private boolean hitExplodable;
    private boolean used = false;
    private TextureAtlas itemAtlas;
    private MaddBomber game;

    public Explosion(int tileX, int tileY, int explosionsRemaining, Direction direction, MaddBomber game, Player owner, GameWorld gameWorld) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.direction = direction;
        this.owner = owner;
        this.gameWorld = gameWorld;
        this.game = game;
        explosionTime = EXPLOSION_TIME;

        hitbox = new Rectangle(tileX * MaddBomber.TILE_SIZE, tileY * MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE);
        texture = game.assets.manager.get(game.assets.itemAtlas, TextureAtlas.class).findRegion("fire");

        TiledMapTileLayer explodables = (TiledMapTileLayer) gameWorld.getMap().getLayers().get("Explodable");
        TiledMapTileLayer walls = (TiledMapTileLayer) gameWorld.getMap().getLayers().get("Walls");

        hitExplodable = explodables.getCell(tileX, tileY) != null;
        //create additional explosions
        if (!hitExplodable) {
            if (explosionsRemaining > 0) {
                switch (direction) {
                    case LEFT:
                        if (walls.getCell(tileX - 1, tileY) == null) {
                            gameWorld.getExplosions().add(new Explosion(tileX - 1, tileY, explosionsRemaining - 1, direction, game, owner, gameWorld));
                        }
                        break;
                    case RIGHT:
                        if (walls.getCell(tileX + 1, tileY) == null) {
                            gameWorld.getExplosions().add(new Explosion(tileX + 1, tileY, explosionsRemaining - 1, direction, game, owner, gameWorld));
                        }
                        break;
                    case UP:
                        if (walls.getCell(tileX, tileY + 1) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX, tileY + 1, explosionsRemaining - 1, direction, game, owner, gameWorld));
                        break;
                    case DOWN:
                        if (walls.getCell(tileX, tileY - 1) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX, tileY - 1, explosionsRemaining - 1, direction, game, owner, gameWorld));
                        break;
                    case ALL:
                        if (walls.getCell(tileX - 1, tileY) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX - 1, tileY, explosionsRemaining - 1, Direction.LEFT, game, owner, gameWorld));
                        if (walls.getCell(tileX + 1, tileY) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX + 1, tileY, explosionsRemaining - 1, Direction.RIGHT, game, owner, gameWorld));
                        if (walls.getCell(tileX, tileY + 1) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX, tileY + 1, explosionsRemaining - 1, Direction.UP, game, owner, gameWorld));
                        if (walls.getCell(tileX, tileY - 1) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX, tileY - 1, explosionsRemaining - 1, Direction.DOWN, game, owner, gameWorld));
                        break;
                }
            }
        }
    }

    public void run(float deltaTime) {
        if (!used) {
            explosionTime -= deltaTime;
            if (explosionTime <= 0) {
                if (hitExplodable) {
                    TiledMapTileLayer explodable = (TiledMapTileLayer) gameWorld.getMap().getLayers().get("Explodable");
                    explodable.setCell(tileX, tileY, null);
                    spawnRandomItem();
                }
                used = true;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, tileX * MaddBomber.TILE_SIZE, tileY * MaddBomber.TILE_SIZE);
    }

    public boolean isUsed() {
        return used;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    private void spawnRandomItem() {
        //20% chance of spawning an item
        if (Math.random() < 0.2) {
            //spawn something
            gameWorld.getPickups().add(new Pickup(tileX, tileY, game));
            System.out.println("Pickup spawned!");
        }
    }
}
