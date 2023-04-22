package com.nocompany.iso;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.nocompany.iso.net.NetworkManager;
import com.nocompany.iso.net.NetworkMove;
import com.nocompany.iso.net.NetworkSubroutine;
import com.nocompany.iso.tiles.CellType;
import com.nocompany.iso.tiles.MapTileGroup;
import com.nocompany.iso.utils.GC;
import com.nocompany.iso.utils.AssetLoader;
import com.nocompany.iso.utils.IsometricHelper;
import com.nocompany.iso.ui.*;
import com.nocompany.iso.ui.button.*;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.ListIterator;

public class Iso extends ApplicationAdapter {

	SpriteBatch batch;
	OrthographicCamera camera;
	HashMap<String, MapTileGroup> tileGroupsHash;
	int[] dir = { -2, -1, 0, 1, 2 };
	TilesFactory factory;
	ObjectsRenderer objectsRenderer;
	NetworkMove networkMove;

	TouchController touchController;

	BitmapFont bitmapFont;
	OrthographicCamera hudCamera;

	OrthographicCamera mapCamera;
	Vector3 target;
	SpriteBatch ui;
	float mapX, mapY;
	float heroX, heroY;

	ShapeRenderer shapeRenderer;
	ShapeRenderer shapeRenderer2;

	UserInterface userInterface;

	@Override
	public void create () {
		target = new Vector3();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 0, 0);

		camera.position.set(83789.0f, 47314.0f, 0);//4011.9043f,5154.005f,0.0f);
		camera.zoom = 1.80005f;

		camera.update();

		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hudCamera.update();

		factory = new TilesFactory(null);//new MapGenerator());

		objectsRenderer = new ObjectsRenderer();
		animalTestMove = new AnimalTestMove("STAG");
		objectsRenderer.addTileObject(animalTestMove);

		tileGroupsHash = new HashMap<String, MapTileGroup>();
		GC.getInstance(tileGroupsHash);

		batch = new SpriteBatch();
		bitmapFont = new BitmapFont();
		ui = new SpriteBatch();

		mapCamera = new OrthographicCamera();
		mapCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		mapCamera.position.set(0, 0, 0);
		mapCamera.zoom = 0.1f;

		mapCamera.rotate(45f);
		mapCamera.update();

		NetworkManager.getInstance().Open();
		touchController = TouchController.getInstance();

		Preferences preferences = Gdx.app.getPreferences("savestore");
		animalTestMove.setX( 83789 );//35104.613f );//preferences.getFloat("HeroX", animalTestMove.getX()));
		animalTestMove.setY( 47314 );//5836.0884f);//preferences.getFloat("HeroY", animalTestMove.getY()));
		preferences.flush();

		System.out.println("===============================");
		System.out.println(""+preferences.getFloat("HeroX", animalTestMove.getX()) + " " +preferences.getFloat("HeroY", animalTestMove.getY()) );
		System.out.println("===============================");

		shapeRenderer = new ShapeRenderer();
		shapeRenderer2 = new ShapeRenderer();
		networkMove = new NetworkMove( objectsRenderer );

		OrthographicCamera camera1 = new OrthographicCamera();
		camera1.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
		userInterface = new UserInterface(camera1);

		userInterface.addWidget( 
			new Widget( 
				new UITexture( AssetLoader.GetInstance().uiPanel, SizeType.OnlyWidth, 1.0f, 1.0f, -0.5f, -0.5f ),
				camera1
			)
		);

		UIButton uiButton = new UIButton.UIButtonBuilder()
			.setButtonSize(0.025f, 0.025f)
			.setButtonOffset(0.9f, 0.86f)
			.setDescription("Close")
			.setCamera(camera1)
			.setIdle( AssetLoader.GetInstance().uiCloseBtnIdle )
			.setPress( AssetLoader.GetInstance().uiCloseBtnPress )
			.build();

		Widget widget = new Widget( new UITexture( AssetLoader.GetInstance().uiInventory, SizeType.OnlyWidth, 0.25f, 0.25f, -0.35f, -0.0f ), camera1);
		widget.addUIButton(uiButton);
		userInterface.addWidget(widget);

		Gson gson = new Gson();
		String json = gson.toJson(uiButton);

