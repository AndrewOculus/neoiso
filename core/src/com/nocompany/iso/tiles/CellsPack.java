package com.nocompany.iso.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CellsPack {

    private HashMap<String, Texture> textures;

    public CellsPack(String folder){

        textures = new HashMap<>();

        FileHandle dirHandle = Gdx.files.internal(folder);

        for (FileHandle entry: dirHandle.list()) {
            Texture texture = new Texture(Gdx.files.internal(entry.path()), true);
            texture.setFilter(Texture.TextureFilter.MipMapNearestNearest, Texture.TextureFilter.Nearest);
            textures.put(entry.nameWithoutExtension() , texture);
        }
    }

    public Texture getTexture(String name){
        return textures.get(name);
    }

    public Set<String> getKeys(){
        return textures.keySet();
    }

    public List<Object> getKeysList(){
        return textures.keySet().stream().collect(Collectors.toList());
    }

}
