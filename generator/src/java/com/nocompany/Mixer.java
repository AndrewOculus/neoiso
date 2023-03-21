package com.nocompany;

import java.util.ArrayList;
import java.util.Random;

import com.nocompany.noise.OpenSimplexNoise;
import com.nocompany.utils.TilesUtil;

public class Mixer {

    private ArrayList<Layer> layers = new ArrayList<>();
    private final Random random;

    public Mixer( Random random ){
        this.random = random;
    }

    public void addLayer( Cell cell, float div, float threshold ){
        layers.add(new Layer(random.nextLong(), cell, div, threshold));
    }

    int getCell(int x, int y){

        Cell[] cells = new Cell[layers.size()]; 

        for( int i = 0 ; i < layers.size() ; ++i ){
            cells[i] = layers.get(i).getCell(x, y);
        }

        Cell[] finalCells = new Cell[3];

        for( int i = 0 ; i < 3 ; ++i ){
            finalCells[i] = Cell.LEAF;
        }

        int id = 0;

        for( int i = 0 ; i < cells.length ; ++i ){
            if( cells[i] != Cell.EMPTY ){
                finalCells[id++] = cells[i];
                if( id == 3 )
                    break;
            }
        }


        int result = TilesUtil.setCell((short)finalCells[0].getTileId(), (short)finalCells[1].getTileId(), (short)finalCells[2].getTileId(), (short)0, (short)0);

        // int result = (finalCells[0].getTileId() << 24) | (finalCells[1].getTileId() << 16) | (finalCells[2].getTileId() << 8);
        return result;
    }
}

class Layer {

    private long layerSeed;
    private Cell cell;
    private float div;
    private float threshold;

    private OpenSimplexNoise openSimplexNoise;

    Layer( long layerSeed, Cell cell, float div, float threshold ){
        this.layerSeed = layerSeed;
        this.cell = cell;
        this.div = div;
        this.threshold = threshold;

        openSimplexNoise = new OpenSimplexNoise(layerSeed);
    }

    Cell getCell( int x, int y ){
        double noise = openSimplexNoise.eval((float)x / div, (float)y / div);
        return (noise > threshold) ? cell : Cell.EMPTY;
    }

}