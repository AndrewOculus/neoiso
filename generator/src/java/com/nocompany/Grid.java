package com.nocompany;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import java.util.ArrayList;

import org.lwjgl.Sys;

import com.nocompany.utils.ZipUtils;


public class Grid {

    private int sizeX, sizeY;
    private int posX, posY;
    private int groupSizeX, groupSizeY;

    private ArrayList<Layer> layers;
    private int layersCount;

    private byte[] data;
    private boolean isLoaded;

    private byte[] arrayX0 = null;
    private byte[] arrayXN = null;
    private byte[] array0Y = null;
    private byte[] arrayNY = null;

    public Grid(int posX, int posY, int sizeX, int sizeY, int groupSizeX, int groupSizeY, Cell[] cells ){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.posX = posX;
        this.posY = posY;
        this.groupSizeX = groupSizeX;
        this.groupSizeY = groupSizeY;

        this.layers = new ArrayList<Layer>();
        // this.layersCount = layers.length;

        for( int i = 0 ; i < cells.length ; ++i ){
            this.layers.add( new Layer( cells[i], sizeX, sizeY ) );
        }

    }

    public void Init(){
        // data = new byte[sizeX*sizeY*4];
        // isLoaded = true;
    }

    public int getLayersCount(){
        return layers.size();
    }

    public void copyCellsToFramework(){

        this.arrayX0 = new byte[sizeX*4];
        this.arrayXN = new byte[sizeX*4];
        this.array0Y = new byte[sizeY*4];
        this.arrayNY = new byte[sizeY*4];

        for( int i = 0 ; i < sizeX*4 ; ++i ){
            this.arrayXN[i] = data[i];
        }

        for( int i = 0 ; i < sizeX*4 ; ++i ){
            this.arrayX0[i] = data[i + sizeX*(sizeY-1)*4];
        }

        for( int i = 0 ; i < sizeY*4 ; i+=4 ){
            this.array0Y[i+0] = data[0 + sizeX*i];
            this.array0Y[i+1] = data[1 + sizeX*i];
            this.array0Y[i+2] = data[2 + sizeX*i];
            this.array0Y[i+3] = data[3 + sizeX*i];
        }

        for( int i = 0 ; i < sizeY*4 ; i+=4 ){
            this.arrayNY[i+0] = data[(sizeX-1)*4 + 0 + sizeX*i];
            this.arrayNY[i+1] = data[(sizeX-1)*4 + 1 + sizeX*i];
            this.arrayNY[i+2] = data[(sizeX-1)*4 + 2 + sizeX*i];
            this.arrayNY[i+3] = data[(sizeX-1)*4 + 3 + sizeX*i];
        }
    }

    public Layer getLayer(int index){
        return layers.get(index);
    }

    public Layer addLayer(Cell cell){
        Layer layer = new Layer( cell, sizeX, sizeY );
        layers.add( layer );
        return layer;
    }

    public int getCell(int globalCellX, int globalCellY ){
        return 0;
    }

    // public int getCellGlobal( int globalCellX, int globalCellY ){

    //     if( posX >= globalCellX &&
    //         posY >= globalCellY &&
    //         posX + sizeX < globalCellX &&
    //         posY + sizeY < globalCellY ){
                
    //     }else{
    //         return -1;
    //     }

    //     int localCellX = globalCellX % sizeX;
    //     int localCellY = globalCellY % sizeY;


    
    //     return val;
        
    // }

    public int getCellLocal( int localCellX, int localCellY ){
        // return data[ localCellY*sizeX + localCellX ];
        byte b0 = data[ localCellY*sizeX*4 + localCellX*4 + 0 ];
        byte b1 = data[ localCellY*sizeX*4 + localCellX*4 + 1 ];
        byte b2 = data[ localCellY*sizeX*4 + localCellX*4 + 2 ];
        byte b3 = data[ localCellY*sizeX*4 + localCellX*4 + 3 ];

        int val = (int)(b0 << 24 | b1 << 16 | b2 << 8 | b3);
        return val;
    }

    public boolean probe( int globalCellX, int globalCellY ){

        if( data == null )
            return false;

        int localCellX = globalCellX - posX*sizeX;
        int localCellY = globalCellY - posY*sizeY;

        if( localCellX >= 0 && localCellX < sizeX && localCellY >= 0 && localCellY < sizeY ){
            return true;
        }else{
            return false;
        } 
    }

