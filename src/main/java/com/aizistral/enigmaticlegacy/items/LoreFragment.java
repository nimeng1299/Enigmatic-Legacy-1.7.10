package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LoreFragment extends ItemBase {

    public LoreFragment() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.uncommon)
                .stacksTo(16));

        // TODO Lore Fragment copying recipe
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {

        net.minecraft.nbt.NBTTagCompound display = stack.getTagCompound() != null ? stack.getTagCompound()
            .getCompoundTag("display") : new net.minecraft.nbt.NBTTagCompound();

        if (!display.getString("Name")
            .equals("")
            || display.getTagList("Lore", 8)
                .tagCount() != 0)
            return;

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreFragment1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreFragment2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreFragment3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreFragment4");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

    }

}
