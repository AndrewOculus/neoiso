package com.nocompany;

import java.util.HashMap;

public class TileVariants {

    public HashMap<Short, Short[]> variants;

    public TileVariants(){
        variants = new HashMap<Short, Short[]>();
    }

    public Short[] getVariants(Short tile){
        return variants.get(tile);
    }
    
}
