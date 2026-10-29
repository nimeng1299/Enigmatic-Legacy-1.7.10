package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.IEldritch;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AbyssalHeart extends ItemBase implements IEldritch {

    public AbyssalHeart() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.epic)
                .stacksTo(1));
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.indicateWorthyOnesOnly(list);
        } else {
            // if (SuperpositionHandler.isTheWorthyOne(Minecraft.getMinecraft().thePlayer)) {
            list.add(Component.translatable("tooltip.enigmaticlegacy.abyssalHeart1"));
            list.add(Component.translatable("tooltip.enigmaticlegacy.abyssalHeart2"));
            list.add(Component.translatable("tooltip.enigmaticlegacy.abyssalHeart3"));
            list.add(Component.translatable("tooltip.enigmaticlegacy.abyssalHeart4"));
            // } else {
            // list.add(Component.translatable("tooltip.enigmaticlegacy.abyssalHeart1_obf"));
            // list.add(Component.translatable("tooltip.enigmaticlegacy.abyssalHeart2_obf"));
            // list.add(Component.translatable("tooltip.enigmaticlegacy.abyssalHeart3_obf"));
            // list.add(Component.translatable("tooltip.enigmaticlegacy.abyssalHeart4_obf"));
            // }

            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.indicateCursedOnesOnly(list);
        }
    }

}
