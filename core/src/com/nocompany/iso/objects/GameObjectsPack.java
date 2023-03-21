package com.nocompany.iso.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

import java.util.HashMap;

public class GameObjectsPack {

//    HashMap<String, Texture> textureHashMap = new HashMap<>();
    Texture texture;
    Texture shadow;

    public GameObjectsPack(String folder) {
        FileHandle dirHandle = Gdx.files.internal(folder);

        for (FileHandle entry: dirHandle.list()) {

            if(!entry.isDirectory() && entry.nameWithoutExtension().equals("image_0")){
                Texture texture = new Texture(Gdx.files.internal(entry.path()), true);
                texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
                this.texture = texture;
//                textureHashMap.put(entry.name(), texture);
            }
            if(!entry.isDirectory() && entry.nameWithoutExtension().equals("image_1")){
                Texture shadow = new Texture(Gdx.files.internal(entry.path()), true);
                shadow.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
                this.shadow = shadow;
//                textureHashMap.put(entry.name(), texture);
            }
        }
    }

    public Texture getGameObject(){
        return texture; //textureHashMap.get(name);
    }
    public Texture getGameObjectShadow(){
        return shadow; //textureHashMap.get(name);
    }

}