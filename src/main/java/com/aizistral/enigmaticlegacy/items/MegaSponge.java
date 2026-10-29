package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.compat.Mth;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPortalParticles;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MegaSponge extends ItemBaseCurio implements Vanishable {

    public static Omniconfig.IntParameter radius;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("Megasponge");

        radius = builder
            .comment("Radius in which Exptrapolated Megaspong absorbs water. Default 4 equals to vanilla sponge")
            .max(128)
            .getInt("Radius", 4);

        builder.popPrefix();
    }

    public MegaSponge() {
        super(
            ItemBaseCurio.getDefaultProperties()
                .rarity(EnumRarity.uncommon));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.megaSponge1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.megaSponge2");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    @Nullable
    public BlockPos getCollidedWater(EntityPlayer player) {
        AxisAlignedBB axisalignedbb = player.boundingBox.expand(-0.001D, -0.001D, -0.001D);
        int i = Mth.floor(axisalignedbb.minX);
        int j = Mth.ceil(axisalignedbb.maxX);
        int k = Mth.floor(axisalignedbb.minY);
        int l = Mth.ceil(axisalignedbb.maxY);
        int i1 = Mth.floor(axisalignedbb.minZ);
        int j1 = Mth.ceil(axisalignedbb.maxZ);

        BlockPos pos = null;

        for (int x = i; x < j; ++x) {
            for (int y = k; y < l; ++y) {
                for (int z = i1; z < j1; ++z) {
                    if (player.worldObj.getBlock(x, y, z)
                        .getMaterial() == Material.water) {
                        pos = new BlockPos(x, y, z);
                    }
                }
            }
        }

        return pos;
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof EntityPlayer && !context.entity().worldObj.isRemote) {
            EntityPlayer player = (EntityPlayer) context.entity();

            if (!com.aizistral.enigmaticlegacy.compat.PlayerCooldowns.isOnCooldown(player, this)) {
                List<BlockPos> doomedWaterBlocks = new ArrayList<BlockPos>();
                BlockPos initialPos = this.getCollidedWater(player);

                if (initialPos != null) {
                    doomedWaterBlocks.add(initialPos);
                    List<BlockPos> processedBlocks = new ArrayList<BlockPos>();
                    processedBlocks.add(initialPos);

                    for (int counter = 0; counter <= radius.getValue(); counter++) {
                        List<BlockPos> outputBlocks = new ArrayList<BlockPos>();

                        for (BlockPos checkedPos : processedBlocks) {
                            outputBlocks.addAll(this.getNearbyWater(player.worldObj, checkedPos));
                        }

                        processedBlocks.clear();

                        for (BlockPos thePos : outputBlocks) {
                            if (!doomedWaterBlocks.contains(thePos)) {
                                processedBlocks.add(thePos);
                                doomedWaterBlocks.add(thePos);
                            }
                        }

                        outputBlocks.clear();
                    }

                    processedBlocks.clear();

                    for (BlockPos exterminatedBlock : doomedWaterBlocks) {
                        this.absorbWaterBlock(exterminatedBlock, player.worldObj);
                    }

                    doomedWaterBlocks.clear();

                    player.worldObj.playSoundAtEntity(
                        player,
                        SoundEvents.BUCKET_FILL.getName(),
                        1.0F,
                        (float) (0.8F + (Math.random() * 0.2)));
                    EnigmaticLegacy.packetInstance.send(
                        PacketDistributor.NEAR.with(
                            () -> new PacketDistributor.TargetPoint(
                                player.posX,
                                player.posY,
                                player.posZ,
                                64,
                                player.worldObj.provider.dimensionId)),
                        new PacketPortalParticles(
                            player.posX,
                            player.posY + (player.height / 2),
                            player.posZ,
                            40,
                            1.0D,
                            false));
                    com.aizistral.enigmaticlegacy.compat.PlayerCooldowns.addCooldown(player, this, 20);
                }
            }
        }
    }

    public void absorbWaterBlock(BlockPos pos, World world) {
        if (world.getBlock(pos.getX(), pos.getY(), pos.getZ())
            .getMaterial() == Material.water) {
            world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public List<BlockPos> getNearbyWater(World world, BlockPos pos) {
        List<BlockPos> nearBlocks = new ArrayList<BlockPos>();
        List<BlockPos> waterBlocks = new ArrayList<BlockPos>();

        nearBlocks.add(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()));
        nearBlocks.add(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()));
        nearBlocks.add(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()));
        nearBlocks.add(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()));
        nearBlocks.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1));
        nearBlocks.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1));

        for (BlockPos checkedPos : nearBlocks) {
            if (world.getBlock(checkedPos.getX(), checkedPos.getY(), checkedPos.getZ())
                .getMaterial() == Material.water) {
                waterBlocks.add(checkedPos);
            }
        }

        return waterBlocks;
    }

}
