package com.aizistral.enigmaticlegacy.registries;

import net.minecraft.item.Item;
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

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.EARTH_HEART)) {
            addToAll(EnigmaticItems.EARTH_HEART, 7);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.UNHOLY_GRAIL)) {
            addToAll(EnigmaticItems.UNHOLY_GRAIL, 1);
        }
    }

    private static void addToAll(Item item, int weight) {
        for (String chest : OVERWORLD_DUNGEON_CHESTS) {
            ChestGenHooks.addItem(chest, new WeightedRandomChestContent(new ItemStack(item), 1, 1, weight));
        }
    }

}
