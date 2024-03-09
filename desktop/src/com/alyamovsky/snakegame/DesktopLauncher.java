package com.alyamovsky.snakegame;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
	private static final int TARGET_FRAMERATE = 60;
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(640, 480);
		config.setForegroundFPS(TARGET_FRAMERATE);
		config.useVsync(true);
		config.setTitle("Shake");
		new Lwjgl3Application(new SnakeGame(TARGET_FRAMERATE), config);
	}
}
