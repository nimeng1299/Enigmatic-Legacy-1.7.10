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
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
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
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class GemOfBinding extends ItemBase implements Vanishable {

	public GemOfBinding() {
		super(ItemBase.getDefaultProperties().rarity(EnumRarity.UNCOMMON).stacksTo(1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.gemOfBinding1");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		if (ItemNBTHelper.verifyExistance(stack, "BoundPlayer")) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.boundToPlayer", EnumChatFormatting.DARK_RED, ItemNBTHelper.getString(stack, "BoundPlayer", "Herobrine"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return ItemNBTHelper.verifyExistance(stack, "BoundPlayer") || super.isFoil(stack);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);

		if (player.isSneaking()) {
			ItemNBTHelper.setString(itemstack, "BoundPlayer", player.getDisplayName().getString());
			ItemNBTHelper.setUUID(itemstack, "BoundUUID", player.getUniqueID());

			worldIn.playSound(null, CompatPos.of(player), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, (float) (0.9F + (Math.random() * 0.1F)));

			player.swing(hand);
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
		}

		return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
	}

}
