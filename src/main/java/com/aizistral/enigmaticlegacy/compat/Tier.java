package com.aizistral.enigmaticlegacy.compat;

/**
 * Backport of the modern tool material interface.
 */
public interface Tier {

    int getUses();

    float getSpeed();

    float getAttackDamageBonus();

    int getLevel();

    int getEnchantmentValue();

    Ingredient getRepairIngredient();

}
