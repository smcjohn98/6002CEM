package com.smc.crafthk.constraint;


import java.util.HashMap;
import java.util.Map;

public enum CraftType {
    TEXTILE_CRAFT(1),
    CERAMIC_CRAFT(2),
    WOODWORKING_CRAFT(3),
    PAPER_CRAFT(4),
    JEWELRY_CRAFT(5),
    FLORAL_CRAFT(6),
    GLASS_CRAFT(7);


    int id;
    public static Map<Integer, CraftType> idToType = new HashMap<>();

    static {
        for(CraftType ct : CraftType.values()){
            idToType.put(ct.id, ct);
        }
    }
    CraftType(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
}
