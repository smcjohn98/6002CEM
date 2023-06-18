package com.smc.crafthk.constraint;


import java.util.HashMap;
import java.util.Map;

public enum CraftType {
    TEXTILE_CRAFT(1, "Textile Craft"),
    CERAMIC_CRAFT(2, "Ceramic Craft"),
    WOODWORKING_CRAFT(3, "Woodworking Craft"),
    PAPER_CRAFT(4, "Paper Craft"),
    JEWELRY_CRAFT(5, "Jewelry Craft"),
    FLORAL_CRAFT(6, "Floral Craft"),
    GLASS_CRAFT(7, "Glass Craft");


    int id;
    String name;
    public static Map<Integer, CraftType> idToType = new HashMap<>();

    public static Map<String, Integer> nameToId = new HashMap<>();

    static {
        for(CraftType ct : CraftType.values()){
            idToType.put(ct.id, ct);
            nameToId.put(ct.getName(), ct.getId());
        }
    }
    CraftType(int id, String name){
        this.id = id;
        this.name = name;
    }
    public int getId(){
        return this.id;
    }
    public String getName() { return this.name; }
}
