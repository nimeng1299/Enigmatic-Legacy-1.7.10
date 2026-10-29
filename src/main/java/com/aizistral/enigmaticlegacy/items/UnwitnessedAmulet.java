package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class UnwitnessedAmulet extends ItemBaseCurio {

    public UnwitnessedAmulet() {
        super(
            ItemBaseCurio.getDefaultProperties()
                .rarity(EnumRarity.uncommon)
                .fireResistant());
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.unwitnessedAmuletShift1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.unwitnessedAmuletShift2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.unwitnessedAmuletShift3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.unwitnessedAmuletShift4");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.unwitnessedAmuletHoldShift");
        }
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return false;
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        return tooltips;
    }

}
