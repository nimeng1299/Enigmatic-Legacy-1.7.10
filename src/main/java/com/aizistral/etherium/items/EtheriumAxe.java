package com.aizistral.etherium.items;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.AOEMiningHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.core.EtheriumUtil;
import com.aizistral.etherium.core.IEtheriumTool;
import com.google.common.collect.Sets;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EtheriumAxe extends com.aizistral.enigmaticlegacy.items.generic.ItemBaseTool
    implements IEtheriumTool, ICreativeTabMember {

    public Set<TagKey<Block>> effectiveTags;

    public EtheriumAxe() {
        super(EnigmaticMaterials.ETHERIUM);
        EtheriumUtil.defaultProperties(EtheriumAxe.class)
            .fireResistant()
            .apply(this);
        this.effectiveTags = Sets.newHashSet();
        this.effectiveTags.add(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (this.getConfig()
            .getAxeMiningVolume() == -1) return;

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.etheriumAxe1",
                EnumChatFormatting.GOLD,
                this.getConfig()
                    .getAxeMiningVolume()
                    + this.getConfig()
                        .getAOEBoost(Minecraft.getMinecraft().thePlayer));
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

            if (!this.getConfig()
                .disableAOEShiftInhibition()) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumAxe2");
            }
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumAxe3");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        if (!this.areaEffectsAllowed(stack)) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.aoeDisabled");
        }
    }

    public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos,
        EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer && this.areaEffectsEnabled((EntityPlayer) entityLiving, stack)
            && this.isCorrectToolForDrops(stack, state)
            && !world.isRemote
            && this.getConfig()
                .getAxeMiningVolume() != -1) {
            ForgeDirection face = ForgeDirection.UP;
            int volume = this.getConfig()
                .getAxeMiningVolume()
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
                true,
                pos,
                stack,
                (objPos, objState) -> { stack.damageItem(1, entityLiving); });
        }

        return super.mineBlock(stack, world, state, pos, entityLiving);
    }

    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockIn) {
        return super.isCorrectToolForDrops(stack, blockIn) || this.hasAnyTag(blockIn, this.effectiveTags);
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return !this.hasAnyTag(state, this.effectiveTags) ? super.getDestroySpeed(stack, state)
            : this.toolMaterial.getEfficiencyOnProperMaterial();
    }

    protected boolean hasAnyTag(BlockState state, Set<TagKey<Block>> tags) {
        return tags.stream()
            .anyMatch(tag -> this.hasTag(state, tag));
    }

    protected boolean hasTag(BlockState state, TagKey<Block> tag) {
        return state.is(tag);
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
        else return super.useOn(context);
    }

}
