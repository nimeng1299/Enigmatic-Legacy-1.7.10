package com.aizistral.enigmaticlegacy.registries;

import java.util.Arrays;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.gen.structure.StructureNetherBridgePieces;
import net.minecraftforge.common.ChestGenHooks;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
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

    // 1.7.10 has no underwater ruins, shipwrecks or buried treasure; these hooks only
    // yield loot once a structure backport mod populates/reads the same categories.
    private static final String[] WATER_DUNGEON_CHESTS = { "underwaterRuinBig", "underwaterRuinSmall",
        "shipwreckTreasure", "buriedTreasure" };

    // Ruined portals and bastion remnants do not exist in 1.7.10 either; same caveat applies.
    private static final String[] NETHER_DUNGEON_CHESTS = { "ruinedPortal", "bastionTreasure", "bastionOther",
        "bastionBridge", "bastionHoglinStable" };

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

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.OCEAN_STONE)) {
            addToChests(EnigmaticItems.OCEAN_STONE, 10, WATER_DUNGEON_CHESTS);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.EARTH_HEART)) {
            addToAll(EnigmaticItems.EARTH_HEART, 7);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.UNHOLY_GRAIL)) {
            addToAll(EnigmaticItems.UNHOLY_GRAIL, 1);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.BLAZING_CORE)) {
            addToChests(EnigmaticItems.BLAZING_CORE, 5, NETHER_DUNGEON_CHESTS);
            injectNetherFortressLoot();
        }
    }

    /**
     * Nether fortress chests use a private static content array instead of ChestGenHooks,
     * so the only way to add to them on 1.7.10 is to replace that array.
     */
    private static void injectNetherFortressLoot() {
        try {
            Class<?> pieceClass = StructureNetherBridgePieces.Corridor.class.getSuperclass();
            WeightedRandomChestContent[] contents = (WeightedRandomChestContent[]) cpw.mods.fml.common.ObfuscationReflectionHelper
                .getPrivateValue((Class) pieceClass, null, "field_111019_a");
            WeightedRandomChestContent[] extended = Arrays.copyOf(contents, contents.length + 1);
            extended[contents.length] = new WeightedRandomChestContent(
                new ItemStack(EnigmaticItems.BLAZING_CORE),
                1,
                1,
                5);

            cpw.mods.fml.common.ObfuscationReflectionHelper
                .setPrivateValue((Class) pieceClass, null, extended, "field_111019_a");
            EnigmaticLegacy.LOGGER.info("Injected Blazing Core into Nether Fortress loot.");
        } catch (Exception ex) {
            EnigmaticLegacy.LOGGER.error("Failed to inject Blazing Core into Nether Fortress loot.", ex);
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
