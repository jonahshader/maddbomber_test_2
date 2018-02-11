package com.jonahshader.maddbomber.GameItems.Pickups;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.jonahshader.maddbomber.MaddBomber;
import com.jonahshader.maddbomber.Player;


public class Pickup {
    public enum PickupType {
        BOMB_COUNT_INCREASE,
        EXPLOSION_SIZE_INCREASE,
        SPEED_INCREASE
    }

    private int tileX, tileY;
    private PickupType type;
    private TextureRegion texture;
    private Rectangle hitbox;
    private boolean isUsed = false;
    private double lifespan;

    public Pickup(int tileX, int tileY, PickupType type, MaddBomber game) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.type = type;



        lifespan = 20; // 20 second lifespan

        hitbox = new Rectangle(tileX * MaddBomber.TILE_SIZE, tileY * MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE);

        switch(type) {
            case BOMB_COUNT_INCREASE:
                break;
            case EXPLOSION_SIZE_INCREASE:
                break;
            case SPEED_INCREASE:
                texture = game.assets.manager.get(game.assets.itemAtlas, TextureAtlas.class).findRegion("shoes item tile");
                break;
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, tileX * MaddBomber.TILE_SIZE, tileY * MaddBomber.TILE_SIZE);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public PickupType getType() {
        return type;
    }

    public void use(Player user) {
        switch (type) {
            case BOMB_COUNT_INCREASE:
                break;
            case EXPLOSION_SIZE_INCREASE:
                break;
            case SPEED_INCREASE:
                user.increaseSpeedByFactor(1.25);
                break;
        }
        isUsed = true;
    }

    public void run(float dt) {
        lifespan -= dt;
        if (lifespan <= 0) {
            isUsed = true;
        }
    }

    public boolean isUsed() {
        return isUsed;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }
}
