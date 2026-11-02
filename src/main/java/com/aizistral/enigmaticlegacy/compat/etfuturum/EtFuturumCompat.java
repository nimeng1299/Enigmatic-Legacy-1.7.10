package com.aizistral.enigmaticlegacy.compat.etfuturum;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;

import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.entities.EntityHusk;
import ganymedes01.etfuturum.entities.EntityShulker;

public final class EtFuturumCompat {

    private EtFuturumCompat() {
        // NO-OP
    }

    public static boolean isHusk(EntityLivingBase entity) {
        return entity instanceof EntityHusk;
    }

    public static boolean isShulker(EntityLivingBase entity) {
        return entity instanceof EntityShulker;
    }

    public static Item netheriteScrap() {
        return ModItems.NETHERITE_SCRAP.isEnabled() ? ModItems.NETHERITE_SCRAP.get() : null;
    }

    public static Item prismarineShard() {
        return ModItems.PRISMARINE_SHARD.isEnabled() ? ModItems.PRISMARINE_SHARD.get() : null;
    }

    public static Item prismarineCrystals() {
        return ModItems.PRISMARINE_CRYSTALS.isEnabled() ? ModItems.PRISMARINE_CRYSTALS.get() : null;
    }
}
