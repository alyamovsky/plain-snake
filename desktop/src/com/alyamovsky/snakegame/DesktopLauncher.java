package com.alyamovsky.snakegame;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setForegroundFPS(60);
		config.useVsync(true);
		config.setTitle("Shake");
		new Lwjgl3Application(new SnakeGame(), config);
	}
}