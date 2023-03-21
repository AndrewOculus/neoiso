package com.nocompany.iso.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nocompany.iso.Settings;
import com.nocompany.iso.utils.AssetLoader;

public class GameObject implements SceneObject {

    private GameObjectTypes gameObjectTypes;
    private Texture texture;
    private Texture shadow;
    private float x, y;
    private boolean active = false;

    public GameObject(float x, float y, GameObjectTypes gameObjectType, boolean test){
        this.gameObjectTypes = gameObjectType;
        if(!test){
            this.texture = AssetLoader.GetInstance().getGameObjectsPack(gameObjectType);
            this.shadow = AssetLoader.GetInstance().getGameObjectsPackShadows(gameObjectType);
        }

        this.x = x;
        this.y = y;
    }

    public GameObject(float x, float y, GameObjectTypes gameObjectType){
        this.gameObjectTypes = gameObjectType;
        this.texture = AssetLoader.GetInstance().getGameObjectsPack(gameObjectType);
        this.shadow = AssetLoader.GetInstance().getGameObjectsPackShadows(gameObjectType);
        this.x = x;
        this.y = y;
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    public GameObjectTypes getObjectType(){
        return gameObjectTypes;
    }

    @Override
    public void render(SpriteBatch spriteBatch){
        float xx = Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH/2 +  x  - y ;
        float yy = (x + y) / 2;
        spriteBatch.draw(texture, xx - texture.getWidth()/2 , yy);
    }

    @Override
    public void renderShadow(SpriteBatch spriteBatch) {
        float xx = Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH/2 +  x  - y ;
        float yy = (x + y) / 2;
        spriteBatch.draw(shadow, xx - shadow.getWidth()/2 , yy - shadow.getHeight()/4);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return (y + x)/2.0f;
    }

    @Override
    public void setX(float x) {

    }

    @Override
    public void setY(float y) {

    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
