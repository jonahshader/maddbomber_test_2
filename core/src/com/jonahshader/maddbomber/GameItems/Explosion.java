package com.jonahshader.maddbomber.GameItems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.jonahshader.maddbomber.GameWorld;
import com.jonahshader.maddbomber.MaddBomber;
import com.jonahshader.maddbomber.Player;

public class Explosion {
    final static float EXPLOSION_TIME = 0.8f;
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

    public Explosion(int tileX, int tileY, int explosionsRemaining, Direction direction, TextureAtlas itemAtlas, Player owner, GameWorld gameWorld) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.direction = direction;
        this.owner = owner;
        this.gameWorld = gameWorld;
        explosionTime = EXPLOSION_TIME;

        hitbox = new Rectangle(tileX * MaddBomber.TILE_SIZE, tileY * MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE);
        texture = itemAtlas.findRegion("fire");

        TiledMapTileLayer explodables = (TiledMapTileLayer) gameWorld.getMap().getLayers().get("Explodable");
        TiledMapTileLayer walls = (TiledMapTileLayer) gameWorld.getMap().getLayers().get("Walls");

        hitExplodable = explodables.getCell(tileX, tileY) != null;

        //create additional explosions
        if (!hitExplodable) {
            if (explosionsRemaining > 0) {
                switch (direction) {
                    case LEFT:
                        if (walls.getCell(tileX - 1, tileY) == null) {
                            gameWorld.getExplosions().add(new Explosion(tileX - 1, tileY, explosionsRemaining - 1, direction, itemAtlas, owner, gameWorld));
                        }
                        break;
                    case RIGHT:
                        if (walls.getCell(tileX + 1, tileY) == null) {
                            gameWorld.getExplosions().add(new Explosion(tileX + 1, tileY, explosionsRemaining - 1, direction, itemAtlas, owner, gameWorld));
                        }
                        break;
                    case UP:
                        if (walls.getCell(tileX, tileY + 1) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX, tileY + 1, explosionsRemaining - 1, direction, itemAtlas, owner, gameWorld));
                        break;
                    case DOWN:
                        if (walls.getCell(tileX, tileY - 1) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX, tileY - 1, explosionsRemaining - 1, direction, itemAtlas, owner, gameWorld));
                        break;
                    case ALL:
                        if (walls.getCell(tileX - 1, tileY) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX - 1, tileY, explosionsRemaining - 1, Direction.LEFT, itemAtlas, owner, gameWorld));
                        if (walls.getCell(tileX + 1, tileY) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX + 1, tileY, explosionsRemaining - 1, Direction.RIGHT, itemAtlas, owner, gameWorld));
                        if (walls.getCell(tileX, tileY + 1) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX, tileY + 1, explosionsRemaining - 1, Direction.UP, itemAtlas, owner, gameWorld));
                        if (walls.getCell(tileX, tileY - 1) == null)
                            gameWorld.getExplosions().add(new Explosion(tileX, tileY - 1, explosionsRemaining - 1, Direction.DOWN, itemAtlas, owner, gameWorld));
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

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    private void spawnRandomItem() {
        //10% chance of spawning an item
        if (Math.random() < 0.1) {
            //spawn something

        }
    }
}
