package com.nocompany.iso.utils;

import com.badlogic.gdx.utils.TimeUtils;
import com.nocompany.iso.Settings;
import com.nocompany.iso.tiles.MapTileGroup;

import java.util.HashMap;

public class GC {

    private static GC gc;

    public static GC getInstance(HashMap<String, MapTileGroup> stringTileGroupHashMap) {
        if(gc == null)
            gc = new GC(stringTileGroupHashMap);

        return gc;
    }

    private HashMap<String, MapTileGroup> stringTileGroupHashMap;
    private int iter = 0;

    private GC( HashMap<String, MapTileGroup> stringTileGroupHashMap){
        this.stringTileGroupHashMap = stringTileGroupHashMap;
    }

    public void refresh(){

        if(stringTileGroupHashMap.keySet().size() > iter){

            String key = stringTileGroupHashMap.keySet().toArray()[iter].toString();
            MapTileGroup tileGroup = (MapTileGroup)(stringTileGroupHashMap.get(key));
            long lastUpdate = (tileGroup).getLastUpdate();

            if(TimeUtils.millis() - lastUpdate > Settings.TIME_TO_DISPOSE){
                tileGroup.dispose();
                stringTileGroupHashMap.remove(key);
                System.out.println("Size " + stringTileGroupHashMap.size());
            }

            iter++;
        }else {
            iter = 0;
        }

    }

}
