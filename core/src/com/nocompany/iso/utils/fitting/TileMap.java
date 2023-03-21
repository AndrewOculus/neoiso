package com.nocompany.iso.utils.fitting;

import java.util.HashMap;

public class TileMap{

    public HashMap<Byte, Short> tiles;

    public TileMap(){
        tiles = new HashMap<Byte, Short>();
    }

    public Short getTile(byte tileOrder){
        return tiles.get(tileOrder);
    }

    public void addNewTile(byte tileOrder, Short tileSet){
        tiles.put( tileOrder, tileSet );
    }

}