		System.out.println( json );

	}

	AnimalTestMove animalTestMove;

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		touchController.update();

		batch.setProjectionMatrix(camera.combined);
		camera.update();

		int x = (int) Math.floor( (animalTestMove.getX() - (Settings.GRID_TILES_WIDTH * Settings.TILE_HEIGHT)/2 )/(Settings.GRID_TILES_WIDTH * Settings.TILE_HEIGHT));
		int y = (int) Math.floor( (animalTestMove.getY() + (Settings.GRID_TILES_WIDTH * Settings.TILE_HEIGHT)/2 )/(Settings.GRID_TILES_WIDTH * Settings.TILE_HEIGHT));

		switch(Gdx.app.getType()) {
			case Android:

				camera.zoom += touchController.getZoom();
				camera.update();

				break;

			case Desktop:

				if(Gdx.input.isKeyPressed(Input.Keys.R)){
					camera.zoom += 0.1f;
					mapCamera.zoom += 0.005f;
					camera.update();
				}

				if(Gdx.input.isKeyPressed(Input.Keys.F)){
					camera.zoom -= 0.1f;
					mapCamera.zoom -= 0.005f;

					if(mapCamera.zoom < 0.1f){
						mapCamera.zoom = 0.05f;
					}

					if(camera.zoom < 0.1){
						camera.zoom = 0.1f;
					}

					camera.update();
				}
				break;
		}

		batch.begin();

		for( int i = 0 ; i < dir.length ; i++ ){
			for( int j = 0 ; j < dir.length ; j++ ){
				MapTileGroup tileGroup = tileGroupsHash.get(String.format("%d_%d", x + dir[i], y + dir[j]));

				if(tileGroup == null){
					tileGroup = factory.getNetworkTiles(x + dir[i],y + dir[j]);//factory.getGroup(x + dir[i],y + dir[j]);
					tileGroupsHash.put(String.format("%d_%d", x + dir[i], y + dir[j]), tileGroup);
				}

				tileGroup.update(objectsRenderer);
				tileGroup.render(batch);

			}
		}
		// MapTileGroup tileGroup = tileGroupsHash.get(String.format("%d_%d", x, y));

		// if(tileGroup!=null){
		// 	int cell = tileGroup.getCurrentCell(animalTestMove.getX(), animalTestMove.getY(), x, y);
		// 	byte firstTile = (byte) (cell  >> 28   & 0x0000000f);
		// 	try{
		// 		CellType cellType = CellType.getCellById(firstTile);

		// 		if(cellType == CellType.WATER || cellType == CellType.WATER_DEEP ){
		// 			animalTestMove.setInWater(true);
		// 		}else{
		// 			animalTestMove.setInWater(false);
		// 		}

		// 		System.out.println( cellType .name());
		// 	}catch(Exception e){

		// 	}
		// }

		NetworkSubroutine.update( animalTestMove.getX(), animalTestMove.getY() );
		// System.out.println( animalTestMove.getX() + " " + animalTestMove.getY() );
		target.set( animalTestMove.GetIsoX(), animalTestMove.GetIsoY(), 0);
		camera.position.set( camera.position.lerp(target, 0.1f) );

//		camera.position.set( animalTestMove.getX(), animalTestMove.getY() + 20, 0 );
		camera.update();

		objectsRenderer.update(Gdx.graphics.getDeltaTime());
		objectsRenderer.renderShadow(batch);
		objectsRenderer.render(batch);

		// draw fps
		batch.end();

		MapTileGroup tileGroup1 = tileGroupsHash.get(String.format("%d_%d", x, y));

		batch.setProjectionMatrix(hudCamera.combined);
		batch.begin();

		bitmapFont.draw(batch, "FPS=" + Gdx.graphics.getFramesPerSecond(), 0, hudCamera.viewportHeight - 20);

		batch.end();

		MapBaker.getInstance().asyncUpdate(x, y);
		GC.getInstance(null).refresh();

		// players

		ArrayList<Float> floatArrayList = NetworkManager.getInstance().getFloatArrayList();

//		if( floatArrayList != null ){
//			networkMove.update( floatArrayList );
//		}


		// map

/*
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getHeight()/3, Gdx.graphics.getHeight()/3);

		if(Gdx.input.isTouched()){
			mapX += Gdx.input.getDeltaX()*6;
			mapY -= Gdx.input.getDeltaY()*6;
		}

		heroX = (animalTestMove.getX() / Settings.TILE_HEIGHT) / 4.0f;
		heroY = (animalTestMove.getY() / Settings.TILE_HEIGHT) / 4.0f;

		mapCamera.position.set(heroX , heroY, 0);
		mapCamera.update();

		ui.setProjectionMatrix(mapCamera.combined);
		ui.begin();
		ui.draw( AssetLoader.GetInstance().GetMap(), 0 ,0, AssetLoader.GetInstance().GetMap().getWidth(), AssetLoader.GetInstance().GetMap().getHeight());
		ui.draw( AssetLoader.GetInstance().GetView(), heroX - 1.5f, heroY + 1.5f, 3, 3);
		ui.end();
*/
		// Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// mapCamera.position.set(0, 0, 0);
		// mapCamera.rotate(-45);
		// mapCamera.zoom = 1.0f;
		// mapCamera.update();
		

		// ui.setProjectionMatrix(mapCamera.combined);
		// ui.begin();
		// ui.draw( AssetLoader.GetInstance().uiPanel, -mapCamera.viewportWidth/2, -mapCamera.viewportWidth/2, mapCamera.viewportWidth, mapCamera.viewportWidth/3.0f );
		// ui.end();
		// mapCamera.rotate(45f);

		userInterface.draw();
		userInterface.update( Gdx.graphics.getDeltaTime());

	}

	@Override
	public void resume() {
//		NetworkManager.getInstance().Close();
//		NetworkManager.getInstance().Open();
	}


	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		float tmpZoom = camera.zoom;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);
		camera.zoom = tmpZoom;

		float tmpMapZoom = mapCamera.zoom;
		mapCamera = new OrthographicCamera();
		mapCamera.setToOrtho(false, width, width);
		mapCamera.zoom = tmpMapZoom;

		mapCamera.rotate(45f);
	}

	@Override
	public void pause() {

//		NetworkManager.getInstance().Close();

		Preferences preferences = Gdx.app.getPreferences("savestore");

		System.out.println("===============================");
		System.out.println(""+animalTestMove.getX() + " " + animalTestMove.getY() );
		System.out.println("===============================");

		preferences.putFloat("HeroX", animalTestMove.getX());
		preferences.putFloat("HeroY", animalTestMove.getY());
		preferences.flush();

	}

	@Override
	public void dispose () {
		batch.dispose();
		AssetLoader.GetInstance().dispose();

	}

}
