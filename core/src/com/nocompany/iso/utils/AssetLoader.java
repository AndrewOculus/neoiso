package com.nocompany.iso.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
// import com.badlogic.gdx.graphics.g3d.Shader;
// import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
// import com.nocompany.iso.objects.GameObject;
import com.badlogic.gdx.utils.TimeUtils;
import com.nocompany.iso.objects.GameObjectTypes;
import com.nocompany.iso.objects.GameObjectsPack;
import com.nocompany.iso.tiles.AnimationPack;
import com.nocompany.iso.tiles.CellType;
import com.nocompany.iso.tiles.CellsPack;

import java.util.HashMap;

public class AssetLoader implements Disposable {

	private static AssetLoader assetLoader;
	
	public static AssetLoader GetInstance(){
		
		if(assetLoader == null){
			assetLoader = new AssetLoader();
		}
		return assetLoader;
	}
	
	private Texture texture, texture1;
	private Texture map, view;

	private AssetManager manager;

	private HashMap<Integer, CellsPack> cellsPack;
	private HashMap<String, AnimationPack> animationPackHashMap;
	private HashMap<String, GameObjectsPack> gameObjectsPackHashMap;

	private HashMap<Integer, TextureAtlas> textureAtlases;

	private Texture errorTile;
	private TextureRegion errorTileRegion;
	public Texture upTile, downTile, leftTile, rightTile;

	private Texture inWaterTile;

	public Texture uiPanel;
	public Texture uiInventory;
	public Texture uiCloseBtnIdle;
	public Texture uiCloseBtnPress;

//	private ShaderProgram uiShader;

	private Animation<Texture> animObject;

	public Animation<Texture> GetAnimObject(){
		return animObject;
	}

	private AssetLoader() {

		Texture[] animTextures = new Texture[8];
		animTextures[0] = new Texture(Gdx.files.internal("waves/0.png"));
		animTextures[1] = new Texture(Gdx.files.internal("waves/1.png"));
		animTextures[2] = new Texture(Gdx.files.internal("waves/2.png"));
		animTextures[3] = new Texture(Gdx.files.internal("waves/3.png"));
		animTextures[4] = new Texture(Gdx.files.internal("waves/4.png"));
		animTextures[5] = new Texture(Gdx.files.internal("waves/5.png"));
		animTextures[6] = animTextures[5];
		animTextures[7] = animTextures[4];

		inWaterTile = new Texture(Gdx.files.internal("inwater.png"));

		animObject = new Animation<Texture>(0.4f, animTextures);
		animObject.setPlayMode(Animation.PlayMode.LOOP);

//		ShaderProgram.pedantic = false;
//		String uiV = Gdx.files.internal("shaders/ui/win_v.glsl").readString();
//		String uiF = Gdx.files.internal("shaders/ui/win_f.glsl").readString();
//		uiShader = new ShaderProgram(uiV, uiF);


		upTile = new Texture(Gdx.files.internal("up.png"));
		rightTile = new Texture(Gdx.files.internal("right.png"));
		leftTile = new Texture(Gdx.files.internal("left.png"));
		downTile = new Texture(Gdx.files.internal("down.png"));

		map = new Texture(Gdx.files.internal("minimap.png"), true);
		map.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.MipMap);

		view = new Texture(Gdx.files.internal("view.png"));
		uiPanel = new Texture(Gdx.files.internal("UI/panel.png"));
		uiInventory = new Texture(Gdx.files.internal("UI/inventory0.png"));

		uiCloseBtnIdle = new Texture(Gdx.files.internal("UI/close/btn0.png"));
		uiCloseBtnPress = new Texture(Gdx.files.internal("UI/close/btn1.png"));

		long timeMillis = TimeUtils.millis();


		cellsPack = new HashMap<>();
		cellsPack.put((int) CellType.SWAMP.getTileId(), new CellsPack("tiles/swamp"));
		cellsPack.put((int) CellType.CLAY.getTileId(), new CellsPack("tiles/clay")); //
		cellsPack.put((int) CellType.MOOR.getTileId(), new CellsPack("tiles/moor")); //
		cellsPack.put((int) CellType.FEN.getTileId(), new CellsPack("tiles/fen"));
		cellsPack.put((int) CellType.WATER.getTileId(), new CellsPack("tiles/water2")); //
		cellsPack.put((int) CellType.WATER_DEEP.getTileId(), new CellsPack("tiles/water")); //
		cellsPack.put((int) CellType.SAND.getTileId(), new CellsPack("tiles/sand")); //
		cellsPack.put((int) CellType.HEARTH.getTileId(), new CellsPack("tiles/heath2")); //
		cellsPack.put((int) CellType.LEAF.getTileId(), new CellsPack("tiles/leaf")); //

