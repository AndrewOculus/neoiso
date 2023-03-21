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
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.utils.TimeUtils;
import com.nocompany.iso.tiles.CellType;
import com.nocompany.iso.tiles.CellsPack;
import com.nocompany.iso.tiles.TileGroup;
import com.nocompany.iso.utils.AssetLoader;
import com.nocompany.iso.tiles.Layer;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Baker {

    private static Baker baker;

    public static Baker getInstance() {
        if(baker == null)
            baker = new Baker();

        return baker;
    }

    private FrameBuffer tilesFrameBuffer;
    private SpriteBatch spriteBatch;
    private OrthographicCamera bakerCamera;

    // private Texture emptyFrame;

    private Queue<TileGroup> tileGroupQueue;

    private long times = 0;
    private long nums = 0;

    public Baker(){
        spriteBatch = new SpriteBatch();
        tilesFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH) + 2, (int)(Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT) +2 , false);
        bakerCamera = new OrthographicCamera();
        tileGroupQueue = new ArrayDeque<>();
    }

    public void bakeAsync(TileGroup tileGroup){
        tileGroupQueue.add(tileGroup);
    }

    public void asyncUpdate(){
        if(!tileGroupQueue.isEmpty()){
            bake( tileGroupQueue.remove());
        }
    }

    public Texture bake(TileGroup tileGroup){

        List<Layer> tiles = tileGroup.getTiles();

        bakerCamera.position.set(Settings.TILE_WIDTH/2,Settings.TILE_HEIGHT * tiles.get(0).getHeight()/2,0);
        bakerCamera.viewportWidth = Settings.TILE_WIDTH * tiles.get(0).getWidth();
        bakerCamera.viewportHeight = Settings.TILE_HEIGHT * tiles.get(0).getHeight();
        bakerCamera.update();

        long timeMillis = TimeUtils.millis();

        tilesFrameBuffer.begin();
        spriteBatch.setProjectionMatrix(bakerCamera.combined);
        spriteBatch.enableBlending();
        spriteBatch.begin();

        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (int j = 0; j < tiles.get(0).getWidth(); j++) {
            for (int i = 0; i < tiles.get(0).getHeight(); i++) {
                for (int k = 0; k < tiles.size() ; k++) {

                    short tile = (short) (tiles.get(k).getTile(j, i) & 0xff);

                    if(tile == CellType.EMPTY.getTileId()){
                        continue;
                    }

                    float x = i * Settings.TILE_HEIGHT - j * Settings.TILE_HEIGHT;
                    float y = (i * Settings.TILE_HEIGHT + j * Settings.TILE_HEIGHT) / 2;

                    TextureAtlas cellsPack = AssetLoader.GetInstance().getCellsAtlas(tile);
                    short currentTile = tiles.get(k).getTile(j, i);
                    short tileType = (short) ((currentTile >> 8) & 0xff);

                    TextureRegion texture = AssetLoader.GetInstance().getTextureByNumberAtlases(cellsPack, tileType);

                    spriteBatch.draw(texture, x, y);
                }
            }
        }
        spriteBatch.end();
        tilesFrameBuffer.end();

        times += TimeUtils.millis() - timeMillis;
        nums += 1;

        System.out.println(String.format("bake tim ms: %d std: %f", TimeUtils.millis() - timeMillis, times*1f/nums));

        if(nums > 50){
            nums = 0;
            times = 0;
        }

        Texture texture = tilesFrameBuffer.getColorBufferTexture();

        tilesFrameBuffer.getTextureAttachments().removeIndex(0);
        tilesFrameBuffer.dispose();
        tilesFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH) + 2, (int)(Settings.GRID_TILES_HEIGHT * Settings.TILE_HEIGHT) + 2, false);

        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        
        tileGroup.setAtlas(texture);

        return texture;
    }
}
