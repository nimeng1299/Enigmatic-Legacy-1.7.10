package com.aizistral.etherium.items;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import com.aizistral.enigmaticlegacy.compat.BlockTags;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TagKey;
import com.aizistral.enigmaticlegacy.compat.ToolAction;
import com.aizistral.enigmaticlegacy.compat.ToolActions;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.AOEMiningHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.core.EtheriumUtil;
import com.aizistral.etherium.core.IEtheriumTool;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EtheriumScythe extends com.aizistral.enigmaticlegacy.items.generic.ItemBaseTool
    implements IEtheriumTool, ICreativeTabMember {

    protected static final Map<Block, Block> HOE_LOOKUP = Maps.newHashMap();

    static {
        HOE_LOOKUP.put(Blocks.grass, Blocks.farmland);
        HOE_LOOKUP.put(Blocks.dirt, Blocks.farmland);
    }
    public Set<TagKey<Block>> effectiveTags;
    public Set<Block> effectiveBlocks;

    public EtheriumScythe() {
        super(EnigmaticMaterials.ETHERIUM);
        EtheriumUtil.defaultProperties(EtheriumScythe.class)
            .fireResistant()
            .apply(this);
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
        this.effectiveBlocks.add(Blocks.reeds);
        this.effectiveBlocks.add(Blocks.reeds);
        this.effectiveBlocks.add(Blocks.grass);
        this.effectiveBlocks.add(Blocks.tallgrass);
        this.effectiveBlocks.add(Blocks.water);
        this.effectiveBlocks.add(Blocks.water);
        this.effectiveBlocks.add(Blocks.nether_wart);
        this.effectiveBlocks.add(Blocks.cactus);
        this.effectiveBlocks.add(Blocks.reeds);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (this.getConfig()
            .getScytheMiningVolume() == -1) return;

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.etheriumScythe1",
                EnumChatFormatting.GOLD,
                this.getConfig()
                    .getScytheMiningVolume()
                    + this.getConfig()
                        .getAOEBoost(Minecraft.getMinecraft().thePlayer));
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.etheriumScythe2",
                EnumChatFormatting.GOLD,
                this.getConfig()
                    .getScytheMiningVolume());
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            if (!this.getConfig()
                .disableAOEShiftInhibition()) {
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

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack stack = player.getHeldItem();

        if (player.isSneaking()) {
            this.toggleAreaEffects(player, stack);

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        } else return super.use(world, player, hand);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer()
            .isSneaking())
            return this.use(context.getLevel(), context.getPlayer(), context.getHand())
                .getResult();

        EntityPlayer player = context.getPlayer();
        World world = context.getLevel();
        ItemStack stack = context.getHeldItem();

        boolean tilled = this.tillBlock(
            stack,
            player,
            world,
            context.getClickedX(),
            context.getClickedY(),
            context.getClickedZ(),
            context.getClickedFace(),
            context.getClickLocationX(),
            context.getClickLocationY(),
            context.getClickLocationZ());

        if (!this.areaEffectsEnabled(player, stack)) return tilled ? InteractionResult.CONSUME : InteractionResult.PASS;

        int supRad = (this.getConfig()
            .getScytheMiningVolume() - 1) / 2;

        if (tilled) {
            for (int x = -supRad; x <= supRad; x++) {
                for (int z = -supRad; z <= supRad; z++) {
                    if (x == 0 && z == 0) {
                        continue;
                    }

                    this.tillBlock(
                        stack,
                        player,
                        world,
                        context.getClickedX() + x,
                        context.getClickedY(),
                        context.getClickedZ() + z,
                        context.getClickedFace(),
                        context.getClickLocationX(),
                        context.getClickLocationY(),
                        context.getClickLocationZ());
                }
            }
        }

        return tilled ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockIn) {
        return super.isCorrectToolForDrops(stack, blockIn) || this.effectiveBlocks.contains(blockIn.getBlock())
            || this.hasAnyTag(blockIn, this.effectiveTags);
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return (!this.effectiveBlocks.contains(state.getBlock()) && !this.hasAnyTag(state, this.effectiveTags))
            ? super.getDestroySpeed(stack, state)
            : this.toolMaterial.getEfficiencyOnProperMaterial();
    }

    private boolean tillBlock(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        return Items.diamond_hoe.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    protected boolean hasAnyTag(BlockState state, Set<TagKey<Block>> tags) {
        return tags.stream()
            .anyMatch(tag -> this.hasTag(state, tag));
    }

    protected boolean hasTag(BlockState state, TagKey<Block> tag) {
        return state.is(tag);
    }

    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return toolAction == ToolActions.HOE_TILL || toolAction == ToolActions.HOE_DIG
            || super.canPerformAction(stack, toolAction);
    }

    public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos,
        EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer && this.areaEffectsEnabled((EntityPlayer) entityLiving, stack)
            && this.isCorrectToolForDrops(stack, state)
            && !world.isRemote
            && this.getConfig()
                .getScytheMiningVolume() != -1) {
            ForgeDirection face = ForgeDirection.UP;
            int volume = this.getConfig()
                .getScytheMiningVolume()
                + (this.getConfig()
                    .getAOEBoost((EntityPlayer) entityLiving));

            AOEMiningHelper.harvestCube(
                world,
                (EntityPlayer) entityLiving,
                face,
                pos.offset(0, (volume - 1) / 2, 0),
                (s) -> this.isCorrectToolForDrops(stack, s),
                volume,
                volume,
                false,
                pos,
                stack,
                (objPos, objState) -> { stack.damageItem(1, entityLiving); });
        }

        return super.mineBlock(stack, world, state, pos, entityLiving);
    }

}
