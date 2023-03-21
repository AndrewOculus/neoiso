package com.nocompany.iso.tiles;

public class MapLayer {

    private int[][] tiles;
    private int height;
    private int width;

    public MapLayer(int width, int height){
        tiles = new int[width][height];
        this.width = width;
        this.height = height;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getTile(int x, int y){
        if(x >= 0 && y >= 0 && x < width && y < height)
            return tiles[x][y];
        else
            return CellType.EMPTY.getTileId();
    }

    public void setTile(int x, int y, int tile){
        if(x >= 0 && y >= 0 && x < width && y < height)
        tiles[x][y] = tile;
    }
}