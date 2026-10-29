package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import com.aizistral.enigmaticlegacy.compat.ToolAction;
import net.minecraftforge.common.ToolActions;

public class InfernalShield extends ItemBase implements ICursed, Vanishable {

	public InfernalShield() {
		super(ItemBase.getDefaultProperties().fireResistant().rarity(EnumRarity.EPIC).stacksTo(1).durability(10000));
		DispenserBlock.registerBehavior(this, ItemArmor.DISPENSE_ITEM_BEHAVIOR);
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield1");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield2");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield3");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield4");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield5");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield6");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield7");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield8");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield9");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield10");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield11");
		} else {
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.holdShift");
		}

		ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
		ItemLoreHelper.indicateCursedOnesOnly(tooltip);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (SuperpositionHandler.isTheCursedOne(player)) {
			player.startUsingItem(hand);
			return InteractionResultHolder.consume(stack);
		} else
			return InteractionResultHolder.pass(stack);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
		if (repairStack.getItem() instanceof ItemBlock blockItem)
			return blockItem.getBlock() == Blocks.OBSIDIAN;
		else
			return super.isValidRepairItem(stack, repairStack);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity holder, int slot, boolean hand) {
		if (holder instanceof EntityPlayerMP player && player.isOnFire() && SuperpositionHandler.isTheCursedOne(player)) {
			if (player.getHeldItem() == stack || player.getHeldItemOffhand() == stack) {
				if (!player.isInLava()) {
					player.clearFire();
				}
			}
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment instanceof DigDurabilityEnchantment || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public boolean isEnchantable(ItemStack pStack) {
		return true;
	}

	@Override
	public int getEnchantmentValue(ItemStack stack) {
		return 16;
	}

}
