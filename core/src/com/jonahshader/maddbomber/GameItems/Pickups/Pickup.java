package com.jonahshader.maddbomber.GameItems.Pickups;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.jonahshader.maddbomber.MaddBomber;
import com.jonahshader.maddbomber.Player;

import static com.jonahshader.maddbomber.MaddBomber.TILE_SIZE;


public class Pickup {
    final static double SPEED_INCREASE_FACTOR = 0.3; //1.1
    public enum PickupType {
        BOMB_COUNT_INCREASE,
        EXPLOSION_SIZE_INCREASE,
        SPEED_INCREASE
    }

    private int tileX, tileY;
    private PickupType type;
    private Sprite sprite;
    private Rectangle hitbox;
    private boolean isUsed = false;
    private double lifespan;

    public Pickup(int tileX, int tileY, PickupType type, MaddBomber game) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.type = type;

        switch(type) {
            case BOMB_COUNT_INCREASE:
                sprite = new Sprite(game.assets.manager.get(game.assets.itemAtlas, TextureAtlas.class).findRegion("bomb item tile"));
                break;
            case EXPLOSION_SIZE_INCREASE:
                sprite = new Sprite(game.assets.manager.get(game.assets.itemAtlas, TextureAtlas.class).findRegion("explosion item tile"));
                break;
            case SPEED_INCREASE:
                sprite = new Sprite(game.assets.manager.get(game.assets.itemAtlas, TextureAtlas.class).findRegion("shoes item tile"));
                break;
        }
        setup();
    }

    public Pickup(int tileX, int tileY, MaddBomber game) {
        this.tileX = tileX;
        this.tileY = tileY;

        int typeTemp = (int) (Math.random() * 3.0);
        switch(typeTemp) {
            case 0:
                type = PickupType.BOMB_COUNT_INCREASE;
                break;
            case 1:
                if (Math.random() > 0.75) {
                    type = PickupType.BOMB_COUNT_INCREASE;
                } else {
                    if (Math.random() > 0.5) {
                        type = PickupType.EXPLOSION_SIZE_INCREASE;
                    } else {
                        type = PickupType.SPEED_INCREASE;
                    }
                }
                break;
            case 2:
                type = PickupType.SPEED_INCREASE;
                break;
            default:
                type = PickupType.SPEED_INCREASE;
                break;
        }

        switch(type) {
            case BOMB_COUNT_INCREASE:
                sprite = new Sprite(game.assets.manager.get(game.assets.itemAtlas, TextureAtlas.class).findRegion("bomb item tile"));
                break;
            case EXPLOSION_SIZE_INCREASE:
                sprite = new Sprite(game.assets.manager.get(game.assets.itemAtlas, TextureAtlas.class).findRegion("explosion item tile"));
                break;
            case SPEED_INCREASE:
                sprite = new Sprite(game.assets.manager.get(game.assets.itemAtlas, TextureAtlas.class).findRegion("shoes item tile"));
                break;
        }
        setup();
    }

    private void setup() {
        lifespan = 20; // 20 second lifespan
        sprite.setPosition(tileX * TILE_SIZE, tileY * TILE_SIZE);
        hitbox = new Rectangle(tileX * TILE_SIZE, tileY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }


    public void draw(SpriteBatch batch) {
        if (lifespan < 3) {
            sprite.setColor(1f, 1f, 1f, (float) (lifespan / 3.0f));
            sprite.draw(batch);
        }
        sprite.draw(batch);
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
                user.increaseMaxBombs(1);
                break;
            case EXPLOSION_SIZE_INCREASE:
                user.increaseExplosionRadius(1);
                break;
            case SPEED_INCREASE:
                user.increaseSpeedByFactor(SPEED_INCREASE_FACTOR);
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
