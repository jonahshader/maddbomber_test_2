package com.jonahshader.maddbomber.GameItems.Pickups;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.jonahshader.maddbomber.MaddBomber;


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

    public Pickup(int tileX, int tileY, PickupType type, TextureAtlas itemAtlas) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.type = type;

        hitbox = new Rectangle(tileX * MaddBomber.TILE_SIZE, tileY * MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE, MaddBomber.TILE_SIZE);

        switch(type) {
            case BOMB_COUNT_INCREASE:
                break;
            case EXPLOSION_SIZE_INCREASE:
                break;
            case SPEED_INCREASE:
                texture = itemAtlas.findRegion("shoes item tile");
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
}
