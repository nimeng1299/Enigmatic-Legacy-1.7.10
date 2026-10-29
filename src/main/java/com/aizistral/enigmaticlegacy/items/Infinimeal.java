package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Infinimeal extends ItemBase implements Vanishable {

    public Infinimeal() {
        super(
            getDefaultProperties().stacksTo(1)
                .rarity(EnumRarity.uncommon));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.infinimeal1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.infinimeal2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.infinimeal3");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        World level = context.getLevel();
        int x = context.getClickedX();
        int y = context.getClickedY();
        int z = context.getClickedZ();

        ItemStack bonemeal = new ItemStack(Items.dye, 1, 15);
        boolean success = ItemDye.applyBonemeal(bonemeal, level, x, y, z, context.getPlayer());

        if (success) {
            if (!level.isRemote) {
                level.playAuxSFX(2005, x, y, z, 0);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

}
