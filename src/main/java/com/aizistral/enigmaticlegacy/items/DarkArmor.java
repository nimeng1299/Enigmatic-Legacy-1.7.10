package com.aizistral.enigmaticlegacy.items;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseArmor;

public class DarkArmor extends ItemBaseArmor {

    public final String TEXTURE = EnigmaticLegacy.MODID + ":textures/models/armor/dark_armor.png";

    public DarkArmor(ArmorMaterial materialIn, int slot) {
        super(materialIn, slot, slot);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return null;
    }

    @Nonnull
    @Override
    public final String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return this.TEXTURE;
    }

}
