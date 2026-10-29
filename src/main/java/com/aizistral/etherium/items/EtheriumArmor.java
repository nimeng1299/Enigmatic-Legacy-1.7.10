package com.aizistral.etherium.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticArmorMaterials;
import com.aizistral.enigmaticlegacy.compat.ArmorType;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseArmor;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EtheriumArmor extends ItemBaseArmor implements ICreativeTabMember {

    public EtheriumArmor(int slot) {
        super(EnigmaticArmorMaterials.ETHERIUM, slot, slot);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "enigmaticlegacy:textures/models/armor/unseen_armor.png";
    }

    private static boolean wearingFullSet(EntityPlayer player) {
        if (player == null) return false;

        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null || !(stack.getItem() instanceof EtheriumArmor)) return false;
        }

        return true;
    }

    public static boolean hasShield(@Nonnull EntityPlayer player) {
        if (player != null)
            if (wearingFullSet(player) && player.getHealth() / player.getMaxHealth() <= EtheriumConfigHandler.instance()
                .getShieldThreshold(player)
                .asMultiplier(false)) return true;

        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (Screen.hasShiftDown()) {
            if (this.armorType == ArmorType.HELMET) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumHelmet1");
            } else if (this.armorType == ArmorType.CHESTPLATE) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumChestplate1");
            } else if (this.armorType == ArmorType.LEGGINGS) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumLeggings1");
            } else if (this.armorType == ArmorType.BOOTS) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumBoots1");
            }
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        if (hasFullSet(player) || ItemNBTHelper.verifyExistance(stack, "forceDisplaySetBonus")
            && ItemNBTHelper.getBoolean(stack, "forceDisplaySetBonus", false)) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumArmorSetBonus1");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.etheriumArmorSetBonus2",
                EnumChatFormatting.GOLD,
                EtheriumConfigHandler.instance()
                    .getShieldThreshold(player)
                    .asPercentage() + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumArmorSetBonus3");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.etheriumArmorSetBonus4",
                EnumChatFormatting.GOLD,
                EtheriumConfigHandler.instance()
                    .getShieldReduction()
                    .asPercentage() + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumArmorSetBonus5");
        }

        if (stack.isItemEnchanted()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        }
    }

}
