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
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;

public class DeceptionAmulet extends ItemBaseCurio implements ICursed {

    public DeceptionAmulet() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.epic)
                .stacksTo(1)
                .fireResistant()
                .durability(2));
    }

    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet10");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.deceptionAmulet11");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(list);
    }

}
