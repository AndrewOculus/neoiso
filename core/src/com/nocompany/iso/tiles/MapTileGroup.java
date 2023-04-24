package com.nocompany.iso.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.nocompany.iso.MapBaker;
import com.nocompany.iso.ObjectsRenderer;
import com.nocompany.iso.Settings;
import com.nocompany.iso.net.NetworkManager;
import com.nocompany.iso.net.NetworkMessage;
import com.nocompany.iso.objects.SceneObject;
import com.nocompany.iso.utils.AssetLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MapTileGroup implements Disposable {

    private int x, y;
    private MapLayer tiles;
    private ArrayList<SceneObject> gameObjects;
    private MapBaker baker;
    private Texture atlas, water;
    private boolean isBaked = false;
    private boolean isEmpty = false;
    private boolean isLoaded = false;

    public final int countPerBake = 8;
    public int bakeCounter = 0;
    public int from;
    public int to;
    public boolean isComplite = false;

    private long lastUpdate;

    public int trueX, trueY;
    public boolean haveWater = false;

    public MapTileGroup (int x, int y, int trueX, int trueY, MapLayer tiles, List<SceneObject> gameObjects){

//        System.out.println("new MapTileGroup!");
        this.x = x;
        this.y = y;
        this.tiles = tiles;
        this.gameObjects = (ArrayList<SceneObject>) gameObjects;
        this.baker = MapBaker.getInstance();
        this.lastUpdate = TimeUtils.millis();

        if(this.gameObjects != null)
            for (SceneObject sc: this.gameObjects) {
                sc.setActive(true);
            }

        this.trueX = trueX;
        this.trueY = trueY;

        if(tiles == null){
           NetworkManager.getInstance().AddToQueue(new NetworkMessage(trueX, trueY));
        }

    }

    public MapTileGroup (){
        isEmpty = true;
    }

    public void update (ObjectsRenderer objectsRenderer){
        if( isLoaded ){
            objectsRenderer.addTileObjects(getObjects());
            isLoaded = false;
        }
    }

    public void render(SpriteBatch spriteBatch){
        if(isEmpty)
            return;

        if(gameObjects == null){
            List<SceneObject> obs = NetworkManager.getInstance().GetObjects(new NetworkMessage(trueX, trueY));

            if(obs != null){
                gameObjects = new ArrayList<>(obs);
                for (SceneObject sc: this.gameObjects) {
                    sc.setActive(true);
                }
            }
            isLoaded = true;
        }

        if(tiles == null){
            int[] res = NetworkManager.getInstance().GetResult(new NetworkMessage(trueX, trueY));
            if( res == null ){
                return;
            }

            List<SceneObject> obs = NetworkManager.getInstance().GetObjects(new NetworkMessage(trueX, trueY));

//            if( obs == null ){
//                return;
//            }
            if(obs != null){
                gameObjects = new ArrayList<>(obs);
            }


            tiles = new MapLayer(Settings.GRID_TILES_WIDTH, Settings.GRID_TILES_HEIGHT);

            int index = 0;
            for( int j = 0 ; j < Settings.GRID_TILES_HEIGHT ; j++ ){
                for( int i = 0 ; i < Settings.GRID_TILES_WIDTH ; i++ ){
                    tiles.setTile(i, j, res[index]);
                    index++;
                }
            }

        }

        if(atlas == null && !isBaked) {
            baker.bakeAsync(this);
            isBaked = true;
        }

        if(atlas != null)
            spriteBatch.draw(atlas, x, y, Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH, Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT );

//            spriteBatch.draw(atlas, x - Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH /2, y  , Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH, Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT );
		// AssetLoader.GetInstance().waterShader.begin();
		// AssetLoader.GetInstance().waterShader.setUniformi("use", 0);
		// AssetLoader.GetInstance().waterShader.end();


        // AssetLoader.GetInstance().waterShader.begin();
		// AssetLoader.GetInstance().waterShader.setUniformi("use", 1);
		// AssetLoader.GetInstance().waterShader.end();

        this.lastUpdate = TimeUtils.millis();
    }

    public void renderWater(SpriteBatch spriteBatch){
        if(water != null && haveWater)
            spriteBatch.draw(water, x, y, Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH, Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT );
    }

    public ArrayList<SceneObject> getObjects(){
        return gameObjects;
    }

    public void setWaterAtlas(Texture water){
        if(this.water != null)
            this.water.dispose();

        this.water = water;
    }

    public void setMainAtlas(Texture atlas){
        if(this.atlas != null)
            this.atlas.dispose();

        this.atlas = atlas;
    }

    public Texture getAtlas(){
        return atlas;
    }

    public MapLayer getTiles(){
        return tiles;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public long getLastUpdate(){
        return lastUpdate;
    }

    public void addObject(SceneObject gameObject){
        gameObject.setActive(true);
        if( gameObjects == null)
            gameObjects = new ArrayList<SceneObject>();
        gameObjects.add(gameObject);
    }

    public void dispose() {
        haveWater = false;

        if(this.gameObjects != null)
            for (SceneObject sc: this.gameObjects) {
                sc.setActive(false);
            }


        if(water != null) {
            water.dispose();
            water = null;

            System.out.println("Atlas object " + this.hashCode() + "water disposed");
        }
        if(atlas != null) {
            atlas.dispose();
            atlas = null;
            isBaked = false;

            System.out.println("Atlas object " + this.hashCode() + " disposed");
        }
    }

    public int getCurrentCell( float x2d, float y2d, int xxxx, int yyyy ){
        float inTileX = x2d - ( trueX * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT + Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT/2 );
        float inTileY = y2d - ( trueY * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT - Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT/2 );

//        System.out.println("inTileX inTileY " + x2d + " " + y2d + " " + trueX * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT + " " + trueY * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT);

        int xTile = (int)( inTileX / Settings.TILE_HEIGHT );
        int yTile = (int)( inTileY / Settings.TILE_HEIGHT );

        System.out.println( inTileX + " " + inTileY + " : " + xTile + " " + yTile);


//        if( tiles != null ) {
//            System.out.println("========");
//
//            for (int i = 0; i < 16; i++) {
//                for (int j = 0; j < 16; j++) {
//                    if( i == xTile && j == yTile ){
//                        System.out.print(-1 + "\t");
//                    }else{
//                        System.out.print((tiles.getTile(j, i) >> 28 & 0x0000000f) + "\t");
//                    }
//                }
//                System.out.println("");
//            }
//            System.out.println("========");
//        }


        if( tiles != null )
            return tiles.getTile( Settings.GRID_TILES_HEIGHT - xTile - 1, Settings.GRID_TILES_HEIGHT - yTile - 1  );
        else
            return -1;
    }

}
