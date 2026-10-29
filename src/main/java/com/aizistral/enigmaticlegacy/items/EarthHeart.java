package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ITaintable;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

public class EarthHeart extends ItemBase implements ITaintable, Vanishable {

    public EarthHeart() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.uncommon)
                .stacksTo(1));
    }

    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayer && !entityIn.worldObj.isRemote) {
            EntityPlayer player = (EntityPlayer) entityIn;
            this.handleTaintable(stack, player);
        }
    }

    public void appendHoverText(ItemStack stack, World worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (this.isTainted(stack)) {
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.tainted1");
        }
    }

}
