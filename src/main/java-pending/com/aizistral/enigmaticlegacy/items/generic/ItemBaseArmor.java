package com.aizistral.enigmaticlegacy.items.generic;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.client.models.UnseenArmorModel;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.entity.Entity;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

@SuppressWarnings("deprecation")
public abstract class ItemBaseArmor extends ItemArmor implements ICreativeTabMember {
	@SideOnly(Side.CLIENT)
	private HumanoidModel<?> model;

	public ItemBaseArmor(ArmorMaterial materialIn, ItemArmor.Type slot, Properties builder) {
		super(materialIn, slot, builder);
	}

	public ItemBaseArmor(ArmorMaterial materialIn, ItemArmor.Type slot) {
		this(materialIn, slot, ItemBaseArmor.getDefaultProperties());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			@SideOnly(Side.CLIENT)
			@SuppressWarnings("unchecked")
			public HumanoidModel<?> getHumanoidArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> original) {
				return ItemBaseArmor.this.provideArmorModelForSlot(armorSlot, original);
			}
		});
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public HumanoidModel<?> provideArmorModelForSlot(EquipmentSlot slot, HumanoidModel<?> original) {
		return this.model != null ? this.model : (this.model = new UnseenArmorModel<>(original));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return EnigmaticLegacy.MODID + ":textures/models/armor/unseen_armor.png";
	}

	public boolean hasFullSet(@Nonnull EntityPlayer player) {
		if (player == null)
			return false;

		for (ItemStack stack : player.getArmorSlots()) {
			if (!(stack.getItem().getClass() == this.getClass()))
				return false;
		}

		return true;
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return EnigmaticTabs.MAIN;
	}

	public static Properties getDefaultProperties() {
		Properties props = new Item.Properties();

		props.stacksTo(1);
		props.rarity(EnumRarity.COMMON);

		return props;
	}

}
