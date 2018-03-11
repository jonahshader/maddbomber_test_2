package com.jonahshader.maddbomber.GameItems;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.jonahshader.maddbomber.GameWorld;
import com.jonahshader.maddbomber.MaddBomber;
import com.jonahshader.maddbomber.Player;

public class Bomb {
    public final static float FUSE_TIME_MAX = 1.7f; //starting time for explosion timer

    private float timeRemaining;
    private int tileX, tileY;
    private TextureAtlas itemAtlas;
    private TextureRegion texture;
    private Rectangle hitbox;
    private boolean playerControl;
    private int explosionSize;
    private Player owner;
    private GameWorld gameWorld;
    private MaddBomber game;
    private boolean used = false;

    private long soundId;

    public Bomb(int tileX, int tileY, TextureAtlas itemAtlas, int explosionSize, boolean playerControl, Player owner, GameWorld gameWorld, MaddBomber game) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.playerControl = playerControl;
        this.explosionSize = explosionSize;
        this.owner = owner;
        this.gameWorld = gameWorld;
        this.itemAtlas = itemAtlas;
        this.game = game;

        timeRemaining = FUSE_TIME_MAX;

        hitbox = new Rectangle(tileX * MaddBomber.TILE_SIZE, tileY * MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE);
        texture = itemAtlas.findRegion("bomb");

        soundId = game.assets.manager.get(game.assets.fuse, Sound.class).play(0.1f, 1f, ((tileX / (float) (gameWorld.getMapProperties().get("width", Integer.class))) - 0.5f) * 1f);
        game.assets.manager.get(game.assets.fuse, Sound.class).setLooping(soundId, true);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, tileX * MaddBomber.TILE_SIZE, tileY * MaddBomber.TILE_SIZE);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void run(float deltaTime) {
        timeRemaining -= deltaTime;
        for (Explosion explosion : gameWorld.getExplosions()) {
            if (explosion.getTileX() == tileX && explosion.getTileY() == tileY) {
                explode(); //TODO: could create an infinite spawn loop bug
                break;
            }
        }

        if (timeRemaining <= 0) {
            explode();
        }
    }

    public boolean isUsed() {
        return used;
    }

    public void activate() {
        explode(); //go boom
    }

    private void explode() {
        //create fire cellular automata
        gameWorld.getExplosions().add(new Explosion(tileX, tileY, explosionSize, Explosion.Direction.ALL, game, owner, gameWorld));
        game.assets.manager.get(game.assets.fuse, Sound.class).stop(soundId);
        game.assets.manager.get(game.assets.explosion, Sound.class).play(0.17f, 1, ((tileX / (float) (gameWorld.getMapProperties().get("width", Integer.class))) - 0.5f));
        used = true;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public int getExplosionSize() {
        return explosionSize;
    }
}
