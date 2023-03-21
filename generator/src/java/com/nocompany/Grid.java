package com.nocompany;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.lwjgl.Sys;

import com.nocompany.utils.ZipUtils;


public class Grid {

    private int sizeX, sizeY;
    private int posX, posY;
    private int groupSizeX, groupSizeY;

    private byte[] data;
    private boolean isLoaded;

    private byte[] arrayX0 = null;
    private byte[] arrayXN = null;
    private byte[] array0Y = null;
    private byte[] arrayNY = null;

    public Grid(int posX, int posY, int sizeX, int sizeY, int groupSizeX, int groupSizeY ){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.posX = posX;
        this.posY = posY;
        this.groupSizeX = groupSizeX;
        this.groupSizeY = groupSizeY;

    }

    public void Init(){
        data = new byte[sizeX*sizeY*4];
        isLoaded = true;
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

    public int getCell( int globalCellX, int globalCellY ){

        int localCellX = globalCellX % sizeX;
        int localCellY = globalCellY % sizeY;

        if(data == null){

            if( localCellX == 0 ){
                byte b0 = array0Y[ localCellY*4 + 0 ];
                byte b1 = array0Y[ localCellY*4 + 1 ];
                byte b2 = array0Y[ localCellY*4 + 2 ];
                byte b3 = array0Y[ localCellY*4 + 3 ];
        
                int val = (int)(b0 << 24 | b1 << 16 | b2 << 8 | b3);
        
                return val;
            }

            if( localCellX == (sizeY-1) ){
                byte b0 = arrayNY[ localCellY*4 + 0 ];
                byte b1 = arrayNY[ localCellY*4 + 1 ];
                byte b2 = arrayNY[ localCellY*4 + 2 ];
                byte b3 = arrayNY[ localCellY*4 + 3 ];
        
                int val = (int)(b0 << 24 | b1 << 16 | b2 << 8 | b3);
        
                return val;
            }

            if( localCellY == 0 ){
                byte b0 = arrayX0[ localCellX*4 + 0 ];
                byte b1 = arrayX0[ localCellX*4 + 1 ];
                byte b2 = arrayX0[ localCellX*4 + 2 ];
                byte b3 = arrayX0[ localCellX*4 + 3 ];
        
                int val = (int)(b0 << 24 | b1 << 16 | b2 << 8 | b3);
        
                return val;
            }

            if( localCellY == (sizeX-1) ){
                byte b0 = arrayXN[ localCellX*4 + 0 ];
                byte b1 = arrayXN[ localCellX*4 + 1 ];
                byte b2 = arrayXN[ localCellX*4 + 2 ];
                byte b3 = arrayXN[ localCellX*4 + 3 ];
        
                int val = (int)(b0 << 24 | b1 << 16 | b2 << 8 | b3);
        
                return val;
            }

            return -1;

        }else{

            byte b0 = data[ localCellY*sizeX*4 + localCellX*4 + 0 ];
            byte b1 = data[ localCellY*sizeX*4 + localCellX*4 + 1 ];
            byte b2 = data[ localCellY*sizeX*4 + localCellX*4 + 2 ];
            byte b3 = data[ localCellY*sizeX*4 + localCellX*4 + 3 ];
    
            int val = (int)(b0 << 24 | b1 << 16 | b2 << 8 | b3);
    
            return val;
        }

    }

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

        copyCellsToFramework();
        try{
            FileOutputStream out = new FileOutputStream("map//"+posX+"_"+posY);
            byte[] bytes = ZipUtils.zipBytes(posX+"_"+posY+".zip", data );

            out.write(bytes);
            out.close();

        } catch (IOException e) {
            System.err.println(e);
        }

        isLoaded = false;
        data = null;
        System.gc();

        System.out.println("store " + posX + " " + posY);
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

    public void read(){

        File file = new File("map//"+posX+"_"+posY);
        byte[] zipData = new byte[(int) file.length()];

        try{
            FileInputStream fis = new FileInputStream(file);
            fis.read(zipData);
        }catch( IOException e){
            System.err.println(e);
        }

        try {
            data = ZipUtils.unzipBytes(zipData);
        } catch (IOException e) {
            System.err.println(e);
        }

        isLoaded = true;
        System.gc();

        System.out.println("read " + posX + " " + posY);
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
