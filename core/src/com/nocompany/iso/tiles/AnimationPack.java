package com.nocompany.iso.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.HashMap;

public class AnimationPack {

    private HashMap<String, HashMap<Integer, Animation<Texture>>> textures;
//    private Animation<Texture> animation;

    public AnimationPack(String folder){

//        animation = new Animation<Texture>(Animation.PlayMode.LOOP);

        textures = new HashMap<>();

        FileHandle dirHandle = Gdx.files.internal(folder);

        for (FileHandle entry: dirHandle.list()) {

            if(entry.isDirectory()){

                FileHandle[] fileHandleList = entry.list();

                textures.put(entry.name(), new HashMap<Integer, Animation<Texture>>());
                //System.out.println(entry.name());


                for ( int i = 0 ; i < fileHandleList.length ; i++ ) {

                    FileHandle[] pngFileHandleList = fileHandleList[i].list();
                    Texture[] texturesArrays = new Texture[pngFileHandleList.length];
                    //System.out.println(fileHandleList[i].name());

                    for(int j = 0 ; j < pngFileHandleList.length ; j ++){
                        //System.out.println(pngFileHandleList[j].path());
                        Texture texture = new Texture(Gdx.files.internal(pngFileHandleList[j].path()));
                        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                        int index = Integer.valueOf(pngFileHandleList[j].nameWithoutExtension());
                        texturesArrays[index] = texture;
//                        textures.get(entry.name()).get( Integer.valueOf(fileHandleList[i].name()) )[index] = texture;
                    }

                    Animation<Texture> animation = new Animation<Texture>(1f/15, texturesArrays);
                    textures.get(entry.name()).put( Integer.valueOf(fileHandleList[i].name()) , animation);
                }
            }
        }
    }

    public Animation<Texture> getAnimations(String name, int id){
        return textures.get(name).get(id);
    }
}
