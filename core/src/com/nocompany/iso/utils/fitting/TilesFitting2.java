package com.nocompany.iso.utils.fitting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
import com.nocompany.iso.tiles.CellsPack;
import com.nocompany.iso.utils.AssetLoader;

public class TilesFitting2 extends ApplicationAdapter {


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
    }

    void nextState(){
        field += 1;
    }

    void prevState(){
        field -= 1;
    }

    void updateState(CellsPack cellsPack){

        if(tileMap == null)
            tileMap = new TileMap();

        System.out.println(""+field + " " + currentState);

        tileMap.addNewTile(field, currentState);
    }

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
            updateState(cellsPack);
            
            String jsonInString = gson.toJson(tileMap);
            try {
                Files.write( Paths.get(System.getProperty("user.dir")+"/tiles_disposer2.json"), jsonInString.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            nextState();

            Short tile = tileMap.tiles.get(field);
            if(tile != null){
                savedState = (short)tile.intValue();
            }else{
                savedState = -1;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            prevState();

            Short tile = tileMap.tiles.get(field);
            if(tile != null){
                savedState = tile;
            }else{
                savedState = -1;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            currentState += 1;

            if( currentState > cellsPack.getKeys().toArray().length - 1){
                currentState = (short)(cellsPack.getKeys().toArray().length - 1);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            currentState -= 1;
            if( currentState < 0){
                currentState = 0;
            }
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        int shift = 0;

        bitmapFont.draw(spriteBatch, ((int)(field) & 0xff)+"", -256, 32);

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