    public void setCell( int globalCellX, int globalCellY, int value ){

        int localCellX = globalCellX - posX*sizeX;
        int localCellY = globalCellY - posY*sizeY;

        if( localCellX >= 0 && localCellX < sizeX && localCellY >= 0 && localCellY < sizeY ){

            byte b0 = (byte)(value >> 24 & 0xFF);
            byte b1 = (byte)(value >> 16 & 0xFF);
            byte b2 = (byte)(value >> 8 & 0xFF);
            byte b3 = (byte)(value >> 0 & 0xFF);
    
            data[ localCellY*sizeX*4 + localCellX*4 + 0 ] = b0;
            data[ localCellY*sizeX*4 + localCellX*4 + 1 ] = b1;
            data[ localCellY*sizeX*4 + localCellX*4 + 2 ] = b2;
            data[ localCellY*sizeX*4 + localCellX*4 + 3 ] = b3;
        }
    }

    public void drop(){
        return;
        // copyCellsToFramework();
        // for( int i = 0 ; i < layers.size() ; ++i ){

        //     Layer layer = layers.get(i);

        //     try{

        //         FileOutputStream out = new FileOutputStream("map//"+posX+"_"+posY+"_"+i);

        //         byte[] store = new byte[ sizeX * sizeY ];

        //         for( int y = 0 ; y < sizeY ; ++y ){
        //             for( int x = 0 ; x < sizeX ; ++x ){
        //                 store[ y*sizeX + x ] = layer.getMatrix()[ y * sizeX + x ] == null ? (byte)0 : (byte)1;
        //             }
        //         }

        //         byte[] bytes = ZipUtils.zipBytes(posX+"_"+posY+".zip", store );

        //         out.write(bytes);
        //         out.close();

        //         store = null;

        //     } catch (IOException e) {
        //         System.err.println(e);
        //     }
        // }

        // for( int i = 0 ; i < layers.size() ; ++i ){
        //     layers.get(i).dropMatrix();
        // }
        // // isLoaded = false;
        // // data = null;
        // System.gc();

        // System.out.println("store " + posX + " " + posY);
    }

    public void storeAsIs(){
        try{
            FileOutputStream out = new FileOutputStream("map//"+"raw_"+posX+"_"+posY);
            out.write(data);
            out.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void release(){
        try{
            FileOutputStream out = new FileOutputStream("map//"+"raw_"+posX+"_"+posY);
            out.write(ByteBuffer.allocate(4).putInt(4096).array());
            out.write(ByteBuffer.allocate(4).putInt(4096).array());
            out.write(data);
            out.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void read(){
        return;


        // for( int i = 0 ; i < layers.size() ; ++i ){

        //     Layer layer = layers.get(i);

        //     File file = new File("map//"+posX+"_"+posY+"_"+i);
        //     byte[] zipData = new byte[(int) file.length()];

        //     try{
        //         FileInputStream fis = new FileInputStream(file);
        //         fis.read(zipData);
        //     }catch( IOException e){
        //         System.err.println(e);
        //     }

        //     byte[] tmp;
        //     Byte[] byteTmp = new Byte[sizeX*sizeY]; 

        //     try {
        //         tmp = ZipUtils.unzipBytes(zipData);

        //         for( int y = 0 ; y < sizeY ; ++y ){
        //             for( int x = 0 ; x < sizeX ; ++x ){
        //                 byteTmp[ y*sizeX + x ] = (tmp[ y*sizeX + x ] == (byte)0) ? null : new Byte((byte)0);
        //             }
        //         }
        //     } catch (IOException e) {
        //         System.err.println(e);
        //     }


        //     tmp = null;

        //     layer.setMatrix( byteTmp );
        // }

        // isLoaded = true;
        // System.gc();

        // System.out.println("read " + posX + " " + posY);
    }

    public int getPosX(){
        return posX;
    }

    public int getPosY(){
        return posY;
    }

    public int getSizeX(){
        return sizeX;
    }

    public int getSizeY(){
        return sizeY;
    }

    public boolean isLoaded(){
        return isLoaded;
    }
}
