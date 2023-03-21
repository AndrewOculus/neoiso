package com.nocompany.iso;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.TimeUtils;
import com.nocompany.iso.objects.GameObject;
import com.nocompany.iso.objects.GameObjectTypes;
import com.nocompany.iso.objects.SceneObject;
import com.nocompany.iso.tiles.*;
import com.nocompany.iso.utils.MapGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;

public class TilesFactory {

    private MapGenerator mapGenerator;
    private HashMap<String, byte[]> tilesMap = new HashMap<>();

    private List<Layer> tilesMapFromFile = new ArrayList<>();
    private HashMap<String, List<SceneObject>> gameObjects = new HashMap<>();

    private MapLayer newTileMapFromFile;


    public TilesFactory(MapGenerator mapGenerator){this.mapGenerator = mapGenerator;}

    public MapTileGroup getNetworkTiles(int x, int y) {

        int xx = x * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT - y * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT;
        int yy = (x * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT + y * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT) / 2;

//        System.out.println(String.format("=> %d_%d", xx, yy));
//        System.out.println(String.format("===> %d_%d", x, y));

        return new MapTileGroup(xx, yy, x, y, null, null );
    }

    public MapTileGroup getCompressedGroupFromFile(int x, int y){

        if(newTileMapFromFile == null){
            long startTime = TimeUtils.millis();

            // tiles
            String fileName = "map/compressed";
            byte[] bytes = Gdx.files.internal(fileName).readBytes();
            tilesMap.put(fileName, bytes);

            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            int width = buffer.getInt();
            int height = buffer.getInt();

            newTileMapFromFile = new MapLayer(width, height);

            for (int j = 0; j < Settings.GRID_TILES_HEIGHT * Settings.GRIDS_PER_FILE; j++) {
                for (int i = 0; i < Settings.GRID_TILES_WIDTH * Settings.GRIDS_PER_FILE; i++) {
                    newTileMapFromFile.setTile(i, j, buffer.getInt());
                    if( i == 2 && j == 2){
                        System.out.println(newTileMapFromFile.getTile(i, j) );
                    }
                }
            }

            // Game objects
            fileName = String.format("map/objs");
            bytes = Gdx.files.internal(fileName).readBytes();
            buffer.clear();
            buffer = ByteBuffer.wrap(bytes);

            int count = buffer.getInt();

            for(int i = 0 ; i < count ; i++) {

                int type = buffer.getInt();
                float xxx = buffer.getFloat();
                float yyy = buffer.getFloat();

                int xx = (int) (xxx / Settings.TILE_HEIGHT);
                int yy = (int) (yyy / Settings.TILE_HEIGHT);

                String key = String.format("%d_%d", (int)(xx / (Settings.GRID_TILES_WIDTH)), (int)(yy / (Settings.GRID_TILES_HEIGHT)) );

                if(key.equals("10_0")){
                    System.out.println(key);
                    System.out.println(xxx + " " + yyy);
                }

                if (gameObjects.get(key) == null) {
                    gameObjects.put(key, new ArrayList<SceneObject>());
                }

                gameObjects.get(key).add(new GameObject(xxx, yyy, GameObjectTypes.values()[type]));

            }
            long stopTime = TimeUtils.millis();
        }

        // тут обычная рутина для генерирования набора тайлов

        MapLayer layers = new MapLayer(Settings.GRID_TILES_WIDTH, Settings.GRID_TILES_HEIGHT);

        for (int i = 0; i < Settings.GRID_TILES_WIDTH; i++) {
            for (int j = 0; j < Settings.GRID_TILES_HEIGHT; j++) {

                int mapTile = newTileMapFromFile.getTile(
                        Settings.GRID_TILES_WIDTH - i - 1 + x * Settings.GRID_TILES_WIDTH,
                        Settings.GRID_TILES_HEIGHT - j - 1 + y * Settings.GRID_TILES_HEIGHT);

                layers.setTile(i, j, mapTile);
            }
        }


        int xx = x * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT - y * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT;
        int yy = (x * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT + y * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT) / 2;

        String key = String.format("%d_%d", x, y);

        return new MapTileGroup(xx, yy, 0 , 0, layers, gameObjects.get(key));

    }

