package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LootGenerator extends ItemBase implements Vanishable {

    public Random lootRandomizer = new Random();
    public List<String> lootList = new ArrayList<String>();

    public LootGenerator() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.epic)
                .stacksTo(1));

        this.lootList.add(ChestGenHooks.DUNGEON_CHEST);
        this.lootList.add(ChestGenHooks.MINESHAFT_CORRIDOR);
        this.lootList.add(ChestGenHooks.PYRAMID_DESERT_CHEST);
        this.lootList.add(ChestGenHooks.PYRAMID_JUNGLE_CHEST);
        this.lootList.add(ChestGenHooks.PYRAMID_JUNGLE_DISPENSER);
        this.lootList.add(ChestGenHooks.STRONGHOLD_CORRIDOR);
        this.lootList.add(ChestGenHooks.STRONGHOLD_LIBRARY);
        this.lootList.add(ChestGenHooks.STRONGHOLD_CROSSING);
        this.lootList.add(ChestGenHooks.VILLAGE_BLACKSMITH);
        this.lootList.add(ChestGenHooks.BONUS_CHEST);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.lootGenerator1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.lootGenerator2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.lootGenerator3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.lootGenerator4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.lootGenerator5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.lootGenerator6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.lootGenerator7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.lootGenerator8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.lootGenerator9");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.lootGeneratorCurrent");

        int index = Math.min(stack.getItemDamage(), this.lootList.size() - 1);
        list.add(
            Component.literal("" + this.lootList.get(index))
                .withStyle(EnumChatFormatting.GOLD));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack itemstack = player.getHeldItem();

        if (!player.isSneaking()) {
            if (itemstack.getItemDamage() < this.lootList.size() - 1) {
                itemstack.setItemDamage(itemstack.getItemDamage() + 1);
            } else {
                itemstack.setItemDamage(0);
            }

            player.swingItem();

        } else {
            if (itemstack.getItemDamage() > 0) {
                itemstack.setItemDamage(itemstack.getItemDamage() - 1);
            } else {
                itemstack.setItemDamage(this.lootList.size() - 1);
            }
            player.swingItem();
        }

        if (player instanceof EntityPlayerMP) {
            player.addChatMessage(new ChatComponentText("Table: " + this.lootList.get(itemstack.getItemDamage())));
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        EntityPlayer player = context.getPlayer();
        World world = context.getLevel();
        ItemStack stack = context.getHeldItem();

        if (world.isRemote) return InteractionResult.SUCCESS;

        TileEntity tile = world.getTileEntity(
            context.getClickedPos()
                .getX(),
            context.getClickedPos()
                .getY(),
            context.getClickedPos()
                .getZ());

        if (tile instanceof TileEntityChest && player.isSneaking()) {
            TileEntityChest chest = (TileEntityChest) tile;
            int dir = 1 - Math.min(context.getClickedFace(), 1);
            String table = this.lootList.get(Math.min(stack.getItemDamage(), this.lootList.size() - 1));

            if (dir == 0) {
                fillChest(chest, table);
            } else if (dir == 1 && !SuperpositionHandler.hasSpellstoneCooldown(player)) {
                SuperpositionHandler.setSpellstoneCooldown(player, 40);
                HashMap<Item, Integer> lootMap = new HashMap<Item, Integer>();

                for (int counter = 0; counter < 32768; counter++) {
                    fillChest(chest, table);

                    for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
                        ItemStack generatedStack = chest.getStackInSlot(slot);

                        if (generatedStack != null && generatedStack.stackSize > 0) {
                            Item generatedItem = generatedStack.getItem();
                            int amount = generatedStack.stackSize;

                            if (lootMap.containsKey(generatedItem)) {
                                lootMap.put(generatedItem, lootMap.get(generatedItem) + amount);
                            } else {
                                lootMap.put(generatedItem, amount);
                            }
                        }
                    }

                    for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
                        chest.setInventorySlotContents(slot, null);
                    }
                }

                EnigmaticLegacy.LOGGER.info("Estimated generation complete in 32768 instances, results:");
                for (Item theItem : lootMap.keySet()) {
                    EnigmaticLegacy.LOGGER.info(
                        "Item: " + theItem.getItemStackDisplayName(new ItemStack(theItem))
                            + ", Amount: "
                            + lootMap.get(theItem));
                }

                player.addChatMessage(
                    new ChatComponentText(
                        Component.translatable("message.enigmaticlegacy.gen_sim_complete")
                            .withStyle(EnumChatFormatting.DARK_PURPLE)
                            .getFormattedText()));
            } else {
                for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
                    chest.setInventorySlotContents(slot, null);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private void fillChest(TileEntityChest chest, String table) {
        for (int slot = 0; slot < chest.getSizeInventory(); slot++) {
            chest.setInventorySlotContents(slot, null);
        }

        net.minecraft.util.WeightedRandomChestContent.generateChestContents(
            this.lootRandomizer,
            ChestGenHooks.getItems(table, this.lootRandomizer),
            chest,
            3 + this.lootRandomizer.nextInt(6));
    }

}
