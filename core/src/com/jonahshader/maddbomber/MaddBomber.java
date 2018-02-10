package com.jonahshader.maddbomber;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jonahshader.maddbomber.Screens.PlayScreen;

/*
How to make a map in Tiled Editor:
	New Map Settings:
		Orthogonal, Base64 comp., Right Down
 */

public class MaddBomber extends Game {
	public static final int V_WIDTH = 640;
	public static final int V_HEIGHT = 360;
	public static final int TILE_SIZE = 32;
	public SpriteBatch batch;
	public ControlManager controls;
	public Assets assets;
	
	@Override
	public void create () {
		assets = new Assets();
		//Load all assets
		assets.load();

		//Display progress bar
		double progress = 0;
		while (!assets.manager.update()) {
			if (assets.manager.getProgress() != progress) {
				progress = assets.manager.getProgress();
				System.out.println(progress * 100 + "%");

			}
		}

		batch = new SpriteBatch();	//Main sprite batch
		controls = new ControlManager();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		assets.dispose();
	}
}
