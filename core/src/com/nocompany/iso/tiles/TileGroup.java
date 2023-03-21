package com.nocompany.iso.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.nocompany.iso.Baker;
import com.nocompany.iso.objects.GameObject;
import com.nocompany.iso.objects.SceneObject;

import java.util.ArrayList;
import java.util.List;

public class TileGroup implements Disposable {

    private int x, y;
    private List<Layer> tiles;
    private ArrayList<SceneObject> gameObjects;
    private Baker baker;
    private Texture atlas;
    private boolean isBaked = false;
    private boolean isEmpty = false;
    private long lastUpdate;

    public TileGroup (int x, int y, List<Layer> tiles, List<SceneObject> gameObjects){
        this.x = x;
        this.y = y;
        this.tiles = tiles;
        this.gameObjects = (ArrayList<SceneObject>) gameObjects;
        this.baker = Baker.getInstance();
        this.lastUpdate = TimeUtils.millis();

        if(this.gameObjects != null)
        for (SceneObject sc: this.gameObjects) {
            sc.setActive(true);
        }

    }

    public TileGroup (){
        isEmpty = true;
    }

    public void render(SpriteBatch spriteBatch){
        if(isEmpty)
            return;

        if(atlas == null && !isBaked) {
            baker.bakeAsync(this);
            isBaked = true;
        }

        if(atlas != null)
            spriteBatch.draw(atlas, x, y);

        this.lastUpdate = TimeUtils.millis();
    }

    public ArrayList<SceneObject> getObjects(){
        return gameObjects;
    }

    public void setAtlas(Texture atlas){
        if(this.atlas != null)
            this.atlas.dispose();

        this.atlas = atlas;
    }

    public Texture getAtlas(){
        return atlas;
    }

    public List<Layer> getTiles(){
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

    public void dispose() {

        if(this.gameObjects != null)
        for (SceneObject sc: this.gameObjects) {
            sc.setActive(false);
        }

        if(atlas != null) {
            atlas.dispose();
            atlas = null;
            isBaked = false;

            System.out.println("Atlas object " + this.hashCode() + " disposed");
        }
    }
}
