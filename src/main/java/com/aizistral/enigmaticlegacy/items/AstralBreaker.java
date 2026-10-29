package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.IMultiblockMiningTool;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import com.aizistral.enigmaticlegacy.compat.BlockTags;
import com.aizistral.enigmaticlegacy.compat.ClipContext;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.ToolAction;
import com.aizistral.enigmaticlegacy.compat.ToolActions;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.AOEMiningHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseTool;
import com.aizistral.enigmaticlegacy.packets.clients.PacketFlameParticles;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.Sets;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// TODO Make sure Astral Breaker actually works
// ... and fix the thing where it works like shears?
public class AstralBreaker extends ItemBaseTool implements IMultiblockMiningTool {

    public static Omniconfig.IntParameter miningRadius;
    public static Omniconfig.IntParameter miningDepth;

    public static Omniconfig.BooleanParameter flameParticlesToggle;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("AstralBreaker");

        if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
            miningRadius = builder.comment("The radius of Astral Breaker AOE mining. Set to -1 to disable the feature.")
                .min(-1)
                .max(128 - 1)
                .getInt("MiningRadius", 3);

            miningDepth = builder.comment("The depth of Astral Breaker AOE mining.")
                .max(128 - 1)
                .getInt("MiningDepth", 1);
        } else {
            flameParticlesToggle = builder
                .comment("Whether or not flame particles should appear when the Astral Breaker breaks a block")
                .getBoolean("FlameParticlesToggle", true);
        }

        builder.popPrefix();
    }

    @SuppressWarnings("unchecked")
    public AstralBreaker() {
        super(
            4F,
            -2.8F,
            EnigmaticMaterials.ETHERIUM,
            ItemBaseTool.getDefaultProperties()
                .defaultDurability(4000)
                .rarity(EnumRarity.epic)
                .fireResistant(),
            Sets.newHashSet(
                BlockTags.MINEABLE_WITH_PICKAXE,
                BlockTags.MINEABLE_WITH_AXE,
                BlockTags.MINEABLE_WITH_SHOVEL,
                BlockTags.MINEABLE_WITH_HOE));
    }

    private Item findTool(String name) {
        return SuperpositionHandler.findItem(EnigmaticLegacy.MODID, name);
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.astralBreaker1",
                EnumChatFormatting.GOLD,
                miningRadius.getValue() + EtheriumConfigHandler.instance()
                    .getAOEBoost(Minecraft.getMinecraft().thePlayer),
                miningDepth.getValue());
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.astralBreaker2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.astralBreaker3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.astralBreaker4");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        if (!this.areaEffectsAllowed(stack)) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.aoeDisabled");
        }
    }

    public void spawnFlameParticles(World world, BlockPos pos) {
        EnigmaticLegacy.packetInstance.send(
            PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    128,
                    world.provider.dimensionId)),
            new PacketFlameParticles(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 18, true));
    }

    public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos,
        EntityLivingBase entityLiving) {
        if (!world.isRemote) {
            this.spawnFlameParticles(world, pos);
        }

        if (entityLiving instanceof EntityPlayer player && this.areaEffectsEnabled(player, stack)
            && this.isCorrectToolForDrops(stack, state)
            && !world.isRemote
            && miningRadius.getValue() != -1) {
            MovingObjectPosition blockTrace = AOEMiningHelper.calcRayTrace(world, player, ClipContext.Fluid.ANY);
            if (blockTrace != null && blockTrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                ForgeDirection face = ForgeDirection.getOrientation(blockTrace.sideHit);

                AOEMiningHelper.harvestCube(
                    world,
                    player,
                    face,
                    pos,
                    (s) -> this.isCorrectToolForDrops(stack, s),
                    miningRadius.getValue() + EtheriumConfigHandler.instance()
                        .getAOEBoost(player),
                    miningDepth.getValue(),
                    true,
                    pos,
                    stack,
                    (objPos, objState) -> {
                        stack.damageItem(1, entityLiving);
                        this.spawnFlameParticles(world, objPos);
                    });
            }
        }

        return super.mineBlock(stack, world, state, pos, entityLiving);
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

    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return toolAction == ToolActions.AXE_DIG || toolAction == ToolActions.PICKAXE_DIG
            || toolAction == ToolActions.SHOVEL_DIG
            || toolAction == ToolActions.HOE_DIG
            || toolAction == ToolActions.SWORD_DIG;
    }

}
