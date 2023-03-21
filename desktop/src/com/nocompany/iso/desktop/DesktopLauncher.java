package com.nocompany.iso.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.nocompany.iso.Iso;
import com.nocompany.iso.utils.fitting.TilesFitting3;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 4;
		config.vSyncEnabled = true;
		new LwjglApplication(new Iso(), config);
		// new LwjglApplication(new TilesFitting3(), config);
	}
}
