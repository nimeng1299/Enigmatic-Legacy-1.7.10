package com.aizistral.etherium.items.generic;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.core.IEtheriumConfig;
import com.aizistral.etherium.core.IEtheriumTool;
import com.google.common.collect.Sets;

import com.aizistral.enigmaticlegacy.compat.TagKey;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.item.ItemTool;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import com.aizistral.enigmaticlegacy.compat.BlockState;

public abstract class ItemEtheriumTool extends ItemTool implements IEtheriumTool, ICreativeTabMember {
	public Set<TagKey<Block>> effectiveTags;

	public ItemEtheriumTool(float attackDamageIn, float attackSpeedIn, TagKey<Block> blocks, Properties builder) {
		super(attackDamageIn, attackSpeedIn, EnigmaticMaterials.ETHERIUM, blocks, builder);
		this.effectiveTags = Sets.newHashSet();
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return EnigmaticTabs.MAIN;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockIn) {
		return super.isCorrectToolForDrops(stack, blockIn) || this.hasAnyTag(blockIn, this.effectiveTags);
	}

	protected boolean hasAnyTag(BlockState state, Set<TagKey<Block>> tags) {
		return tags.stream().anyMatch(tag -> this.hasTag(state, tag));
	}

	protected boolean hasTag(BlockState state, TagKey<Block> tag) {
		return state.is(tag);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return !this.hasAnyTag(state, this.effectiveTags) ? super.getDestroySpeed(stack, state) : this.speed;
	}

}
