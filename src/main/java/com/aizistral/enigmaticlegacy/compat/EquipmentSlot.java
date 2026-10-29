package com.aizistral.enigmaticlegacy.compat;

public enum EquipmentSlot {

    HEAD(0),
    CHEST(1),
    LEGS(2),
    FEET(3),
    MAINHAND(0),
    OFFHAND(0);

    private final int armorIndex;

    private EquipmentSlot(int armorIndex) {
        this.armorIndex = armorIndex;
    }

    public int getArmorIndex() {
        return this.armorIndex;
    }

    public boolean isArmorSlot() {
        return this.ordinal() <= FEET.ordinal();
    }

}
