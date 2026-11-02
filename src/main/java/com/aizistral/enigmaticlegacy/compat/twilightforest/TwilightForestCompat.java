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
        if (!OmniconfigHandler.isItemEnabled(EnigmaticItems.ETHERIUM_ORE)) return;

        // Rare tables weigh 20 (cache) and 40 (key), so these weights yield 60% and 90% per chest.
        TFTreasure.darktower_cache.rare.add(EnigmaticItems.ETHERIUM_ORE, 2, 30);
        TFTreasure.darktower_key.rare.add(EnigmaticItems.ETHERIUM_ORE, 2, 360);
        EnigmaticLegacy.LOGGER.info("Injected Etherium Ore into Twilight Forest Dark Tower loot.");
    }

    public static boolean isTowerGolem(EntityLivingBase entity) {
        return entity instanceof EntityTFTowerGolem;
    }

}
