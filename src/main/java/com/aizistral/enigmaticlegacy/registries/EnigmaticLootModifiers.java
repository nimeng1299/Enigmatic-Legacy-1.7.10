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

    private static final String[] GOLEM_HEART_COMMON_CHESTS = { ChestGenHooks.DUNGEON_CHEST,
        ChestGenHooks.MINESHAFT_CORRIDOR };

    private static final String[] PYRAMID_CHESTS = { ChestGenHooks.PYRAMID_DESERT_CHEST,
        ChestGenHooks.PYRAMID_JUNGLE_CHEST };

    private static final EnigmaticLootModifiers INSTANCE = new EnigmaticLootModifiers();

    private EnigmaticLootModifiers() {
        // NO-OP
    }

    public static void register() {
        if (!OmniconfigHandler.customDungeonLootEnabled.getValue()) return;

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.GOLEM_HEART)) {
            addToChests(EnigmaticItems.GOLEM_HEART, 6, GOLEM_HEART_COMMON_CHESTS);
            addToChests(EnigmaticItems.GOLEM_HEART, 3, PYRAMID_CHESTS);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.ANGEL_BLESSING)) {
            addToChests(EnigmaticItems.ANGEL_BLESSING, 5, PYRAMID_CHESTS);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.EARTH_HEART)) {
            addToAll(EnigmaticItems.EARTH_HEART, 7);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.UNHOLY_GRAIL)) {
            addToAll(EnigmaticItems.UNHOLY_GRAIL, 1);
        }
    }

    private static void addToAll(Item item, int weight) {
        addToChests(item, weight, OVERWORLD_DUNGEON_CHESTS);
    }

    private static void addToChests(Item item, int weight, String[] chests) {
        for (String chest : chests) {
            ChestGenHooks.addItem(chest, new WeightedRandomChestContent(new ItemStack(item), 1, 1, weight));
        }
    }

}