		long timeMillis1 = TimeUtils.millis();


		animationPackHashMap = new HashMap<>();
		animationPackHashMap.put("STAG", new AnimationPack("objects/STAG"));
//		animationPackHashMap.put("HUMAN", new AnimationPack("objects/HUMAN"));

//		animationPackHashMap.put("BEAR", new AnimationPack("objects/BEAR"));
//		animationPackHashMap.put("BOAR", new AnimationPack("objects/BOAR"));
//		animationPackHashMap.put("WOLF", new AnimationPack("objects/WOLF"));
//		animationPackHashMap.put("RABBIT", new AnimationPack("objects/RABBIT"));
//		animationPackHashMap.put("FOX", new AnimationPack("objects/FOX"));

		gameObjectsPackHashMap = new HashMap<>();

		gameObjectsPackHashMap.put(GameObjectTypes.ATREE.getName(), new GameObjectsPack("trees/atree"));
//		gameObjectsPackHashMap.put(GameObjectTypes.BIRCH.getName(), new GameObjectsPack("trees/birch"));
//		gameObjectsPackHashMap.put(GameObjectTypes.ELM.getName(), new GameObjectsPack("trees/elm"));
		gameObjectsPackHashMap.put(GameObjectTypes.FIR.getName(), new GameObjectsPack("trees/fir"));
//		gameObjectsPackHashMap.put(GameObjectTypes.OAK.getName(), new GameObjectsPack("trees/oak"));
		gameObjectsPackHashMap.put(GameObjectTypes.PINE.getName(), new GameObjectsPack("trees/pine"));
//		gameObjectsPackHashMap.put(GameObjectTypes.WILLOW.getName(), new GameObjectsPack("trees/willow"));
//		gameObjectsPackHashMap.put(GameObjectTypes.YEW.getName(), new GameObjectsPack("trees/yew"));

//		texture = new Texture(Gdx.files.internal("tiles/moor/base_9.png"));
//		texture1 = new Texture(Gdx.files.internal("tiles/moor/base_1.png"));
		errorTile = new Texture(Gdx.files.internal("error.png"));
		errorTileRegion = new TextureRegion(  errorTile);

		long timeMillis2 = TimeUtils.millis();

		textureAtlases = new HashMap<>();

		manager = new AssetManager();
		manager.load("tiles_atlases/clay.atlas", TextureAtlas.class);
		manager.load("tiles_atlases/fen.atlas", TextureAtlas.class);
		manager.load("tiles_atlases/heath.atlas", TextureAtlas.class);
		manager.load("tiles_atlases/leaf.atlas", TextureAtlas.class);
		manager.load("tiles_atlases/moor.atlas", TextureAtlas.class);
		manager.load("tiles_atlases/sand.atlas", TextureAtlas.class);
		manager.load("tiles_atlases/water_deep.atlas", TextureAtlas.class);
		manager.load("tiles_atlases/water.atlas", TextureAtlas.class);
		manager.load("tiles_atlases/grass.atlas", TextureAtlas.class);
		manager.load("tiles_atlases/wald.atlas", TextureAtlas.class);
		manager.finishLoading();


