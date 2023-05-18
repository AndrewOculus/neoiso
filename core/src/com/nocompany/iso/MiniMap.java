package com.nocompany.iso;

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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.nocompany.iso.tiles.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;

public class MiniMap{

    private static MiniMap miniMap;

    public static MiniMap getInstance() {
        if(miniMap == null)
            miniMap = new MiniMap();

        return miniMap;
    }

    private FrameBuffer miniMapTilesFrameBuffer;
    private OrthographicCamera bakerCamera;
    private SpriteBatch spriteBatch;
    private ArrayDeque<MapTileGroup> tileGroupQueue;

    public MiniMap(){
        tileGroupQueue = new ArrayDeque<MapTileGroup>();
        miniMapTilesFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 4096, 4096, false);
        spriteBatch = new SpriteBatch();
        bakerCamera = new OrthographicCamera();
        bakerCamera.position.set(4096/2, 4096/2, 0);
        bakerCamera.viewportWidth = 4096;
        bakerCamera.viewportHeight = 4096;
        bakerCamera.update();
    }

    public void asyncBakeToMap(MapTileGroup mapTileGroup){
        tileGroupQueue.add(mapTileGroup);
    }

    public void asyncUpdate(){

        if( tileGroupQueue.isEmpty() ){
            return;
        }

        MapTileGroup mapTileGroup = tileGroupQueue.peek();
        tileGroupQueue.remove();

        miniMapTilesFrameBuffer.begin();
        spriteBatch.setProjectionMatrix(bakerCamera.combined);
        spriteBatch.begin();

        System.out.println( String.format("Minimap bake %d %d", mapTileGroup.trueX, mapTileGroup.trueY) );
        // for( int y = 0 ; y < Settings.GRID_TILES_HEIGHT ; ++y ){
        //     for( int x = 0 ; x < Settings.GRID_TILES_WIDTH ; ++x ){
                
        //     }
        // }

        spriteBatch.end();
        miniMapTilesFrameBuffer.end();
    }
}