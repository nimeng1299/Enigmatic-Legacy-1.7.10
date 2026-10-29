package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.item.Item.ToolMaterial;

public final class Tiers {

    public static final Tier WOOD = of(ToolMaterial.WOOD);
    public static final Tier STONE = of(ToolMaterial.STONE);
    public static final Tier IRON = of(ToolMaterial.IRON);
    public static final Tier DIAMOND = of(ToolMaterial.EMERALD);
    public static final Tier GOLD = of(ToolMaterial.GOLD);
    public static final Tier NETHERITE = of(ToolMaterial.EMERALD);

    private Tiers() {
        // NO-OP
    }

    public static Tier of(final ToolMaterial material) {
        return new Tier() {

            @Override
            public int getUses() {
                return material.getMaxUses();
            }

            @Override
            public float getSpeed() {
                return material.getEfficiencyOnProperMaterial();
            }

            @Override
            public float getAttackDamageBonus() {
                return material.getDamageVsEntity();
            }

            @Override
            public int getLevel() {
                return material.getHarvestLevel();
            }

            @Override
            public int getEnchantmentValue() {
                return material.getEnchantability();
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.EMPTY;
            }
        };
    }

}
