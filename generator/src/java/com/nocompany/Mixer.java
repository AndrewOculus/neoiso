package com.nocompany;

public class Mixer{
    private GlobalLayer[] globalLayers;
    private Cell[] cells;

    public Mixer( GlobalLayer[] globalLayers ){
        this.globalLayers = globalLayers;
        this.cells = new Cell[globalLayers.length];

        for( int i = 0 ; i < globalLayers.length ; ++i){
            cells[i] = globalLayers[i].getType();
        }

    }

    public Cell[] getCells(){
        return cells;
    }

    public boolean getCell( int x, int y, int layer ){
        return globalLayers[layer].getCell(x, y);
    }
}