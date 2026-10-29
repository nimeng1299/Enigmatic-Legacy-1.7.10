package com.aizistral.etherium.items;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.helpers.AOEMiningHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.core.EtheriumUtil;
import com.aizistral.etherium.core.IEtheriumConfig;
import com.aizistral.etherium.core.IEtheriumTool;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.Minecraft;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.BlockTags;
import com.aizistral.enigmaticlegacy.compat.TagKey;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSword;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import net.minecraft.util.MovingObjectPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.ToolAction;
import net.minecraftforge.common.ToolActions;

public class EtheriumScythe extends ItemSword implements IEtheriumTool, ICreativeTabMember {
	protected static final Map<Block, BlockState> HOE_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK,
			Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT_PATH, Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT,
			Blocks.FARMLAND.defaultBlockState(), Blocks.COARSE_DIRT, Blocks.DIRT.defaultBlockState()));
	public Set<TagKey<Block>> effectiveTags;
	public Set<Block> effectiveBlocks;

	public EtheriumScythe() {
		super(EnigmaticMaterials.ETHERIUM, 3, -2.0F, EtheriumUtil.defaultProperties(EtheriumScythe.class).fireResistant());
		this.effectiveTags = Sets.newHashSet();
		this.effectiveTags.add(BlockTags.MINEABLE_WITH_HOE);
		this.effectiveTags.add(BlockTags.FLOWERS);
		this.effectiveTags.add(BlockTags.SMALL_FLOWERS);
		this.effectiveTags.add(BlockTags.TALL_FLOWERS);
		this.effectiveTags.add(BlockTags.LEAVES);
		this.effectiveTags.add(BlockTags.CAVE_VINES);
		this.effectiveTags.add(BlockTags.CROPS);
		this.effectiveTags.add(BlockTags.REPLACEABLE);

		this.effectiveBlocks = Sets.newHashSet();
		this.effectiveBlocks.add(Blocks.BAMBOO);
		this.effectiveBlocks.add(Blocks.BAMBOO_SAPLING);
		this.effectiveBlocks.add(Blocks.GRASS);
		this.effectiveBlocks.add(Blocks.TALL_GRASS);
		this.effectiveBlocks.add(Blocks.SEAGRASS);
		this.effectiveBlocks.add(Blocks.TALL_SEAGRASS);
		this.effectiveBlocks.add(Blocks.NETHER_WART);
		this.effectiveBlocks.add(Blocks.CACTUS);
		this.effectiveBlocks.add(Blocks.SUGAR_CANE);
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return EnigmaticTabs.MAIN;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (this.getConfig().getScytheMiningVolume() == -1)
			return;

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumScythe1", EnumChatFormatting.GOLD, this.getConfig().getScytheMiningVolume() + this.getConfig().getAOEBoost(Minecraft.getInstance().player));
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumScythe2", EnumChatFormatting.GOLD, this.getConfig().getScytheMiningVolume());
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			if (!this.getConfig().disableAOEShiftInhibition()) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumScythe3");
			}
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumScythe4");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		if (!this.areaEffectsAllowed(stack)) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.aoeDisabled");
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);

		if (player.isSneaking()) {
			this.toggleAreaEffects(player, stack);

			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		} else
			return super.use(world, player, hand);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer().isSneaking())
			return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();

		InteractionResult type = Items.DIAMOND_HOE.useOn(context);

		if (!this.areaEffectsEnabled(context.getPlayer(), context.getItemInHand()))
			return type;

		int supRad = (this.getConfig().getScytheMiningVolume() - 1) / 2;

		if (type == InteractionResult.CONSUME) {
			for (int x = -supRad; x <= supRad; x++) {
				for (int z = -supRad; z <= supRad; z++) {
					if (x == 0 & z == 0) {
						continue;
					}

					Items.DIAMOND_HOE.useOn(new UseOnContext(context.getPlayer(), context.getHand(), new MovingObjectPosition(context.getClickLocation().add(x, 0, z), ForgeDirection.UP, context.getClickedPos().offset(x, 0, z), context.isInside())));
				}
			}
		}

		return type;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockIn) {
		return super.isCorrectToolForDrops(stack, blockIn) || this.effectiveBlocks.contains(blockIn.getBlock())
				|| this.hasAnyTag(blockIn, this.effectiveTags);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return (!this.effectiveBlocks.contains(state.getBlock()) && !this.hasAnyTag(state, this.effectiveTags))
				? super.getDestroySpeed(stack, state) : this.getTier().getSpeed();
	}

	protected boolean hasAnyTag(BlockState state, Set<TagKey<Block>> tags) {
		return tags.stream().anyMatch(tag -> this.hasTag(state, tag));
	}

	protected boolean hasTag(BlockState state, TagKey<Block> tag) {
		return state.is(tag);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return toolAction == ToolActions.HOE_TILL || toolAction == ToolActions.HOE_DIG
				|| super.canPerformAction(stack, toolAction);
	}

	@Override
	public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer && this.areaEffectsEnabled((EntityPlayer) entityLiving, stack) && this.isCorrectToolForDrops(stack, state) && !world.isClientSide && this.getConfig().getScytheMiningVolume() != -1) {
			ForgeDirection face = ForgeDirection.UP;
			int volume = this.getConfig().getScytheMiningVolume() + (this.getConfig().getAOEBoost((EntityPlayer) entityLiving));

			AOEMiningHelper.harvestCube(world, (EntityPlayer) entityLiving, face, pos.offset(0, (volume - 1) / 2, 0), (s) -> this.isCorrectToolForDrops(stack, s), volume, volume, false, pos, stack, (objPos, objState) -> {
				stack.hurtAndBreak(1, entityLiving, p -> p.broadcastBreakEvent(EntityLiving.getEquipmentSlotForItem(stack)));
			});
		}

		return super.mineBlock(stack, world, state, pos, entityLiving);
	}

}