		textureAtlases.put((int) CellType.FEN.getTileId(), 			((TextureAtlas)manager.get("tiles_atlases/fen.atlas")));
		textureAtlases.put((int) CellType.GRASS.getTileId(), 		((TextureAtlas)manager.get("tiles_atlases/grass.atlas")));
		textureAtlases.put((int) CellType.CLAY.getTileId(), 		((TextureAtlas)manager.get("tiles_atlases/clay.atlas"))); //
		textureAtlases.put((int) CellType.MOOR.getTileId(), 		((TextureAtlas)manager.get("tiles_atlases/moor.atlas"))); //
		textureAtlases.put((int) CellType.WALD.getTileId(), 		((TextureAtlas)manager.get("tiles_atlases/wald.atlas")));
		textureAtlases.put((int) CellType.WATER.getTileId(), 		((TextureAtlas)manager.get("tiles_atlases/water.atlas"))); //
		textureAtlases.put((int) CellType.WATER_DEEP.getTileId(), 	((TextureAtlas)manager.get("tiles_atlases/water_deep.atlas"))); //
		textureAtlases.put((int) CellType.SAND.getTileId(), 		((TextureAtlas)manager.get("tiles_atlases/sand.atlas"))); //
		textureAtlases.put((int) CellType.HEARTH.getTileId(), 		((TextureAtlas)manager.get("tiles_atlases/heath.atlas"))); //
		textureAtlases.put((int) CellType.LEAF.getTileId(), 		((TextureAtlas)manager.get("tiles_atlases/leaf.atlas"))); //

		long timeMillis3 = TimeUtils.millis();


