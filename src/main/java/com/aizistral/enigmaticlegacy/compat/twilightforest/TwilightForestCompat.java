package com.aizistral.enigmaticlegacy.compat.twilightforest;

import net.minecraft.entity.EntityLivingBase;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import twilightforest.TFTreasure;
import twilightforest.entity.EntityTFTowerGolem;

/**
 * Kept separate so Twilight Forest classes are only loaded when the mod is present.
 */
public final class TwilightForestCompat {

    private TwilightForestCompat() {
        // NO-OP
    }

    public static void registerLoot() {
        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.ETHERIUM_ORE)) {
            // Rare tables weigh 20 (cache) and 40 (key), so these weights yield 60% and 90% per chest.
            TFTreasure.darktower_cache.rare.add(EnigmaticItems.ETHERIUM_ORE, 2, 30);
            TFTreasure.darktower_key.rare.add(EnigmaticItems.ETHERIUM_ORE, 2, 360);
            EnigmaticLegacy.LOGGER.info("Injected Etherium Ore into Twilight Forest Dark Tower loot.");
        }

        if (OmniconfigHandler.customDungeonLootEnabled.getValue()
            && OmniconfigHandler.isItemEnabled(EnigmaticItems.FORBIDDEN_FRUIT)) {
            // Every Lich Tower chest rolls exactly one rare item; weight 10 against the existing rare
            // totals of 80 (room) and 90 (library) yields ~10% per chest.
            TFTreasure.tower_room.rare.add(EnigmaticItems.FORBIDDEN_FRUIT, 1, 10);
            TFTreasure.tower_library.rare.add(EnigmaticItems.FORBIDDEN_FRUIT, 1, 10);
            EnigmaticLegacy.LOGGER.info("Injected The Forbidden Fruit into Twilight Forest Lich Tower loot.");
        }

        if (OmniconfigHandler.customDungeonLootEnabled.getValue()
            && OmniconfigHandler.isItemEnabled(EnigmaticItems.OCEAN_STONE)) {
            // Aurora room chests roll exactly one rare item (existing total 80), cache chests one
            // out of an existing total of 11; this yields ~11% and ~8% per chest respectively.
            TFTreasure.aurora_room.rare.add(EnigmaticItems.OCEAN_STONE, 1, 10);
            TFTreasure.aurora_cache.rare.add(EnigmaticItems.OCEAN_STONE, 1, 1);
            EnigmaticLegacy.LOGGER.info("Injected Will of the Ocean into Twilight Forest Aurora Palace loot.");
        }
    }

    public static boolean isTowerGolem(EntityLivingBase entity) {
        return entity instanceof EntityTFTowerGolem;
    }

}
