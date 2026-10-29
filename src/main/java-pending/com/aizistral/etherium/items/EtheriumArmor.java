package com.aizistral.etherium.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticArmorMaterials;
import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.core.EtheriumUtil;
import com.aizistral.etherium.core.IEtheriumConfig;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.Minecraft;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.entity.Entity;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EtheriumArmor extends ItemArmor implements ICreativeTabMember {

	public EtheriumArmor(ItemArmor.Type slot) {
		super(EnigmaticArmorMaterials.ETHERIUM, slot,
				EtheriumUtil.defaultProperties(EtheriumArmor.class).fireResistant());
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return EnigmaticTabs.MAIN;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return "enigmaticlegacy:textures/models/armor/unseen_armor.png";
	}

	public static boolean hasFullSet(@Nonnull EntityPlayer player) {
		if (player == null)
			return false;

		for (ItemStack stack : player.getArmorSlots()) {
			if (!(stack.getItem().getClass() == EtheriumArmor.class))
				return false;
		}

		return true;
	}

	public static boolean hasShield(@Nonnull EntityPlayer player) {
		if (player != null)
			if (hasFullSet(player) && player.getHealth() / player.getMaxHealth()
					<= EtheriumConfigHandler.instance().getShieldThreshold(player).asMultiplier(false))
				return true;

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			EtheriumArmor armor = (EtheriumArmor) stack.getItem();
			if (armor.getType() == Type.HELMET) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumHelmet1");
			} else if (armor.getType() == Type.CHESTPLATE) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumChestplate1");
			} else if (armor.getType() == Type.LEGGINGS) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumLeggings1");
			} else if (armor.getType() == Type.BOOTS) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumBoots1");
			}
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		if (hasFullSet(Minecraft.getInstance().player) || (ItemNBTHelper.verifyExistance(stack, "forceDisplaySetBonus") && ItemNBTHelper.getBoolean(stack, "forceDisplaySetBonus", false))) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumArmorSetBonus1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumArmorSetBonus2", EnumChatFormatting.GOLD, EtheriumConfigHandler.instance().getShieldThreshold(Minecraft.getInstance().player).asPercentage() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumArmorSetBonus3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumArmorSetBonus4", EnumChatFormatting.GOLD, EtheriumConfigHandler.instance().getShieldReduction().asPercentage() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumArmorSetBonus5");

			//ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumArmorSetBonus6");
		}

		if (stack.isEnchanted()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
		}

	}

}
