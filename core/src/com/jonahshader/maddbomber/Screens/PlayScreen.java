package com.jonahshader.maddbomber.Screens;

import com.badlogic.gdx.Screen;
import com.jonahshader.maddbomber.MaddBomber;
import com.jonahshader.maddbomber.MatchSystems.Match;

public class PlayScreen implements Screen {
    private MaddBomber game;
    private Match match;

    public PlayScreen(MaddBomber game) {
        this.game = game;
        match = new Match(game, 1, "Maps/Sandstone Larger.tmx");
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        match.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        match.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        match.dispose();
    }

}
