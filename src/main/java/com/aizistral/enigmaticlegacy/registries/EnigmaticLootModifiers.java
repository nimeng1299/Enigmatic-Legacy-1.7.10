package com.aizistral.enigmaticlegacy.registries;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

/**
 * Global loot modifiers do not exist on 1.7.10; loot is injected through
 * {@code ChestGenHooks} instead.
 */
public class EnigmaticLootModifiers {

    private static final String[] OVERWORLD_DUNGEON_CHESTS = { ChestGenHooks.DUNGEON_CHEST,
        ChestGenHooks.MINESHAFT_CORRIDOR, ChestGenHooks.STRONGHOLD_CORRIDOR, ChestGenHooks.STRONGHOLD_CROSSING,
        ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST };

    private static final EnigmaticLootModifiers INSTANCE = new EnigmaticLootModifiers();

    private EnigmaticLootModifiers() {
        // NO-OP
    }

    public static void register() {
        if (!OmniconfigHandler.customDungeonLootEnabled.getValue()) return;
        if (!OmniconfigHandler.isItemEnabled(EnigmaticItems.EARTH_HEART)) return;

        for (String chest : OVERWORLD_DUNGEON_CHESTS) {
            ChestGenHooks
                .addItem(chest, new WeightedRandomChestContent(new ItemStack(EnigmaticItems.EARTH_HEART), 1, 1, 7));
        }
    }

}