		System.out.println( (timeMillis1 - timeMillis) + " " + (timeMillis3 - timeMillis2));

	}

	public Texture GetInWater(){
		return inWaterTile;
	}

	public Texture GetTexture(){
		return texture;
	}
	public Texture GetMap(){
		return map;
	}
	public Texture GetView(){
		return view;
	}
	public TextureAtlas getCellsAtlas(int id){
		return textureAtlases.get(id);
	}
	public CellsPack getCells(int id){
		System.out.println( cellsPack.size() );
		return cellsPack.get(id);
	}
	public Texture getError(){
		return errorTile;
	}

	public AnimationPack getAnimationPack(String name){
		return animationPackHashMap.get(name);
	}

	public Texture getGameObjectsPack(GameObjectTypes gameObjectTypes){
		return gameObjectsPackHashMap.get(gameObjectTypes.getName()).getGameObject();
	}

	public Texture getGameObjectsPackShadows(GameObjectTypes gameObjectTypes){
		return gameObjectsPackHashMap.get(gameObjectTypes.getName()).getGameObjectShadow();
	}

	public TextureRegion getTextureByNumberAtlases( TextureAtlas cellsPack, short tile ){

		TextureRegion texture = cellsPack.findRegion("base", 1);

		switch (tile) {
			case 0:
				texture = cellsPack.findRegion("base", 1);
				break;
			case 1:
				texture = cellsPack.findRegion("base", 2);
				break;
			case 2:
				texture = cellsPack.findRegion("base", 3);
				break;

			case 3:
				texture = cellsPack.findRegion("b8");
				break;
			case 4:
				texture = cellsPack.findRegion("b8", 3);
				break;
			case 5:
				texture = cellsPack.findRegion("b8", 2);
				break;

			case 6:
				texture = cellsPack.findRegion("c2");
				break;
			case 7:
				texture = cellsPack.findRegion("c2", 2);
				break;
			case 8:
				texture = cellsPack.findRegion("c2", 3);
				break;

			case 9:
				texture = cellsPack.findRegion("c4");
				break;
			case 10:
				texture = cellsPack.findRegion("c4", 2);
				break;
			case 11:
				texture = cellsPack.findRegion("c4", 3);
				break;

			case 12:
				texture = cellsPack.findRegion("c12");
				break;
			case 13:
				texture = cellsPack.findRegion("c12", 2);
				break;
			case 14:
				texture = cellsPack.findRegion("c12", 3);
				break;

			case 15:
				texture = cellsPack.findRegion("b4");
				break;
			case 16:
				texture = cellsPack.findRegion("b4", 2);
				break;
			case 17:
				texture = cellsPack.findRegion("b4", 3);
				break;

			case 18:
				texture = cellsPack.findRegion("b9");
				break;
			case 19:
				texture = cellsPack.findRegion("b9", 2);
				break;
			case 20:
				texture = cellsPack.findRegion("b9", 3);
				break;

			case 21:
				texture = cellsPack.findRegion("c6");
				break;
			case 22:
				texture = cellsPack.findRegion("c6", 2);
				break;
			case 23:
				texture = cellsPack.findRegion("c6", 3);
				break;

			case 24:
				texture = cellsPack.findRegion("b8");
				break;
			case 25:
				texture = cellsPack.findRegion("b8", 2);
				break;
			case 26:
				texture = cellsPack.findRegion("b8", 3);
				break;


			case 27:
				texture = cellsPack.findRegion("c2");
				break;
			case 28:
				texture = cellsPack.findRegion("c2", 2);
				break;
			case 29:
				texture = cellsPack.findRegion("c2", 3);
				break;

			case 30:
				texture = cellsPack.findRegion("b1");
				break;
			case 31:
				texture = cellsPack.findRegion("b1", 2);
				break;
			case 32:
				texture = cellsPack.findRegion("b1", 3);
				break;

			case 33:
				texture = cellsPack.findRegion("b12");
				break;
			case 34:
				texture = cellsPack.findRegion("b12", 2);
				break;
			case 35:
				texture = cellsPack.findRegion("b12", 3);
				break;

			case 36:
				texture =  cellsPack.findRegion("b9");
				break;
			case 37:
				texture = cellsPack.findRegion("b9", 2);
				break;
			case 38:
				texture = cellsPack.findRegion("b9", 3);
				break;

			case 39:
				texture = cellsPack.findRegion("b2");
				break;
			case 40:
				texture = cellsPack.findRegion("b2", 2);
				break;
			case 41:
				texture = cellsPack.findRegion("b2", 3);
				break;

			case 42:
				texture = cellsPack.findRegion("b3");
				break;
			case 43:
				texture = cellsPack.findRegion("b3", 2);
				break;
			case 44:
				texture = cellsPack.findRegion("b3", 3);
				break;

			case 45:
				texture = cellsPack.findRegion("b2");
				break;
			case 46:
				texture = cellsPack.findRegion("b2", 2);
				break;
			case 47:
				texture = cellsPack.findRegion("b2", 3);
				break;

			case 48:
				texture = cellsPack.findRegion("b6");
				break;
			case 49:
				texture = cellsPack.findRegion("b6", 2);
				break;
			case 50:
				texture = cellsPack.findRegion("b6", 3);
				break;

			case 51:
				texture = cellsPack.findRegion("c8");
				break;
			case 52:
				texture = cellsPack.findRegion("c8", 2);
				break;
			case 53:
				texture = cellsPack.findRegion("c8", 3);
				break;

			case 54:
				texture =  cellsPack.findRegion("b2");
				break;
			case 55:
				texture = cellsPack.findRegion("b2", 2);
				break;
			case 56:
				texture = cellsPack.findRegion("b2", 3);
				break;


			case 57:
				texture = cellsPack.findRegion("b6");
				break;
			case 58:
				texture = cellsPack.findRegion("b6", 2);
				break;
			case 59:
				texture = cellsPack.findRegion("b6", 3);
				break;


			case 60:
				texture =  cellsPack.findRegion("b3");
				break;
			case 61:
				texture = cellsPack.findRegion("b3", 2);
				break;
			case 62:
				texture = cellsPack.findRegion("b3", 3);
				break;


			case 63:
				texture = cellsPack.findRegion("c4");
				break;
			case 64:
				texture = cellsPack.findRegion("c4", 2);
				break;
			case 65:
				texture = cellsPack.findRegion("c4", 3);
				break;

			case 66:
				texture = cellsPack.findRegion("c4");
				break;
			case 67:
				texture = cellsPack.findRegion("c4", 2);
				break;
			case 68:
				texture = cellsPack.findRegion("c4", 3);
				break;

			case 69:
				texture = cellsPack.findRegion("b4");
				break;
			case 70:
				texture = cellsPack.findRegion("b4", 2);
				break;
			case 71:
				texture = cellsPack.findRegion("b4", 3);
				break;

			case 72:
				texture = cellsPack.findRegion("b4");
				break;
			case 73:
				texture = cellsPack.findRegion("b4", 2);
				break;
			case 74:
				texture = cellsPack.findRegion("b4", 3);
				break;

			case 75:
				texture = cellsPack.findRegion("b1");
				break;
			case 76:
				texture = cellsPack.findRegion("b1", 2);
				break;
			case 77:
				texture = cellsPack.findRegion("b1", 3);
				break;

			case 78:
				texture = cellsPack.findRegion("c2");
				break;
			case 79:
				texture = cellsPack.findRegion("c2", 2);
				break;
			case 80:
				texture = cellsPack.findRegion("c2", 3);
				break;

			case 81:
				texture = cellsPack.findRegion("b12");
				break;
			case 82:
				texture = cellsPack.findRegion("b12", 2);
				break;
			case 83:
				texture = cellsPack.findRegion("b12", 3);
				break;

//			case 84:
//				texture = AssetLoader.GetInstance().upTile;
//				break;
//			case 85:
//				texture = AssetLoader.GetInstance().downTile;
//				break;
//			case 86:
//				texture = AssetLoader.GetInstance().leftTile;
//				break;
//			case 87:
//				texture = AssetLoader.GetInstance().rightTile;
//				break;

			case 88:
				texture = cellsPack.findRegion("c1");
				break;
			case 89:
				texture = cellsPack.findRegion("c1", 2);
				break;
			case 90:
				texture = cellsPack.findRegion("c1", 3);
				break;
				
			case 91:
				texture = errorTileRegion;//AssetLoader.GetInstance().getError();
				break;
		}

		return texture;
	}

		public Texture getTextureByNumber( CellsPack cellsPack, short tile ){

			Texture texture = cellsPack.getTexture("base_1");

			switch (tile) {
				case 0:
					texture = cellsPack.getTexture("base_1");
					break;
				case 1:
					texture = cellsPack.getTexture("base_2");
					break;
				case 2:
					texture = cellsPack.getTexture("base_3");
					break;

				case 3:
					texture = cellsPack.getTexture("b8");
					break;
				case 4:
					texture = cellsPack.getTexture("b8_3");
					break;
				case 5:
					texture = cellsPack.getTexture("b8_2");
					break;

				case 6:
					texture = cellsPack.getTexture("c2");
					break;
				case 7:
					texture = cellsPack.getTexture("c2_2");
					break;
				case 8:
					texture = cellsPack.getTexture("c2_3");
					break;

				case 9:
					texture = cellsPack.getTexture("c4");
					break;
				case 10:
					texture = cellsPack.getTexture("c4_2");
					break;
				case 11:
					texture = cellsPack.getTexture("c4_3");
					break;

				case 12:
					texture = cellsPack.getTexture("c12");
					break;
				case 13:
					texture = cellsPack.getTexture("c12_2");
					break;
				case 14:
					texture = cellsPack.getTexture("c12_3");
					break;

				case 15:
					texture = cellsPack.getTexture("b4");
					break;
				case 16:
					texture = cellsPack.getTexture("b4_2");
					break;
				case 17:
					texture = cellsPack.getTexture("b4_3");
					break;

				case 18:
					texture = cellsPack.getTexture("b9");
					break;
				case 19:
					texture = cellsPack.getTexture("b9_2");
					break;
				case 20:
					texture = cellsPack.getTexture("b9_3");
					break;

				case 21:
					texture = cellsPack.getTexture("c6");
					break;
				case 22:
					texture = cellsPack.getTexture("c6_2");
					break;
				case 23:
					texture = cellsPack.getTexture("c6_3");
					break;

				case 24:
					texture = cellsPack.getTexture("b8");
					break;
				case 25:
					texture = cellsPack.getTexture("b8_2");
					break;
				case 26:
					texture = cellsPack.getTexture("b8_3");
					break;


				case 27:
					texture = cellsPack.getTexture("c2");
					break;
				case 28:
					texture = cellsPack.getTexture("c2_2");
					break;
				case 29:
					texture = cellsPack.getTexture("c2_3");
					break;

				case 30:
					texture = cellsPack.getTexture("b1");
					break;
				case 31:
					texture = cellsPack.getTexture("b1_2");
					break;
				case 32:
					texture = cellsPack.getTexture("b1_3");
					break;

				case 33:
					texture = cellsPack.getTexture("b12");
					break;
				case 34:
					texture = cellsPack.getTexture("b12_2");
					break;
				case 35:
					texture = cellsPack.getTexture("b12_3");
					break;

				case 36:
					texture =  cellsPack.getTexture("b9");
					break;
				case 37:
					texture = cellsPack.getTexture("b9_2");
					break;
				case 38:
					texture = cellsPack.getTexture("b9_3");
					break;

				case 39:
					texture = cellsPack.getTexture("b2");
					break;
				case 40:
					texture = cellsPack.getTexture("b2_2");
					break;
				case 41:
					texture = cellsPack.getTexture("b2_3");
					break;

				case 42:
					texture = cellsPack.getTexture("b3");
					break;
				case 43:
					texture = cellsPack.getTexture("b3_2");
					break;
				case 44:
					texture = cellsPack.getTexture("b3_3");
					break;

				case 45:
					texture = cellsPack.getTexture("b2");
					break;
				case 46:
					texture = cellsPack.getTexture("b2_2");
					break;
				case 47:
					texture = cellsPack.getTexture("b2_3");
					break;

				case 48:
					texture = cellsPack.getTexture("b6");
					break;
				case 49:
					texture = cellsPack.getTexture("b6_2");
					break;
				case 50:
					texture = cellsPack.getTexture("b6_3");
					break;

				case 51:
					texture = cellsPack.getTexture("c8");
					break;
				case 52:
					texture = cellsPack.getTexture("c8_2");
					break;
				case 53:
					texture = cellsPack.getTexture("c8_3");
					break;

				case 54:
					texture =  cellsPack.getTexture("b2");
					break;
				case 55:
					texture = cellsPack.getTexture("b2_2");
					break;
				case 56:
					texture = cellsPack.getTexture("b2_3");
					break;


				case 57:
					texture = cellsPack.getTexture("b6");
					break;
				case 58:
					texture = cellsPack.getTexture("b6_2");
					break;
				case 59:
					texture = cellsPack.getTexture("b6_3");
					break;


				case 60:
					texture =  cellsPack.getTexture("b3");
					break;
				case 61:
					texture = cellsPack.getTexture("b3_2");
					break;
				case 62:
					texture = cellsPack.getTexture("b3_3");
					break;


				case 63:
					texture = cellsPack.getTexture("c4");
					break;
				case 64:
					texture = cellsPack.getTexture("c4_2");
					break;
				case 65:
					texture = cellsPack.getTexture("c4_3");
					break;

				case 66:
					texture = cellsPack.getTexture("c4");
					break;
				case 67:
					texture = cellsPack.getTexture("c4_2");
					break;
				case 68:
					texture = cellsPack.getTexture("c4_3");
					break;

				case 69:
					texture = cellsPack.getTexture("b4");
					break;
				case 70:
					texture = cellsPack.getTexture("b4_2");
					break;
				case 71:
					texture = cellsPack.getTexture("b4_3");
					break;

				case 72:
					texture = cellsPack.getTexture("b4");
					break;
				case 73:
					texture = cellsPack.getTexture("b4_2");
					break;
				case 74:
					texture = cellsPack.getTexture("b4_3");
					break;

				case 75:
					texture = cellsPack.getTexture("b1");
					break;
				case 76:
					texture = cellsPack.getTexture("b1_2");
					break;
				case 77:
					texture = cellsPack.getTexture("b1_3");
					break;

				case 78:
					texture = cellsPack.getTexture("c2");
					break;
				case 79:
					texture = cellsPack.getTexture("c2_2");
					break;
				case 80:
					texture = cellsPack.getTexture("c2_3");
					break;

				case 81:
					texture = cellsPack.getTexture("b12");
					break;
				case 82:
					texture = cellsPack.getTexture("b12_2");
					break;
				case 83:
					texture = cellsPack.getTexture("b12_3");
					break;
				case 84:
					texture = AssetLoader.GetInstance().upTile;
					break;
				case 85:
					texture = AssetLoader.GetInstance().downTile;
					break;
				case 86:
					texture = AssetLoader.GetInstance().leftTile;
					break;
				case 87:
					texture = AssetLoader.GetInstance().rightTile;
					break;

				case 88:
					texture = cellsPack.getTexture("c1");
					break;
				case 89:
					texture = cellsPack.getTexture("c1_2");
					break;
				case 90:
					texture = cellsPack.getTexture("c1_3");
					break;

				case 91:
					texture = AssetLoader.GetInstance().getError();
					break;
			}

		return texture;
	}

	@Override
	public void dispose() {
//		texture.dispose();
//		texture1.dispose();
		assetLoader = null;
	}
}
