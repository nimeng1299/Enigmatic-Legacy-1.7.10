package com.aizistral.enigmaticlegacy.items.generic;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemBaseArmor extends ItemArmor implements ICreativeTabMember {

    @SideOnly(Side.CLIENT)
    protected IIcon itemIcon;

    public ItemBaseArmor(ArmorMaterial materialIn, int renderIndex, int armorType) {
        super(materialIn, renderIndex, armorType);
        this.setMaxStackSize(1);
    }

    private String getIconName() {
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

    public boolean hasFullSet(@Nonnull EntityPlayer player) {
        if (player == null) return false;

        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null || stack.getItem()
                .getClass() != this.getClass()) return false;
        }

        return true;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return EnigmaticLegacy.MODID + ":textures/models/armor/unseen_armor.png";
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

    public int getArmorType() {
        return this.armorType;
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        List<Component> lore = new ArrayList<>();
        this.appendHoverText(
            stack,
            player == null ? null : player.worldObj,
            lore,
            advanced ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
        list.addAll(lore);
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        // NO-OP
    }

}
