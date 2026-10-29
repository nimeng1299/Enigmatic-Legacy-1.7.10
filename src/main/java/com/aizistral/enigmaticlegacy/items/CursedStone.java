package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

public class CursedStone extends ItemBase implements ICursed {

    public CursedStone() {
        super(
            getDefaultProperties().rarity(EnumRarity.epic)
                .fireResistant()
                .stacksTo(1));
    }

    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedStone1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedStone2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedStone3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedStone4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedStone5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedStone6");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(list);
    }

}
