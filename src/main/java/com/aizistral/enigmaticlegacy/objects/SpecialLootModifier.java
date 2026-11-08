package com.aizistral.enigmaticlegacy.objects;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.quack.ILootChest;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.ImmutableList;

/**
 * Port of the 1.20 global loot modifier of the same name. 1.7.10 has no global loot modifier
 * system and rolls chest contents at world generation time, so the per-player one-time rewards are
 * applied when the player opens the chest instead (see {@link #onChestOpened}); the archaeology
 * branch is not applicable and only keeps its configuration entry.
 */
public class SpecialLootModifier {

    /**
     * 1.20 fills chests when they are opened, so a loot modifier can hand out a per-player
     * one-time reward there. 1.7.10 rolls chest contents during world generation instead, which
     * is long before any player is involved - see {@link #onChestOpened} for the workaround.
     */
    private static final String ARCHITECT_EYE_TAG = "LootedArchitectEye";
    private static final String ASTRAL_FRUIT_TAG = "LootedFirstEndCityChest";
    private static final String ICHOR_BOTTLE_TAG = "LootedIchorBottle";

    private static final int END_DIMENSION_ID = 1;

    public static final List<ResourceLocation> SUSPICIOUS_TABLES = ImmutableList.of(
        new ResourceLocation("minecraft", "archaeology/desert_pyramid"),
        new ResourceLocation("minecraft", "archaeology/desert_well"),
        new ResourceLocation("minecraft", "archaeology/ocean_ruin_cold"),
        new ResourceLocation("minecraft", "archaeology/ocean_ruin_warm"),
        new ResourceLocation("minecraft", "archaeology/trail_ruins_common"),
        new ResourceLocation("minecraft", "archaeology/trail_ruins_rare"));

    public static Omniconfig.DoubleParameter earthHeartChance;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("CustomLoot");

        if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
            earthHeartChance = builder
                .comment("The chance for Heart of the Earth to be obtained from suspicious blocks (default is 3%).")
                .getDouble("EarthHeartChance", 0.03);
        }

        builder.popPrefix();
    }

    /**
     * Entry point of every per-player one-time chest reward; called from {@code MixinEntityPlayerMP}
     * right before the chest GUI is created, i.e. before the player is able to take anything out.
     * <p>
     * 1.7.10 generates no end cities and has no loot tables, so the 1.20 conditions based on loot
     * table ids are approximated: "is this a loot chest" is answered by the flag {@link #markLootChest}
     * puts on structure chests during world generation, and "end city treasure" degrades to "any chest
     * block in the End". Ender chests are not a {@link TileEntityChest}, so they never count.
     */
    public static void onChestOpened(EntityPlayerMP player, IInventory inventory) {
        if (!(inventory instanceof TileEntityChest)) return;
        if (player.worldObj == null) return;

        SpecialLootModifier.grantArchitectEye(player, inventory);
        SpecialLootModifier.grantAstralFruit(player, inventory);
        SpecialLootModifier.handleIchorBottle(player, inventory);
    }

    /**
     * Flags a chest that is being filled from a structure loot pool, so the one-time rewards above
     * can recognise it once a player opens it. Called from {@code MixinWeightedRandomChestContent}
     * for every inventory vanilla loot generation fills; only chests implement {@link ILootChest},
     * so dispensers, minecarts and modded containers are left alone.
     */
    public static void markLootChest(IInventory inventory) {
        if (inventory instanceof ILootChest) {
            ((ILootChest) inventory).setEnigmaticLootChest(true);
        }
    }

    /**
     * Mirrors the Architect Eye branch of the 1.20 loot modifier: the first loot chest a player opens
     * contains one dormant Eye, once per player. A freshly created stack is dormant by default (see
     * {@code EnigmaticEye#isDormant}), so the player has to activate it themselves.
     * <p>
     * The tag is only consumed once the Eye has actually been placed, so a chest that happens to be
     * full does not waste the reward - the next loot chest gets it instead.
     */
    private static void grantArchitectEye(EntityPlayerMP player, IInventory inventory) {
        if (!OmniconfigHandler.isItemEnabled(EnigmaticItems.ENIGMATIC_EYE)) return;
        if (SuperpositionHandler.hasPersistentTag(player, ARCHITECT_EYE_TAG)) return;
        if (!(inventory instanceof ILootChest) || !((ILootChest) inventory).isEnigmaticLootChest()) return;

        ItemStack eye = new ItemStack(EnigmaticItems.ENIGMATIC_EYE);

        if (!SpecialLootModifier.insertStack(inventory, player.worldObj.rand, eye)) return;

        SuperpositionHandler.setPersistentBoolean(player, ARCHITECT_EYE_TAG, true);
    }

    /**
     * Mirrors the Celestial Fruit branch of the 1.20 loot modifier: the first chest a bearer of the
     * Seven Curses opens in the End contains the fruit.
     */
    private static void grantAstralFruit(EntityPlayerMP player, IInventory inventory) {
        if (player.worldObj.provider.dimensionId != END_DIMENSION_ID) return;
        if (!OmniconfigHandler.isItemEnabled(EnigmaticItems.ASTRAL_FRUIT)) return;
        if (SuperpositionHandler.hasPersistentTag(player, ASTRAL_FRUIT_TAG)) return;
        if (!SuperpositionHandler.isTheCursedOne(player)) return;

        ItemStack fruit = new ItemStack(EnigmaticItems.ASTRAL_FRUIT);

        if (!SpecialLootModifier.insertStack(inventory, player.worldObj.rand, fruit)) return;

        SuperpositionHandler.setPersistentBoolean(player, ASTRAL_FRUIT_TAG, true);
    }

    /**
     * Mirrors the Ichor Bottle branch of the 1.20 loot modifier, where a player may only ever get
     * one bottle: 1.20 filters the loot as it is generated for the player opening the container.
     * 1.7.10 fills chests during world generation, so the equivalent is to strip the bottle from
     * the shared chest when an already-looted player opens it. That ends up the same way, since on
     * 1.20 the contents are generated once, for whoever opens the chest first, and then locked in.
     * <p>
     * Only structure chests flagged by {@link #markLootChest} are touched, so a bottle a player
     * stashes in their own chest is never consumed or removed.
     */
    private static void handleIchorBottle(EntityPlayerMP player, IInventory inventory) {
        if (!OmniconfigHandler.isItemEnabled(EnigmaticItems.ICHOR_BOTTLE)) return;
        if (!(inventory instanceof ILootChest) || !((ILootChest) inventory).isEnigmaticLootChest()) return;

        int slot = SpecialLootModifier.findIchorBottleSlot(inventory);

        if (slot < 0) return;

        if (SuperpositionHandler.hasPersistentTag(player, ICHOR_BOTTLE_TAG)) {
            inventory.setInventorySlotContents(slot, null);
            inventory.markDirty();
        } else {
            SuperpositionHandler.setPersistentBoolean(player, ICHOR_BOTTLE_TAG, true);
        }
    }

    private static int findIchorBottleSlot(IInventory inventory) {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);

            if (stack != null && stack.getItem() == EnigmaticItems.ICHOR_BOTTLE) return slot;
        }

        return -1;
    }

    private static boolean insertStack(IInventory inventory, Random random, ItemStack stack) {
        int size = inventory.getSizeInventory();
        int start = size > 0 ? random.nextInt(size) : 0;

        for (int attempt = 0; attempt < size; attempt++) {
            int slot = (start + attempt) % size;

            if (inventory.getStackInSlot(slot) == null) {
                inventory.setInventorySlotContents(slot, stack);
                inventory.markDirty();
                return true;
            }
        }

        return false;
    }

}
