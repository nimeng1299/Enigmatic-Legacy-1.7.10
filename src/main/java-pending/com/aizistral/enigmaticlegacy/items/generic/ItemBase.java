package com.aizistral.enigmaticlegacy.items.generic;

import java.util.Random;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.text.MutableComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.ClipContext;
import net.minecraft.world.World;
import net.minecraft.util.MovingObjectPosition;

public abstract class ItemBase extends Item implements ICreativeTabMember {
	protected static final Random random = new Random();
	protected boolean isPlaceholder;

	public ItemBase() {
		this(ItemBase.getDefaultProperties());
	}

	public ItemBase(Properties props) {
		super(props);
		this.isPlaceholder = false;
	}

	@Override
	public void onCraftedBy(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		// Insert existential void here
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return EnigmaticTabs.MAIN;
	}

	public static Properties getDefaultProperties() {
		Properties props = new Item.Properties();

		props.stacksTo(64);
		props.rarity(EnumRarity.COMMON);

		return props;
	}

	public static MovingObjectPosition rayTrace(World worldIn, EntityPlayer player, ClipContext.Fluid fluidMode) {
		return Item.getPlayerPOVHitResult(worldIn, player, fluidMode);
	}

	public Item setPlaceholder() {
		this.isPlaceholder = true;
		return this;
	}

	public boolean isPlaceholder() {
		return this.isPlaceholder;
	}

	@Override
	public Component getName(ItemStack stack) {
		Component superName = super.getName(stack);

		if (this.isPlaceholder) {
			if (superName instanceof MutableComponent)
				return ((MutableComponent)superName).withStyle(EnumChatFormatting.OBFUSCATED);
		}

		return superName;
	}

	protected static String minimizeNumber(double num) {
		return SuperpositionHandler.minimizeNumber(num);
	}

}
