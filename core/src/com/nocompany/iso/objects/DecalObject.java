package com.nocompany.iso.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nocompany.iso.Settings;


public class DecalObject implements SceneObject {

    TextureRegion textureRegion;
    float currentTime;
    float x;
    float y;
    boolean isActive;

    public DecalObject( float x, float y, TextureRegion textureRegion){
        this.textureRegion = textureRegion;
        this.x = x;
        this.y = y;
        this.isActive = true;
    }

    @Override
    public void render(SpriteBatch batch) {

        // float xx = Settings.GRID_TILES_WIDTH * Settings.TILE_WIDTH/2 +  x - y;
        // float yy = (x + y) / 2;
        // batch.draw(textureRegion, xx - textureRegion.getRegionWidth()/2 , yy);
        if(isActive)
            batch.draw(textureRegion, x, y);

            // batch.draw(textureRegion, x, y, 0.5f, 0.5f, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), 1.0f, -1.0f, 0.0f );
    }

    @Override
    public void renderShadow(SpriteBatch batch) {
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
        return y - textureRegion.getRegionHeight()/2;
    }

    @Override
    public float getZ() {
        return y;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public GameObjectTypes getObjectType() {
        return null;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }
}
