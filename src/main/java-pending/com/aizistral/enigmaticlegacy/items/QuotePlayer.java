package com.aizistral.enigmaticlegacy.items;

import java.awt.TextComponent;
import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.IHidden;
import com.aizistral.enigmaticlegacy.client.Quote;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class QuotePlayer extends ItemBase implements IHidden {

	public QuotePlayer() {
		super(getDefaultProperties().rarity(EnumRarity.EPIC).stacksTo(1).fireResistant());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.quotePlayer1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.quotePlayer2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.quotePlayer3");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.quotePlayerSelection", EnumChatFormatting.GOLD,
				Quote.getAllQuotes().get(stack.getDamageValue()).getName().toUpperCase());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);

		if (player.isSneaking()) {
			player.startUsingItem(hand);

			if (itemstack.getDamageValue() < Quote.getAllQuotes().size() - 1) {
				itemstack.setDamageValue(itemstack.getDamageValue() + 1);
			} else {
				itemstack.setDamageValue(0);
			}

			player.swing(hand);

			if (player instanceof EntityPlayerMP) {
				player.displayClientMessage(Component.literal("Quote: " + Quote.getAllQuotes().get(itemstack.getDamageValue()).getName().toUpperCase()), true);
			}
		} else {
			if (player instanceof EntityPlayerMP splayer) {
				Quote.getAllQuotes().get(itemstack.getDamageValue()).play(splayer, 10);
			}
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
	}

}
