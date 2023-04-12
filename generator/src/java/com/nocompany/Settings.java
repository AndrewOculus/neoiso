package com.nocompany;

import java.util.ArrayList;

import com.google.gson.Gson;

import javafx.util.Pair;

public class Settings{
    public final int groupSizeX = 16;
    public final int groupSizeY = 32;

    public final int gridSizeX = 4096;
    public final int gridSizeY = 4096;

    public final int gridsX = 1;
    public final int gridsY = 1;

    public final int seed = 234;

    TileMap tilesDisposer = null;
    TileVariants tileVariants = null;
    Gson gson = null;

    boolean skipInit = false;
    boolean skipRivers = false;

    ArrayList<Pair<Integer, Integer>> pairs = new ArrayList<Pair<Integer, Integer>>();

    public Settings(){
        gson = new Gson();

        pairs.add(new Pair<Integer,Integer>(1, 1)); //(x+1, y+1)
        pairs.add(new Pair<Integer,Integer>(0, 1)); //(x, y+1)
        pairs.add(new Pair<Integer,Integer>(-1, 1));     //(x-1, y+1)
        pairs.add(new Pair<Integer,Integer>(1, 0));  //(x+1, y)
        pairs.add(new Pair<Integer,Integer>(-1, 0));  //(x-1, y)
        pairs.add(new Pair<Integer,Integer>(1, -1));  //(x+1, y-1)
        pairs.add(new Pair<Integer,Integer>(0, -1));  //(x, y-1)
        pairs.add(new Pair<Integer,Integer>(-1, -1));  //(x-1, y-1)

    }

}