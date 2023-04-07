package com.nocompany;


public class Layer{

    private Cell cell;
    private int width;
    private int height;
    private Byte[] matrix;

    public Layer(Cell cell, int width, int height){
        this.cell = cell;
        this.width = width;
        this.height = height;
        this.matrix = new Byte[width * height];
    }

    public void setCell( int x, int y, Byte cell ){
        if( x >= 0 && y >= 0 && x < width && y < height ){
            if( matrix != null )
                matrix[ y*width + x ] = cell;
        }
    }

    public Byte getCell( int x, int y ){
        return matrix[ y*width + x ];
    }

    public Byte[] getMatrix(){
        return matrix;
    }

    public void setMatrix( Byte[] matrix){
        this.matrix = null;
        System.gc();
        this.matrix = matrix;
    }

    public void dropMatrix(){
        this.matrix = null;
        System.gc();
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public Cell getType(){
        return cell;
    }

}