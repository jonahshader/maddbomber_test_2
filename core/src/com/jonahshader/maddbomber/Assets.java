package com.jonahshader.maddbomber;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {

    public final AssetManager manager = new AssetManager();

    //Spritesheets
    public final String itemAtlas = "Spritesheets/non_tile_spritesheet.pack";
    //Sounds
    public final String fuse = "Sounds/wick light.mp3";
    public final String explosion = "Sounds/explosion.mp3";
    public final String death = "Sounds/death.mp3";
    //Fonts
//    public final String loadingFont = "Fonts/visitor/visitor1.ttf";

    public Assets() {
    }

    public void load() {
        manager.load(itemAtlas, TextureAtlas.class);
        manager.load(fuse, Sound.class);
        manager.load(explosion, Sound.class);
        manager.load(death, Sound.class);
//        manager.load(loadingFont, BitmapFont.class);
    }

    public void dispose() {
        manager.dispose();
    }
}
