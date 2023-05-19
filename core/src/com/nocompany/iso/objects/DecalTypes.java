package com.nocompany.iso.objects;

public enum DecalTypes{
        
    EMPTY(0, 0, "empty"),
    CLAY(1, 0, "empty"),
    FEN(2, 9, "fen"),
    PRAIRIE(3, 0, "empty"),
    LEAF(4, 0, "empty"),
    FOREST_PINE(5, 0, "empty"),
    SWAMP( 6, 9 , "swamp"),
    HEARTH(7, 4, "heath"),
    MOOR(8, 12, "moor"),
    SAND(9, 0, "empty"),
    WATER(10, 0, "empty"),
    WATER_DEEP(11, 0, "empty"),
    GRASS (12, 0, "empty"),
    WALD(13, 0, "wald");


    private int id;
    private int count;
    private String name;

    DecalTypes(int id, int count, String name){
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public String getName(){
        return name;
    }

    public int getDecalId() {
        return id;
    }

    public int getDecalCount(){
        return count;
    }

    public static DecalTypes getDecalById(int id){
        return DecalTypes.values()[id];
    }
}