package com.nocompany.iso;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.nocompany.iso.Iso;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		config.useGLSurfaceView20API18 = true;
//		config.useGL30 = true;
		config.numSamples = 2;
		initialize(new Iso(), config);
	}
}
