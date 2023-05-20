package com.nocompany.iso;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import com.nocompany.iso.objects.AnimateObject;
import com.nocompany.iso.tiles.*;
import com.nocompany.iso.utils.AssetLoader;
import com.nocompany.iso.objects.*;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

public class MapBaker {

    private static MapBaker mapBaker;

    public static MapBaker getInstance() {
        if(mapBaker == null)
            mapBaker = new MapBaker();

        return mapBaker;
    }

    private FrameBuffer tilesFrameBuffer;
    private FrameBuffer waterTilesFrameBuffer;
    private FrameBuffer miniMapTilesFrameBuffer;
    private SpriteBatch spriteBatch;
    private OrthographicCamera bakerCamera;

    private Queue<MapTileGroup> tileGroupQueue;

    private long times = 0;
    private long nums = 0;

    private static int numberFrame = 0;

    public MapBaker(){
        spriteBatch = new SpriteBatch();
        tilesFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH ) , (int)(Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT ) , false);
        bakerCamera = new OrthographicCamera();
        tileGroupQueue = new ArrayDeque<>();
    }

    public void bakeAsync(MapTileGroup tileGroup){
        tileGroupQueue.add(tileGroup);
    }

    public void asyncUpdate( int x, int y){
//
//        if(numberFrame < Settings.FRAMES_PER_BAKE){
//            if(numberFrame == Settings.FRAMES_PER_BAKE/2){
//                if(tilesFrameBuffer == null)
//                tilesFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH ), (int)(Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT ), false);
//            }
//            numberFrame += 1;
//            return;
//        }else {
//            numberFrame = 0;
//        }
        if(tilesFrameBuffer == null)
            tilesFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH ), (int)(Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT ), false);

        if(waterTilesFrameBuffer == null)
            waterTilesFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH ), (int)(Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT ), false);

        if(!tileGroupQueue.isEmpty()){
            MapTileGroup mapTileGroup = tileGroupQueue.peek();

            if( mapTileGroup.getX() < x - 3 && mapTileGroup.getX() > x + 3 && mapTileGroup.getY() < y - 3 && mapTileGroup.getY() > y + 3  ){
                tileGroupQueue.remove();
                return;
            }

            bake( mapTileGroup );
            if(mapTileGroup.isComplite){
                tileGroupQueue.remove();
            }
        }
    }

    public Texture bake(MapTileGroup tileGroup){

        MapLayer tiles = tileGroup.getTiles();

        tileGroup.to = (tileGroup.bakeCounter + 1) * (tiles.getWidth() / tileGroup.countPerBake );
        tileGroup.from = tileGroup.bakeCounter * (tiles.getWidth() / tileGroup.countPerBake );

        if ( tileGroup.bakeCounter + 1 == tileGroup.countPerBake ){
            tileGroup.isComplite = true;
        }
//        System.out.println( ">>>>" + tileGroup.to + " " + tileGroup.from);
        tileGroup.bakeCounter ++;

        bakerCamera.position.set(Settings.TILE_WIDTH/2,Settings.TILE_HEIGHT * tiles.getHeight()/2,0);
        bakerCamera.viewportWidth = Settings.TILE_WIDTH * tiles.getWidth();
        bakerCamera.viewportHeight = Settings.TILE_HEIGHT * tiles.getHeight();
        bakerCamera.update();

//        long timeMillis = TimeUtils.millis();

        tilesFrameBuffer.begin();
        spriteBatch.setProjectionMatrix(bakerCamera.combined);
        spriteBatch.enableBlending();
        spriteBatch.begin();

        if(tileGroup.from == 0){
            Gdx.gl.glClearColor(1, 1, 1, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        ArrayList<StoreWater> waterArray = new ArrayList();

        for (int j = tileGroup.from; j < tileGroup.to; j++) {
            for (int i = 0; i < tiles.getHeight(); i++) {

                int currentTile = tiles.getTile(j, i);
                short firstTileType = (short) (currentTile >> 8 & 0x000000ff);
                short secondTileType = (short) (currentTile     & 0x000000ff);

                short firstTile = (short) (currentTile  >> 28   & 0x0000000f);
                short secondTile = (short) (currentTile >> 24   & 0x0000000f);
                short thirdTile = (short) (currentTile  >> 16   & 0x0000000f);

                int noRenderWaterFirst = 0;
                if( firstTile == CellType.WATER.getTileId() || firstTile == CellType.WATER_DEEP.getTileId()){
                    noRenderWaterFirst = 1;
                }

                int noRenderWaterSecond = 0;
                if( secondTile == CellType.WATER.getTileId() || secondTile == CellType.WATER_DEEP.getTileId()){
                    noRenderWaterSecond = 1;
                }  

                float x = i * Settings.TILE_HEIGHT  - j * Settings.TILE_HEIGHT ;
                float y = (i * Settings.TILE_HEIGHT  + j * Settings.TILE_HEIGHT ) / 2;

//                2 * i  * Settings.TILE_HEIGHT = x + y;
//                i = (x + y)/(2 * Settings.TILE_HEIGHT);
//                - j * 2 * Settings.TILE_HEIGHT = x - y;
//                j = (y - x)/( 2 * Settings.TILE_HEIGHT );

                if(firstTileType == 0 || firstTileType == 1 || firstTileType == 2 || firstTileType == 3){

                    TextureAtlas cellsPack = AssetLoader.GetInstance().getCellsAtlas(firstTile);

                    if( firstTile == CellType.WATER_DEEP.getTileId() && (ThreadLocalRandom.current().nextInt(0, 400) < 2) ){
                        tileGroup.addObject(new AnimateObject( tileGroup.getX() + x + Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH /2 , tileGroup.getY() - y + Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT ));
                    }

                    if( firstTile == CellType.FEN.getTileId() && (ThreadLocalRandom.current().nextInt(0, 40) < 4) ){
                        TextureRegion decRegion = AssetLoader.GetInstance().getDecal(DecalTypes.FEN, ThreadLocalRandom.current().nextInt(1, DecalTypes.FEN.getDecalCount()) );
                        if(decRegion != null)
                        tileGroup.addObject(new DecalObject( tileGroup.getX() + x + Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH /2 , tileGroup.getY() - y + Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT , decRegion));
                    }

                    if( firstTile == CellType.MOOR.getTileId() && (ThreadLocalRandom.current().nextInt(0, 40) < 4) ){
                        TextureRegion decRegion = AssetLoader.GetInstance().getDecal(DecalTypes.FEN, ThreadLocalRandom.current().nextInt(1, DecalTypes.FEN.getDecalCount()) );
                        if(decRegion != null)
                        tileGroup.addObject(new DecalObject( tileGroup.getX() + x + Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH /2 , tileGroup.getY() - y + Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT , decRegion));
                    }

                    if(cellsPack != null) {
                        TextureRegion texture = AssetLoader.GetInstance().getTextureByNumberAtlases(cellsPack, firstTileType);
                        spriteBatch.draw(texture, x, y);

                        if ( noRenderWaterFirst != 0 ){
                            StoreWater store = new StoreWater();
                            store.type = firstTileType;
                            store.mode = firstTile;
                            store.x = x;
                            store.y = y;
                            waterArray.add( store );
                        }

                    }
                    

                } else {

                    TextureAtlas cellsPack = AssetLoader.GetInstance().getCellsAtlas(thirdTile);
                    if(cellsPack != null){
                        TextureRegion texture = AssetLoader.GetInstance().getTextureByNumberAtlases(cellsPack, (short) 0);
                        if(texture!=null)
                            spriteBatch.draw(texture, x, y);
                        
                    }
                    else{
                        System.out.println("thirdTile "+thirdTile);
                    }
                        cellsPack = AssetLoader.GetInstance().getCellsAtlas(secondTile);
                        if(cellsPack != null) {
                            TextureRegion texture = AssetLoader.GetInstance().getTextureByNumberAtlases(cellsPack, secondTileType);
                            if(texture!=null)
                                spriteBatch.draw(texture, x, y);

                            if ( noRenderWaterSecond != 0){
                                StoreWater store = new StoreWater();
                                store.type = secondTileType;
                                store.mode = secondTile;
                                store.x = x;
                                store.y = y;
                                waterArray.add( store );
                            }
                        }
                        else{
                            System.out.println("secondTile "+secondTile);
                        }
                    
                        cellsPack = AssetLoader.GetInstance().getCellsAtlas(firstTile);
                        if(cellsPack != null) {
                            TextureRegion texture = AssetLoader.GetInstance().getTextureByNumberAtlases(cellsPack, firstTileType);
                            if(texture!=null)
                                spriteBatch.draw(texture, x, y);

                            if(noRenderWaterFirst != 0){
                                StoreWater store = new StoreWater();
                                store.type = firstTileType;
                                store.mode = firstTile;
                                store.x = x;
                                store.y = y;
                                waterArray.add( store );
                            }
                        }
                        else{
                            System.out.println("firstTile "+firstTile);
                        }
                    
                }
            }
        }
        spriteBatch.end();
        tilesFrameBuffer.end();


        tilesFrameBuffer.begin();
        spriteBatch.setProjectionMatrix(bakerCamera.combined);
        spriteBatch.enableBlending();
        spriteBatch.begin();

        for (int j = 2; j < tiles.getWidth() - 2; j++) {
            for (int i = 2; i < tiles.getHeight() - 2; i++) {
                if (ThreadLocalRandom.current().nextInt(0, 600) < 50) {
                    int currentTile = tiles.getTile(j, i);
                    short firstTile = (short) (currentTile  >> 28   & 0x0000000f);

                    DecalTypes dec = DecalTypes.getDecalById(firstTile);

                    if(dec.getDecalId() != DecalTypes.HEARTH.getDecalId()){
                        if (ThreadLocalRandom.current().nextInt(0, 600) < 50){
                            continue;
                        }
                    }

                    if(dec.getDecalId() != DecalTypes.MOOR.getDecalId())
                    if( dec.getDecalId() != DecalTypes.FEN.getDecalId())
                    if( dec.getDecalCount() != 0 ){
                        float x = i * Settings.TILE_HEIGHT  - j * Settings.TILE_HEIGHT ;
                        float y = (i * Settings.TILE_HEIGHT  + j * Settings.TILE_HEIGHT ) / 2;
                        TextureRegion decRegion = AssetLoader.GetInstance().getDecal(dec, ThreadLocalRandom.current().nextInt(1, dec.getDecalCount()) );

                        if(decRegion!=null){
                            spriteBatch.draw(decRegion, x, y, 0.5f, 0.5f, decRegion.getRegionWidth(), decRegion.getRegionHeight(), 1.0f, -1.0f, 0.0f );
                        }



//                         draw(TextureRegion region, float x, float y,
// float originX, float originY, float width, float height,
// float scaleX, float scaleY, float rotation)
                    }
                }
            }
        }


        spriteBatch.end();
        tilesFrameBuffer.end();

        if( !waterArray.isEmpty() ){
            waterTilesFrameBuffer.begin();
            spriteBatch.setProjectionMatrix(bakerCamera.combined);
            spriteBatch.enableBlending();
            spriteBatch.begin();

            if(tileGroup.haveWater == false){
                tileGroup.haveWater = true;

                Gdx.gl.glClearColor(0, 0, 0, 0);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            }

            for( int i = 0  ; i < waterArray.size() ; ++i ){
                StoreWater store = waterArray.get(i);

                short firstTile = store.mode;
                short firstTileType = store.type;

                TextureAtlas cellsPack = AssetLoader.GetInstance().getCellsAtlas(firstTile);
                if(cellsPack != null) {
                    TextureRegion texture = AssetLoader.GetInstance().getTextureByNumberAtlases(cellsPack, firstTileType);
                    if(texture!=null)
                        spriteBatch.draw(texture, store.x, store.y);
                }
            }

            spriteBatch.end();
            waterTilesFrameBuffer.end();
        }

        if( !tileGroup.isComplite ){
            return null;
        }

        miniMapTilesFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(Settings.GRID_TILES_WIDTH), (int)(Settings.GRID_TILES_HEIGHT), false);

//        times += TimeUtils.millis() - timeMillis;
//        nums += 1;

//        System.out.println(String.format("bake tim ms: %d std: %f", TimeUtils.millis() - timeMillis, times*1f/nums));

//        if(nums > 50){
//            nums = 0;
//            times = 0;
//        }

        Texture texture = waterTilesFrameBuffer.getColorBufferTexture();

        waterTilesFrameBuffer.getTextureAttachments().removeIndex(0);
        waterTilesFrameBuffer.dispose();
        waterTilesFrameBuffer = null;

        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        if(tileGroup.haveWater)
            tileGroup.setWaterAtlas(texture);
        else{
            texture.dispose();
        }

        Texture mainTexture = tilesFrameBuffer.getColorBufferTexture();

        tilesFrameBuffer.getTextureAttachments().removeIndex(0);
        tilesFrameBuffer.dispose();
        tilesFrameBuffer = null;

        mainTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        tileGroup.setMainAtlas(mainTexture);

        return texture;
    }
}

class StoreWater{
    float x;
    float y;
    short type;
    short mode;
}