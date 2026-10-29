package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CosmicHeart extends ItemBase implements Vanishable {

    public static BiConsumer<ItemStack, EntityPlayer> blessableHandler = (stack, player) -> {};
    public static BiConsumer<ItemStack, List<Component>> tooltipHandler = (stack, tooltip) -> {};

    public CosmicHeart() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.epic)
                .stacksTo(1));
    }

    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayer && !entityIn.worldObj.isRemote) {
            EntityPlayer player = (EntityPlayer) entityIn;

            if (SuperpositionHandler.isTheBlessedOne(player)) {
                if (!ItemNBTHelper.getBoolean(stack, "isBelieverBlessed", false)) {
                    ItemNBTHelper.setBoolean(stack, "isBelieverBlessed", true);
                }
            } else {
                if (ItemNBTHelper.getBoolean(stack, "isBelieverBlessed", false)) {
                    ItemNBTHelper.setBoolean(stack, "isBelieverBlessed", false);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (ItemNBTHelper.getBoolean(stack, "isBelieverBlessed", false)) {
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.blessed1");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.blessed2");
        }
    }

}
