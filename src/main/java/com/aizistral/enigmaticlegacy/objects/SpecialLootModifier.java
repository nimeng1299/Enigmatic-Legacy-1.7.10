package com.aizistral.enigmaticlegacy.objects;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.ImmutableList;

/**
 * Loot modifier stand-in; 1.7.10 has no global loot modifier system, so this
 * only retains the configuration entries used elsewhere.
 */
public class SpecialLootModifier {

    /**
     * 1.20 fills chests when they are opened, so a loot modifier can hand out a per-player
     * one-time reward there. 1.7.10 rolls chest contents during world generation instead, which
     * is long before any player is involved - see {@link #onChestOpened} for the workaround.
     */
    private static final String ASTRAL_FRUIT_TAG = "LootedFirstEndCityChest";

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
     * Mirrors the Celestial Fruit branch of the 1.20 loot modifier: the first chest a bearer of the
     * Seven Curses opens in the End contains the fruit. Called from {@code MixinEntityPlayerMP}
     * right before the chest GUI is created, i.e. before the player is able to take anything out.
     * <p>
     * 1.7.10 generates no end cities, so the check is limited to "any chest block in the End"
     * rather than the {@code chests/end_city_treasure} loot table, and ender chests (which are not
     * a {@link TileEntityChest}) never count. Unlike 1.20 the one-time tag is only consumed once
     * the fruit has actually been placed, so opening a chest without the ring equipped does not
     * waste the reward.
     */
    public static void onChestOpened(EntityPlayerMP player, IInventory inventory) {
        if (!(inventory instanceof TileEntityChest)) return;
        if (player.worldObj == null || player.worldObj.provider.dimensionId != END_DIMENSION_ID) return;
        if (!OmniconfigHandler.isItemEnabled(EnigmaticItems.ASTRAL_FRUIT)) return;
        if (SuperpositionHandler.hasPersistentTag(player, ASTRAL_FRUIT_TAG)) return;
        if (!SuperpositionHandler.isTheCursedOne(player)) return;

        ItemStack fruit = new ItemStack(EnigmaticItems.ASTRAL_FRUIT);

        if (!SpecialLootModifier.insertStack(inventory, player.worldObj.rand, fruit)) return;

        SuperpositionHandler.setPersistentBoolean(player, ASTRAL_FRUIT_TAG, true);
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
