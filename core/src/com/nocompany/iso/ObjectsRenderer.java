package com.nocompany.iso;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nocompany.iso.objects.SceneObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ObjectsRenderer {

    private ArrayList<SceneObject> sceneObjects = new ArrayList<>();
    private SceneObjectsComp sceneObjectComparator = new SceneObjectsComp();
    private float timer = 0;

    public ObjectsRenderer(){

    }

    public void addTileObject(SceneObject sceneObject){
        sceneObjects.add(sceneObject);
    }

    public void addTileObjects(ArrayList<SceneObject> sceneObject){
        if(sceneObject != null)
        sceneObjects.addAll(sceneObject);
    }

    public void update(float dt){

        for (int i = 0 ; i < sceneObjects.size() ; i ++){

            SceneObject sceneObject = sceneObjects.get(i);
            if(!sceneObject.isActive()){
                sceneObjects.remove(i);
            }else {
                sceneObject.update(dt);
            }

        }

        timer += dt;

        if(timer > Settings.SORT_UPDATE) {
//            sceneObjects.sort(sceneObjectComparator);
            Collections.sort(sceneObjects, sceneObjectComparator);
            timer = 0;
        }
    }

    public void renderShadow(SpriteBatch batch){

        for (SceneObject sc : sceneObjects) {
            sc.renderShadow(batch);
        }

    }

    public void render(SpriteBatch batch){

        for (SceneObject sc : sceneObjects) {
            sc.render(batch);
        }

    }

}

class SceneObjectsComp implements Comparator<SceneObject>{

    @Override
    public int compare(SceneObject o1, SceneObject o2) {
        return (int)(o2.getZ()*1000 - o1.getZ()*1000);
    }
}
