package com.nocompany.iso.objects;
import com.nocompany.iso.Settings;
import java.util.Random;

public enum GameObjectTypes {

    EMPTY(0),
    ATREE(1),
    BIRCH(2),
    ELM(3),
    FIR(4),
    OAK(5),
    PINE(6),
    WILLOW(7),
    YEW(8),
    STONE(9);

    private static final Random random = new Random(Settings.SEED);

    public static GameObjectTypes getRandomType(){
        return values()[random.nextInt(values().length - 1 - 1) + 1];
    }

    private int objId;

    GameObjectTypes(int objId){
        this.objId = objId;
    }

    public String getName(){
        return this.name();
    }

    public int getObjectId() {
        return objId;
    }

}
