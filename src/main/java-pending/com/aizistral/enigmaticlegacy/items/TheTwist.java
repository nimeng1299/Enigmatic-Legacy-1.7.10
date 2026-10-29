package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.Minecraft;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TheTwist extends TheAcknowledgment implements ICursed {
	public static Omniconfig.DoubleParameter attackDamage;
	public static Omniconfig.DoubleParameter attackSpeed;
	public static Omniconfig.PerhapsParameter bossDamageBonus;
	public static Omniconfig.PerhapsParameter knockbackBonus;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("TheTwist");

		attackDamage = builder
				.comment("Attack damage of The Twist, actual damage shown in tooltip will be is 1 + this_value.")
				.max(32768)
				.getDouble("AttackDamage", 8);

		attackSpeed = builder
				.comment("Attack speed of The Twist.")
				.minMax(32768)
				.getDouble("AttackSpeed", -1.8);

		bossDamageBonus = builder
				.comment("Attack damage bonus of The Twist against players and bossess.")
				.getPerhaps("BossDamageBonus", 300);

		knockbackBonus = builder
				.comment("Knockback bonus of The Twist. For Phantoms, this value is multiplied by 1.5.")
				.getPerhaps("KnockbackPowerBonus", 300);

		builder.popPrefix();
	}

	public TheTwist() {
		super(getDefaultProperties().rarity(EnumRarity.EPIC).stacksTo(1).fireResistant(),
				"the_twist", attackDamage.getValue(), attackSpeed.getValue());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List<Component> list, TooltipFlag flag) {
		if (Screen.hasShiftDown()) {
			if (Minecraft.getInstance().player != null && SuperpositionHandler.isTheCursedOne(Minecraft.getInstance().player)) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theTwist4");
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theTwist5");
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			}
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theTwist6", EnumChatFormatting.GOLD, bossDamageBonus + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theTwist7", EnumChatFormatting.GOLD, knockbackBonus + "%");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theTwist1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theTwist2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
		ItemLoreHelper.indicateCursedOnesOnly(list);

		if (stack.isEnchanted()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
		}

		try {
			//list.add(new TextComponent("").append(TheAcknowledgment.getEdition()).mergeStyle(ChatFormatting.DARK_PURPLE));
		} catch (Exception ex) {
			// Just don't do it lol
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
		if (!SuperpositionHandler.isTheCursedOne(player))
			return InteractionResultHolder.pass(player.getItemInHand(hand));

		if (hand == InteractionHand.MAIN_HAND) {
			ItemStack offhandStack = player.getHeldItemOffhand();

			if (offhandStack != null && (offhandStack.getItem().getUseAnimation(offhandStack) == UseAnim.BLOCK))
				return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, player.getItemInHand(hand));
		}

		return super.use(world, player, hand);
	}

}
