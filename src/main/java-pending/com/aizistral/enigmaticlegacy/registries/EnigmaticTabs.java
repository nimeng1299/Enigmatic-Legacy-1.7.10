package com.aizistral.enigmaticlegacy.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.blocks.BlockAstralDust;
import com.aizistral.enigmaticlegacy.blocks.BlockBigLamp;
import com.aizistral.enigmaticlegacy.blocks.BlockCosmicCake;
import com.aizistral.enigmaticlegacy.blocks.BlockEndAnchor;
import com.aizistral.enigmaticlegacy.blocks.BlockMassiveLamp;
import com.aizistral.enigmaticlegacy.items.CosmicCake;
import com.aizistral.enigmaticlegacy.items.EndAnchor;
import com.aizistral.enigmaticlegacy.items.generic.GenericBlockItem;
import com.aizistral.etherium.blocks.BlockEtherium;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import net.minecraft.block.Block;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;

public class EnigmaticTabs extends AbstractRegistry<CreativeTabs> {
	private static final EnigmaticTabs INSTANCE = new EnigmaticTabs();

	// Object holders don't work for creative tabs for some reason

	//	@ObjectHolder(value = MODID + ":tab_main", registryName = "creative_mode_tab")
	public static final CreativeTabs MAIN;

	//	@ObjectHolder(value = MODID + ":tab_potions", registryName = "creative_mode_tab")
	public static final CreativeTabs POTIONS;

	static {
		MAIN = CreativeTabs.builder().title(Component.translatable("itemGroup.enigmaticCreativeTab"))
				.icon(() -> new ItemStack(EnigmaticItems.ENIGMATIC_ITEM)).build();

		POTIONS = CreativeTabs.builder().title(Component.translatable("itemGroup.enigmaticPotionCreativeTab"))
				.icon(() -> new ItemStack(EnigmaticItems.RECALL_POTION)).build();
	}

	private EnigmaticTabs() {
		super(Registries.CREATIVE_MODE_TAB);
		this.register("tab_potions", () -> POTIONS);
		this.register("tab_main", () -> MAIN);
	}

	@Override
	protected void onRegister(RegisterEvent event) {
		// NO-OP
	}

}
