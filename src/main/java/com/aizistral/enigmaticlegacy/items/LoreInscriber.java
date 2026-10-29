package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.gui.EnigmaticGuiHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LoreInscriber extends ItemBase implements Vanishable {

    public LoreInscriber() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.epic)
                .stacksTo(1));
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber10");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreInscriber11");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

    }

    @Override
    public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getHeldItem();

        if (playerIn.isSneaking()) return InteractionResultHolder.pass(stack);

        if (!worldIn.isRemote) {
            playerIn.openGui(
                EnigmaticLegacy.instance,
                EnigmaticGuiHandler.LORE_INSCRIBER_GUI,
                worldIn,
                (int) playerIn.posX,
                (int) playerIn.posY,
                (int) playerIn.posZ);
        }

        return InteractionResultHolder.success(stack);
    }

}
