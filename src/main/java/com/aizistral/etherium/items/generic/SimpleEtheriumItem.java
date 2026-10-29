package com.aizistral.etherium.items.generic;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Plain etherium material item with a registered inventory icon.
 */
public abstract class SimpleEtheriumItem extends Item implements ICreativeTabMember {

    @SideOnly(Side.CLIENT)
    protected IIcon itemIcon;

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

    @SideOnly(Side.CLIENT)
    protected String getIconName() {
        String name = this.getUnlocalizedName();

        if (name != null && name.startsWith("item.")) {
            name = name.substring("item.".length());
        }

        if (name != null && name.startsWith("enigmaticlegacy.")) {
            name = name.substring("enigmaticlegacy.".length());
        }

        return name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registry) {
        this.itemIcon = registry.registerIcon("enigmaticlegacy:item/" + this.getIconName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return this.itemIcon;
    }

}
