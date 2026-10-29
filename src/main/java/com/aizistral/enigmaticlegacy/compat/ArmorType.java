package com.aizistral.enigmaticlegacy.compat;

/**
 * 1.7.10 uses plain ints for armor slots; this mirrors the modern ArmorItem.Type.
 */
public final class ArmorType {

    public static final int HELMET = 0;
    public static final int CHESTPLATE = 1;
    public static final int LEGGINGS = 2;
    public static final int BOOTS = 3;

    private ArmorType() {
        // NO-OP
    }

    public static int fromName(String name) {
        switch (name) {
            case "helmet":
                return HELMET;
            case "chestplate":
                return CHESTPLATE;
            case "leggings":
                return LEGGINGS;
            case "boots":
                return BOOTS;
            default:
                return HELMET;
        }
    }

}
