package com.nocompany;

import java.util.ArrayList;
import java.util.Random;

import com.nocompany.noise.OpenSimplexNoise;
import com.nocompany.utils.TilesUtil;

public class GlobalLayer{

    private long layerSeed;
    private Cell cell;
    private float div;
    private float threshold;
    private boolean isFull;

    private OpenSimplexNoise openSimplexNoise;

    public GlobalLayer( Cell cell, boolean isFull ){
        this.cell = cell;
        this.isFull = isFull;
    }

    public GlobalLayer( Cell cell, long layerSeed, float div, float threshold ){
        this.layerSeed = layerSeed;
        this.div = div;
        this.cell = cell;
        this.threshold = threshold;
        this.openSimplexNoise = new OpenSimplexNoise(layerSeed);
    }

    public Cell getType(){
        return cell;
    }

    boolean getCell( int x, int y ){
        if(isFull){
            return true;
        }

        double noise = openSimplexNoise.eval((float)x / div, (float)y / div);
        return (noise > threshold) ? true : false;
    }
}