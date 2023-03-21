package com.nocompany.iso.utils.fitting;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import com.nocompany.iso.Settings;
import com.nocompany.iso.tiles.CellsPack;
import com.nocompany.iso.tiles.Layer;
import com.nocompany.iso.utils.AssetLoader;
import com.nocompany.iso.tiles.*;

public class TilesFitting3 extends ApplicationAdapter {

    static final Gson gson = new Gson();

    private Vector3 unproj;
    private Vector3 coords;

    private final float tilew = 64;
    private final float tileh = 32;

    FrameBuffer fbo;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    BitmapFont bitmapFont;

    TileMap tileMap;

    private HashMap<String, byte[]> tilesMap = new HashMap<>();

    private List<Layer> tilesMapFromFile = new ArrayList<>();

    byte field; 
    short currentState = 0;
    short savedState = -1;

    @Override
    public void create() {

        field = 0;

        unproj = new Vector3(0, 0, 0);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        bitmapFont = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 0);

        camera.position.set(0,0,0.0f);
        camera.zoom = 0.8f;

        camera.update();

        try {
            String tiledMap =  new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+"/tiles_disposer2.json")));
            tileMap = gson.fromJson(tiledMap, TileMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

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



    }

    void nextState(){
        currentState ++;
        tileMap.addNewTile(field, currentState);
    }

    void prevState(){
        currentState --;
        tileMap.addNewTile(field, currentState);
    }

    void updateState(CellsPack cellsPack, Layer layer, int i, int j){

        short t = (short)((layer.getTile(j, i)) & 0xff);
        t &= 0xff;
        
        BitSet bitField = new BitSet(8);

        boolean ul = (layer.getTile(j+1, i+1) & 0xff)   == t ? true : false;
        boolean l = (layer.getTile(j+1, i) & 0xff)      == t ? true : false;
        boolean dl = (layer.getTile(j+1, i-1) & 0xff)   == t ? true : false;
        boolean u = (layer.getTile(j, i+1) & 0xff)      == t ? true : false;
        boolean d = (layer.getTile(j, i-1) & 0xff)      == t ? true : false;
        boolean ur = (layer.getTile(j-1, i+1) & 0xff)   == t ? true : false;
        boolean r = (layer.getTile(j-1, i) & 0xff)      == t ? true : false;
        boolean dr = (layer.getTile(j-1, i-1) & 0xff)   == t ? true : false;

        // ul, l, dl, u, d, ur, r, dr

        bitField.set(7, ul);
        bitField.set(6, l);
        bitField.set(5, dl);
        bitField.set(4, u);
        bitField.set(3, d);
        bitField.set(2, ur);
        bitField.set(1, r);
        bitField.set(0, dr);

        byte tilesOrder = 0x00;
        if(bitField.toByteArray().length != 0){
            tilesOrder = bitField.toByteArray()[0];// (byte)(Integer.parseInt(String.format("%d%d%d%d%d%d%d%d", ul, l, dl, u, d, ur, r, dr) , 2));         
            field = tilesOrder;
        }

        Short short1 = tileMap.getTile( tilesOrder );

        if(short1 != null){
            currentState = short1.shortValue();
        }

    }

    int selectX = 124;
    int selectY = 158;

    @Override
    public void render () {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        unproj.x = Gdx.input.getX();
        unproj.y = Gdx.input.getY();

        coords = camera.unproject(unproj);

        float px = coords.x - tilew / 2;
        float py = coords.y - tileh / 2;


        if (Gdx.input.isTouched()) {
            camera.position.set(camera.position.x - (Gdx.input.getDeltaX()) * camera.zoom, camera.position.y + (Gdx.input.getDeltaY()) * camera.zoom, 0);
            camera.update();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            camera.zoom += 0.1f;
            camera.update();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            camera.zoom -= 0.1f;
            if (camera.zoom < 0.1) {
                camera.zoom = 0.1f;
            }

            camera.update();
        }

        CellsPack cellsPack = AssetLoader.GetInstance().getCells(10);

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            // updateState(cellsPack);
            
            String jsonInString = gson.toJson(tileMap);
            try {
                Files.write( Paths.get(System.getProperty("user.dir")+"/tiles_disposer2.json"), jsonInString.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Layer layer = tilesMapFromFile.get(6);
        CellsPack cellsPack1 = AssetLoader.GetInstance().getCells(1);

        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            nextState();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            prevState();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectY += 1;
            System.out.println(selectX + " " + selectY);
            updateState(cellsPack1, layer, selectX, selectY);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectY -= 1;
            updateState(cellsPack1, layer, selectX, selectY);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectX += 1;
            updateState(cellsPack1, layer, selectX, selectY);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectX -= 1;
            updateState(cellsPack1, layer, selectX, selectY);
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        int shift = 0;

        bitmapFont.draw(spriteBatch, ((int)(field) & 0xff)+"", -256, 32);


        spriteBatch.setColor(1.0f, 1.0f, 1.0f, .5f);
        for (int j = 0; j < 300; j++) {
            for (int i = 0; i < 300; i++) {

                float x = i * Settings.TILE_HEIGHT - j * Settings.TILE_HEIGHT + 1000.0f;
                float y = (i * Settings.TILE_HEIGHT + j * Settings.TILE_HEIGHT) / 2 - 4500.0f;

                if( i == selectX && j == selectY){
                    short tile1 = (short)(91 & 0xff);
                    Texture texture = AssetLoader.GetInstance().getTextureByNumber(cellsPack1, tile1);
                    spriteBatch.draw(texture, x, y, 64, 32, 0, 0, 64, 32, false, true);
                }

                // x+1 y up
                if( i == 3 && j == 2){
                    short tile1 = (short)(84 & 0xff);
                    Texture texture = AssetLoader.GetInstance().getTextureByNumber(cellsPack1, tile1);
                    spriteBatch.draw(texture, x, y, 64, 32, 0, 0, 64, 32, false, true);
                }

                // x-1 y down
                if( i == 1 && j == 2){
                    short tile1 = (short)(85 & 0xff);
                    Texture texture = AssetLoader.GetInstance().getTextureByNumber(cellsPack1, tile1);
                    spriteBatch.draw(texture, x, y, 64, 32, 0, 0, 64, 32, false, true);
                }

                //x y-1 right
                if( i == 2 && j == 1){
                    short tile1 = (short)(87 & 0xff);
                    Texture texture = AssetLoader.GetInstance().getTextureByNumber(cellsPack1, tile1);
                    spriteBatch.draw(texture, x, y, 64, 32, 0, 0, 64, 32, false, true);
                }

                //x y+1 left
                if( i == 2 && j == 3){
                    short tile1 = (short)(86 & 0xff);
                    Texture texture = AssetLoader.GetInstance().getTextureByNumber(cellsPack1, tile1);
                    spriteBatch.draw(texture, x, y, 64, 32, 0, 0, 64, 32, false, true);
                }

                short tile = (short) (layer.getTile(j, i) & 0xff);

                if(tile == CellType.EMPTY.getTileId()){
                    continue;
                }

                short t = (short)((layer.getTile(j, i)) & 0xff);
                t &= 0xff;
                
                BitSet bitField = new BitSet(8);

                boolean ul = (layer.getTile(j+1, i+1) & 0xff)   == t ? true : false;
                boolean l = (layer.getTile(j+1, i) & 0xff)      == t ? true : false;
                boolean dl = (layer.getTile(j+1, i-1) & 0xff)   == t ? true : false;
                boolean u = (layer.getTile(j, i+1) & 0xff)      == t ? true : false;
                boolean d = (layer.getTile(j, i-1) & 0xff)      == t ? true : false;
                boolean ur = (layer.getTile(j-1, i+1) & 0xff)   == t ? true : false;
                boolean r = (layer.getTile(j-1, i) & 0xff)      == t ? true : false;
                boolean dr = (layer.getTile(j-1, i-1) & 0xff)   == t ? true : false;

                // ul, l, dl, u, d, ur, r, dr

                bitField.set(7, ul);
                bitField.set(6, l);
                bitField.set(5, dl);
                bitField.set(4, u);
                bitField.set(3, d);
                bitField.set(2, ur);
                bitField.set(1, r);
                bitField.set(0, dr);

                byte tilesOrder = 0x00;
                if(bitField.toByteArray().length != 0){
                    tilesOrder = bitField.toByteArray()[0];// (byte)(Integer.parseInt(String.format("%d%d%d%d%d%d%d%d", ul, l, dl, u, d, ur, r, dr) , 2));         
                }

                Short tilePosition = tileMap.getTile( tilesOrder );
                if(tilePosition != null ){

                    short tile1 = (short)tilePosition.shortValue();
                    Texture texture = AssetLoader.GetInstance().getTextureByNumber(cellsPack1, tile1);
                    // spriteBatch.draw(texture, x, y);
                    spriteBatch.draw(texture, x, y, 64, 32, 0, 0, 64, 32, false, true);

                }
                else{
                    short tile1 = (short)(0 & 0xff);
                    Texture texture = AssetLoader.GetInstance().getTextureByNumber(cellsPack1, tile1);
                    // spriteBatch.draw(texture, x, y);
                    spriteBatch.draw(texture, x, y, 64, 32, 0, 0, 64, 32, false, true);

                }


                
            }
        }
        spriteBatch.end();


        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {

                float x = i * tileh - j * tileh;
                float y = (i * tileh + j * tileh) / 2;

                if( i == 1 && j == 1 ){
                    
                    Texture texture;
                    if(savedState == -1){
                        texture = AssetLoader.GetInstance().getTextureByNumber(cellsPack, currentState);
                    }else{
                        texture = AssetLoader.GetInstance().getTextureByNumber(cellsPack, savedState);
                    }

                    // spriteBatch.draw(texture, x, y);
                    spriteBatch.draw(texture, x, y, 64, 32, 0, 0, 64, 32, false, true);
                    continue;
                }

                int isVisible = (field >> shift) & 1;

                Texture texture = cellsPack.getTexture("base_1");

                if (isVisible == 1){
                    spriteBatch.draw(texture, x, y);
                    bitmapFont.draw(spriteBatch, shift+"", x + 32, y + 32);
                }
                shift ++;
            }
        }

        spriteBatch.end();

    }
}