package com.jonahshader.maddbomber.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jonahshader.maddbomber.MaddBomber;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "MaddBomber 2";
		config.width = 1920;
		config.height = 1080;
		config.foregroundFPS = 144;
		config.backgroundFPS = 60;
		new LwjglApplication(new MaddBomber(), config);
	}
}
