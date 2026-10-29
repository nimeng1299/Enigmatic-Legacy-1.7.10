package com.aizistral.enigmaticlegacy.items.generic;

import java.util.Set;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.google.common.collect.Sets;

import com.aizistral.enigmaticlegacy.compat.BlockTags;
import com.aizistral.enigmaticlegacy.compat.TagKey;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.item.ItemTool;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.Tier;
import net.minecraft.block.Block;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import com.aizistral.enigmaticlegacy.compat.ToolAction;

public abstract class ItemBaseTool extends ItemTool implements ICreativeTabMember {
	public Set<ToolAction> toolActions;
	public Set<TagKey<Block>> effectiveTags;

	public ItemBaseTool(float attackDamageIn, float attackSpeedIn, Tier tier, Properties builder, Set<TagKey<Block>> effectiveBlocksIn) {
		super(attackDamageIn, attackSpeedIn, tier, effectiveBlocksIn.stream().findAny().get(), builder);
		this.toolActions = Sets.newHashSet();
		this.effectiveTags = effectiveBlocksIn;
	}

	@SuppressWarnings("unchecked")
	public ItemBaseTool(Tier tier) {
		this(4F, -2.8F, tier, ItemBaseTool.getDefaultProperties(), Sets.newHashSet(BlockTags.MINEABLE_WITH_PICKAXE));
	}

	@SuppressWarnings("unchecked")
	public ItemBaseTool() {
		this(4F, -2.8F, EnigmaticMaterials.ETHERIUM, ItemBaseTool.getDefaultProperties(), Sets.newHashSet(BlockTags.MINEABLE_WITH_PICKAXE));
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockIn) {
		return super.isCorrectToolForDrops(stack, blockIn) || this.hasAnyTag(blockIn, this.effectiveTags);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return !this.hasAnyTag(state, this.effectiveTags) ? super.getDestroySpeed(stack, state) : this.speed;
	}

	protected boolean hasAnyTag(BlockState state, Set<TagKey<Block>> tags) {
		return tags.stream().anyMatch(tag -> this.hasTag(state, tag));
	}

	protected boolean hasTag(BlockState state, TagKey<Block> tag) {
		return state.is(tag);
	}
	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return super.canPerformAction(stack, toolAction) || this.toolActions.contains(toolAction);
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