    public TileGroup getGroupFromFile(int x, int y){

        if(tilesMapFromFile.isEmpty()){
            long startTime = TimeUtils.millis();
            String fileName = String.format("map/%d_%d", 0, 0);

            byte[] bytes = Gdx.files.internal(fileName).readBytes();
            tilesMap.put(fileName, bytes);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            int w = buffer.getInt();
            int h = buffer.getInt();
            int layers = buffer.getInt();

            for (int i = 0 ; i < layers ; i ++ ){
                bytes[i] = buffer.get();
            }

            for(int n = 0 ; n < layers ; n++) {
                Layer layer = new Layer(Settings.GRID_TILES_WIDTH * Settings.GRIDS_PER_FILE, Settings.GRID_TILES_HEIGHT * Settings.GRIDS_PER_FILE, CellType.values()[bytes[n]]);
                for (int j = 0; j < Settings.GRID_TILES_HEIGHT * Settings.GRIDS_PER_FILE; j++) {
                    for (int i = 0; i < Settings.GRID_TILES_WIDTH * Settings.GRIDS_PER_FILE; i++) {
                        layer.setTile(i, j, buffer.getShort());
                    }
                }
                tilesMapFromFile.add(layer);
            }

            // GameObjects

            fileName = String.format("map/objs");
            bytes = Gdx.files.internal(fileName).readBytes();
            buffer.clear();
            buffer = ByteBuffer.wrap(bytes);

            int count = buffer.getInt();

            for(int i = 0 ; i < count ; i++) {

                int type = buffer.getInt();
                float xxx = buffer.getFloat();
                float yyy = buffer.getFloat();

                int xx = (int) (xxx / Settings.TILE_HEIGHT);
                int yy = (int) (yyy / Settings.TILE_HEIGHT);

                String key = String.format("%d_%d", (int)(xx / (Settings.GRID_TILES_WIDTH)), (int)(yy / (Settings.GRID_TILES_HEIGHT)) );

                if (gameObjects.get(key) == null) {
                    gameObjects.put(key, new ArrayList<SceneObject>());
                }

                gameObjects.get(key).add(new GameObject(xxx, yyy, GameObjectTypes.values()[type]));

            }
            long stopTime = TimeUtils.millis();
            // System.out.println(String.format("delta: %d", stopTime - startTime) );
        }

        List<Layer> layers = new ArrayList<>();

        for(int k = 0 ; k < tilesMapFromFile.size() ; k++) {

            layers.add(new Layer(Settings.GRID_TILES_WIDTH, Settings.GRID_TILES_HEIGHT, tilesMapFromFile.get(k).getCellType()));

            for (int i = 0; i < Settings.GRID_TILES_WIDTH; i++) {
                for (int j = 0; j < Settings.GRID_TILES_HEIGHT; j++) {

                    short mapTile = tilesMapFromFile.get(k).getTile(
                            Settings.GRID_TILES_WIDTH - i - 1 + x * Settings.GRID_TILES_WIDTH,
                            Settings.GRID_TILES_HEIGHT - j - 1 + y * Settings.GRID_TILES_HEIGHT);

                    layers.get(k).setTile(i, j, mapTile);
                }
            }
        }

        int xx = x * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT - y * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT;
        int yy = (x * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT + y * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT) / 2;

        String key = String.format("%d_%d", x, y);

        return new TileGroup(xx, yy, layers, gameObjects.get(key));
    }

    public TileGroup getGroup(int x, int y){

//        x = 0;
//        y  =0 ;
//        if(x >= 0 && x < Settings.WORLD_WIDTH/(Settings.TILE_WIDTH*Settings.GRID_TILES_WIDTH) && y >= 0 && y < Settings.WORLD_HEIGHT/(Settings.TILE_HEIGHT*Settings.GRID_TILES_HEIGHT)){}
//        else
//            return new TileGroup();

        List<Layer> layers = new ArrayList<>();

        for(int k = 0 ; k < mapGenerator.getMap().size() ; k++) {

            layers.add(new Layer(Settings.GRID_TILES_WIDTH, Settings.GRID_TILES_HEIGHT, mapGenerator.getMap().get(k).getCellType()));

            for (int i = 0; i < Settings.GRID_TILES_WIDTH; i++) {
                for (int j = 0; j < Settings.GRID_TILES_HEIGHT; j++) {

                    short mapTile = mapGenerator.getMap().get(k).getTile(
                            Settings.GRID_TILES_WIDTH - i - 1 + x * Settings.GRID_TILES_WIDTH,
                            Settings.GRID_TILES_HEIGHT - j - 1 + y * Settings.GRID_TILES_HEIGHT);

                    layers.get(k).setTile(i, j, mapTile);
                }
            }
        }

        int xx = x * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT - y * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT;
        int yy = (x * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT + y * Settings.TILE_HEIGHT * Settings.GRID_TILES_HEIGHT) / 2;

        String key = String.format("%d_%d", x, y);

        return new TileGroup(xx, yy, layers, mapGenerator.getGameObjects().get(key));
    }
}