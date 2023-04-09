package com.nocompany;

public enum Cell {
    EMPTY((byte) 0, (byte) 0, (byte)0, (byte)0),
    CLAY((byte)1, (byte) 60, (byte)121, (byte)162),
    FEN((byte)2, (byte) 75, (byte)127, (byte)55),
    PRAIRIE((byte)3, (byte) 125, (byte)127, (byte)55),
    LEAF((byte)4, (byte) 75, (byte)127, (byte)55),
    FOREST_PINE((byte)5, (byte) 75, (byte)167, (byte)55),
    SWAMP((byte) 6, (byte) 255, (byte)255, (byte)255),
    HEARTH((byte) 7, (byte) 75, (byte)107, (byte)55),
    MOOR((byte) 8, (byte) 125, (byte)70, (byte)55),
    SAND((byte) 9, (byte) 184, (byte)252, (byte)245),
    WATER((byte) 10, (byte) 255, (byte)0, (byte)0),
    WATER_DEEP((byte) 11, (byte) 170, (byte)30, (byte)0);


    private byte tileId;
    private byte r = (byte) 255;
    private byte g = (byte) 255;
    private byte b = (byte) 255;

    Cell(byte tileId){
        this.tileId = tileId;
    }
    Cell(byte tileId, byte r, byte g, byte b){
        this.tileId = tileId;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public byte getTileId() {
        return tileId;
    }

    public byte getR(){return r;}
    public byte getG(){return g;}
    public byte getB(){return b;}

    public static Cell getCellById(byte id){
        return Cell.values()[id];
    }
}
