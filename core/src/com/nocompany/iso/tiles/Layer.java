package com.nocompany.iso.tiles;

import com.nocompany.iso.tiles.CellType;

public class Layer {

    private short[][] tiles;
    private int height;
    private int width;
    private CellType cellType;

    public Layer(int width, int height, CellType cellType){
        tiles = new short[width][height];
        this.width = width;
        this.height = height;
        this.cellType = cellType;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public short getTile(int x, int y){
        if(x >= 0 && y >= 0 && x < width && y < height)
            return tiles[x][y];
        else
            return CellType.EMPTY.getTileId();
    }

    public void setTile(int x, int y, short tile){
        if(x >= 0 && y >= 0 && x < width && y < height)
        tiles[x][y] = tile;
    }

    public CellType getCellType(){
        return cellType;
    }

}