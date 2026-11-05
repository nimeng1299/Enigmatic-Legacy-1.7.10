package com.aizistral.enigmaticlegacy.registries;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.gen.structure.StructureNetherBridgePieces;
import net.minecraftforge.common.ChestGenHooks;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.items.RevelationTome;

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

    private static final String[] STRONGHOLD_CHESTS = { ChestGenHooks.STRONGHOLD_CORRIDOR,
        ChestGenHooks.STRONGHOLD_CROSSING };

    // End cities do not generate in 1.7.10; loot only appears if a structure backport
    // populates the matching category.
    private static final String[] END_CITY_CHESTS = { "endCityTreasure" };

    private static final EnigmaticLootModifiers INSTANCE = new EnigmaticLootModifiers();

    private EnigmaticLootModifiers() {
        // NO-OP
    }

    // ------------------------------------------------------------------
    // Tattered Tome as an independent loot pool
    // ------------------------------------------------------------------

    /*
     * 1.20 does not compete for weight here: it adds a separate LootPool whose rolls are drawn
     * from a range that is mostly negative, so the book appears *on top of* the vanilla contents
     * instead of replacing an entry. Rolling -7..2 over ten equally likely values means one roll
     * in ten yields one tome and one in ten yields two - a 20% chance overall; the library pool
     * uses -4..3 over eight values, i.e. 37.5% for one, two or three tomes.
     * ChestGenHooks cannot express that, since anything added there simply competes for the same
     * weighted slots. So the category is remembered when ChestGenHooks hands out its item array,
     * and the tomes are appended once the chest has been filled.
     */

    private static final String[] OVERWORLD_LITERATURE_CHESTS = { ChestGenHooks.DUNGEON_CHEST,
        ChestGenHooks.MINESHAFT_CORRIDOR, ChestGenHooks.STRONGHOLD_CORRIDOR, ChestGenHooks.STRONGHOLD_CROSSING,
        ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST };

    private static final String[] LIBRARY_LITERATURE_CHESTS = { ChestGenHooks.STRONGHOLD_LIBRARY };

    // Both are provided by structure backport mods only; 1.7.10 itself has no nether-fortress
    // ChestGenHooks category (its loot is a private array, see below) and no end cities.
    private static final String[] NETHER_LITERATURE_CHESTS = { "netherFortress" };

    private static final ThreadLocal<String> PENDING_CATEGORY = new ThreadLocal<>();
    private static final ThreadLocal<WeightedRandomChestContent[]> PENDING_ITEMS = new ThreadLocal<>();

    /**
     * Nether fortress chests use a private static content array instead of ChestGenHooks, so
     * their loot is matched by this array reference rather than by category name.
     */
    private static WeightedRandomChestContent[] netherFortressContents;

    /**
     * Called from {@code ChestGenHooks#getItems}. The returned array is what structure generation
     * passes straight into {@code WeightedRandomChestContent#generateChestContents}, so matching
     * on the array identity avoids mistaking chests that use private content arrays (bonus chest,
     * nether fortress) for chests of this category.
     */
    public static void onChestItemsRequested(String category, WeightedRandomChestContent[] items) {
        PENDING_CATEGORY.set(category);
        PENDING_ITEMS.set(items);
    }

    /**
     * Called once a chest has been populated, to append the tomes of the matching literature pool.
     */
    public static void onChestContentsGenerated(Random random, WeightedRandomChestContent[] items,
        IInventory inventory) {
        String category = PENDING_CATEGORY.get();
        WeightedRandomChestContent[] requested = PENDING_ITEMS.get();

        PENDING_CATEGORY.remove();
        PENDING_ITEMS.remove();

        boolean netherFortress = netherFortressContents != null && netherFortressContents == items;

        if (!netherFortress && (category == null || requested != items)) return;
        if (!OmniconfigHandler.customDungeonLootEnabled.getValue()) return;

        /*
         * Points and experience ranges mirror the 1.20 LootFunctionRevelation arguments; all three
         * pools there roll -7..2 except the library one, which rolls -4..3.
         */
        if (netherFortress) {
            appendLiterature(random, inventory, EnigmaticItems.WITHERED_TOME, 1, 3, 100, 700, -7, 10);
        } else if (contains(OVERWORLD_LITERATURE_CHESTS, category)) {
            appendLiterature(random, inventory, EnigmaticItems.TATTERED_TOME, 1, 3, 50, 500, -7, 10);
        } else if (contains(LIBRARY_LITERATURE_CHESTS, category)) {
            appendLiterature(random, inventory, EnigmaticItems.TATTERED_TOME, 1, 3, 50, 500, -4, 8);
        } else if (contains(NETHER_LITERATURE_CHESTS, category)) {
            appendLiterature(random, inventory, EnigmaticItems.WITHERED_TOME, 1, 3, 100, 700, -7, 10);
        } else if (contains(END_CITY_CHESTS, category)) {
            appendLiterature(random, inventory, EnigmaticItems.CORRUPTED_TOME, 1, 4, 200, 1000, -7, 10);
        }
    }

    private static void appendLiterature(Random random, IInventory inventory, RevelationTome tome, int minPoints,
        int maxPoints, int minXp, int maxXp, int rollStart, int rollSpan) {
        if (!OmniconfigHandler.isItemEnabled(tome)) return;

        int rolls = rollStart + random.nextInt(rollSpan);

        for (int i = 0; i < rolls; i++) {
            int slot = findRandomEmptySlot(random, inventory);

            if (slot < 0) break;

            inventory.setInventorySlotContents(
                slot,
                tome.createTome(
                    minPoints + random.nextInt(maxPoints - minPoints + 1),
                    minXp + random.nextInt(maxXp - minXp + 1)));
        }
    }

    private static boolean contains(String[] values, String candidate) {
        for (String value : values) {
            if (value.equals(candidate)) return true;
        }

        return false;
    }

    private static int findRandomEmptySlot(Random random, IInventory inventory) {
        int size = inventory.getSizeInventory();

        for (int attempt = 0; attempt < size; attempt++) {
            int slot = random.nextInt(size);

            if (inventory.getStackInSlot(slot) == null) return slot;
        }

        for (int slot = 0; slot < size; slot++) {
            if (inventory.getStackInSlot(slot) == null) return slot;
        }

        return -1;
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

        // 1.20 adds the Iron Ring to the "epic" overworld dungeon pool with weight 20.
        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.IRON_RING)) {
            addToAll(EnigmaticItems.IRON_RING, 20);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.UNHOLY_GRAIL)) {
            addToAll(EnigmaticItems.UNHOLY_GRAIL, 1);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.EYE_OF_NEBULA)) {
            addEyeOfNebulaToChests(5, STRONGHOLD_CHESTS);
            addEyeOfNebulaToChests(10, END_CITY_CHESTS);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.BLAZING_CORE)) {
            addToChests(EnigmaticItems.BLAZING_CORE, 5, NETHER_DUNGEON_CHESTS);
            injectNetherFortressLoot();
        }

        // Must run after injectNetherFortressLoot so the cached reference is the array the
        // structure will actually use.
        cacheNetherFortressContents();
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

    /**
     * Captures the array Nether Fortress chests are generated from, so the Withered Tome pool can
     * recognise those chests in {@code generateChestContents} even though they never go through
     * ChestGenHooks.
     */
    private static void cacheNetherFortressContents() {
        try {
            Class<?> pieceClass = StructureNetherBridgePieces.Corridor.class.getSuperclass();
            netherFortressContents = (WeightedRandomChestContent[]) cpw.mods.fml.common.ObfuscationReflectionHelper
                .getPrivateValue((Class) pieceClass, null, "field_111019_a");
        } catch (Exception ex) {
            EnigmaticLegacy.LOGGER.error("Failed to capture Nether Fortress loot contents.", ex);
        }
    }

    private static void addToAll(Item item, int weight) {
        addToChests(item, weight, OVERWORLD_DUNGEON_CHESTS);
    }

    /**
     * Mirrors the 90/10 Eye of Nebula to Void Pearl split of the end-city
     * spellstone pool on 1.20: every generated Eye has a 1/10 chance of being
     * replaced by a Void Pearl.
     */
    private static void addEyeOfNebulaToChests(int weight, String[] chests) {
        boolean replaceWithVoidPearl = OmniconfigHandler.isItemEnabled(EnigmaticItems.VOID_PEARL);

        for (String chest : chests) {
            WeightedRandomChestContent content = replaceWithVoidPearl
                ? new VoidPearlReplacementContent(new ItemStack(EnigmaticItems.EYE_OF_NEBULA), 1, 1, weight)
                : new WeightedRandomChestContent(new ItemStack(EnigmaticItems.EYE_OF_NEBULA), 1, 1, weight);
            ChestGenHooks.addItem(chest, content);
        }
    }

    private static class VoidPearlReplacementContent extends WeightedRandomChestContent {

        private VoidPearlReplacementContent(ItemStack stack, int min, int max, int weight) {
            super(stack, min, max, weight);
        }

        @Override
        protected ItemStack[] generateChestContent(Random random, IInventory inventory) {
            if (random.nextInt(10) == 0) {
                return new ItemStack[] { new ItemStack(EnigmaticItems.VOID_PEARL) };
            }

            return super.generateChestContent(random, inventory);
        }

    }

    private static void addToChests(Item item, int weight, String[] chests) {
        for (String chest : chests) {
            ChestGenHooks.addItem(chest, new WeightedRandomChestContent(new ItemStack(item), 1, 1, weight));
        }
    }

}
