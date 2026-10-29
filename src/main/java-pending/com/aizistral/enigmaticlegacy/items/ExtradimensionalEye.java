package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExtradimensionalEye extends ItemBase implements Vanishable {

	public float range = 3.0F;

	public ExtradimensionalEye() {
		super(ItemBase.getDefaultProperties().rarity(EnumRarity.UNCOMMON).stacksTo(1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEye1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEye2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEye3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEye4");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEye5");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEye6");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEye7");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
		if (ItemNBTHelper.verifyExistance(stack, "BoundDimension")) {

			String boundDimensionName = null;
			String dimensionID = ItemNBTHelper.getString(stack, "BoundDimension", "minecraft:overworld");

			if (dimensionID.equals("minecraft:overworld")) {
				boundDimensionName = "tooltip.enigmaticlegacy.overworld";
			} else if (dimensionID.equals("minecraft:the_nether")) {
				boundDimensionName = "tooltip.enigmaticlegacy.nether";
			} else if (dimensionID.equals("minecraft:the_end")) {
				boundDimensionName = "tooltip.enigmaticlegacy.end";
			}

			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEyeLocation");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEyeX", EnumChatFormatting.GOLD, ItemNBTHelper.getInt(stack, "BoundX", 0));
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEyeY", EnumChatFormatting.GOLD, ItemNBTHelper.getInt(stack, "BoundY", 0));
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEyeZ", EnumChatFormatting.GOLD, ItemNBTHelper.getInt(stack, "BoundZ", 0));
			if (boundDimensionName != null) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEyeDimension", null, Component.translatable(boundDimensionName).getString());
			} else {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.extradimensionalEyeDimension", EnumChatFormatting.GOLD, dimensionID);
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);

		if (playerIn.isSneaking() && ItemNBTHelper.getString(itemstack, "BoundDimension", null) == null) {
			ItemNBTHelper.setDouble(itemstack, "BoundX", playerIn.getX());
			ItemNBTHelper.setDouble(itemstack, "BoundY", playerIn.getY());
			ItemNBTHelper.setDouble(itemstack, "BoundZ", playerIn.getZ());

			ItemNBTHelper.setString(itemstack, "BoundDimension", playerIn.worldObj.dimension().location().toString());
			playerIn.swing(handIn);
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
		}

		return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
	}

}
