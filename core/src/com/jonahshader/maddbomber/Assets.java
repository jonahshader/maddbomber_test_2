package com.jonahshader.maddbomber;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

public class Assets {

    public final AssetManager manager = new AssetManager();

    //Spritesheets
    public final String itemAtlas = "Spritesheets/non_tile_spritesheet.pack";
    //Sounds
    public final String fuse = "Sounds/wick light.mp3";
    public final String explosion = "Sounds/explosion.mp3";
    public final String death = "Sounds/death.mp3";
    //Fonts
    public final String loadingFont = "Fonts/visitor/visitor1.ttf";
//    private final FreeTypeFontGenerator.FreeTypeFontParameter hudFontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();

    public Assets() {
//        hudFontParams.size = 50;
    }

    public void load() {
        manager.load(itemAtlas, TextureAtlas.class);
        manager.load(fuse, Sound.class);
        manager.load(explosion, Sound.class);
        manager.load(death, Sound.class);

        //loading TFFs

        //loadint hudFont
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter hudFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        hudFont.fontFileName = loadingFont;
        hudFont.fontParameters.size = 36;
        manager.load(loadingFont, BitmapFont.class, hudFont);
    }

    public void dispose() {
        manager.dispose();
    }
}
