package com.nocompany.utils;

public class TilesUtil {
    
    public static short getFirstTileMode( int cell ){
        return (short) (cell >> 8 & 0x000000ff);
    }

    public static short getSecondTileMode( int cell ){
        return (short) (cell >> 0 & 0x000000ff);
    }

    public static short getFirstTileType( int cell ){
        return (short) (cell >> 28 & 0x0000000f);
    }

    public static short getSecondTileType( int cell ){
        return (short) (cell >> 24 & 0x0000000f);
    }

    public static short getThirdTileType( int cell ){
        return (short) (cell >> 16 & 0x0000000f);
    }

    public static int setCell( short firstType, short secondType, short thirdType, short firstMode, short secondMode ){

        byte firstByte = (byte)(firstType << 4 & 0xF0 | secondType & 0x0F);
        byte secondByte = (byte)(thirdType & 0x0F);
        byte thirdByte = (byte)firstMode;
        byte fourthByte = (byte)secondMode;

        // return (int)( fourthByte << 24 | thirdByte << 16 | secondByte << 8 | firstByte );
        return (int)( firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte );
    }

}
