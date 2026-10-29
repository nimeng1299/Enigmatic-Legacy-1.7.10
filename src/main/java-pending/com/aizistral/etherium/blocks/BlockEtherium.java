package com.aizistral.etherium.blocks;

import java.util.ArrayList;
import java.util.List;

import com.aizistral.etherium.core.IEtheriumConfig;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;

public class BlockEtherium extends Block {

	public BlockEtherium() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops()
				.strength(5F, 1200F).lightLevel(state -> 10));
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		return List.of(new ItemStack(this));
	}

}
