package com.aizistral.enigmaticlegacy.compat.villagenames;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;

import astrotibs.villagenames.config.GeneralConfig;
import astrotibs.villagenames.item.ModItems;
import astrotibs.villagenames.prismarine.guardian.entity.monster.EntityGuardian;

public final class VillageNamesCompat {

    private VillageNamesCompat() {
        // NO-OP
    }

    public static boolean isGuardian(EntityLivingBase entity) {
        return entity instanceof EntityGuardian;
    }

    public static boolean isElderGuardian(EntityLivingBase entity) {
        return entity instanceof EntityGuardian && ((EntityGuardian) entity).isElder();
    }

    public static Item prismarineShard() {
        return GeneralConfig.addPrismarine ? ModItems.prismarine_shard : null;
    }

    public static Item prismarineCrystals() {
        return GeneralConfig.addPrismarine ? ModItems.prismarine_crystals : null;
    }
}
