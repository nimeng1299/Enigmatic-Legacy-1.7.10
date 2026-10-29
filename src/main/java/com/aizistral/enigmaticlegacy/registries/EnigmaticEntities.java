package com.aizistral.enigmaticlegacy.registries;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.entities.EnigmaticPotionEntity;
import com.aizistral.enigmaticlegacy.entities.PermanentItemEntity;
import com.aizistral.enigmaticlegacy.entities.UltimateWitherSkullEntity;

import cpw.mods.fml.common.registry.EntityRegistry;

/**
 * 1.7.10 entity registration is class based instead of an EntityType registry.
 */
public class EnigmaticEntities {

    private static final EnigmaticEntities INSTANCE = new EnigmaticEntities();

    private EnigmaticEntities() {
        EntityRegistry.registerModEntity(
            PermanentItemEntity.class,
            "permanent_item_entity",
            0,
            EnigmaticLegacy.instance,
            64,
            2,
            false);
        EntityRegistry.registerModEntity(
            EnigmaticPotionEntity.class,
            "enigmatic_potion_entity",
            1,
            EnigmaticLegacy.instance,
            64,
            10,
            true);
        EntityRegistry.registerModEntity(
            UltimateWitherSkullEntity.class,
            "ultimate_wither_skull_entity",
            2,
            EnigmaticLegacy.instance,
            64,
            1,
            true);
    }

    public static void init() {
        // Forces static initialization.
    }

}
