package com.aizistral.enigmaticlegacy.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.aizistral.enigmaticlegacy.items.EndAnchor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 1.7.10 respawn anchor equivalent; charge is stored in block metadata (0-4).
 */
public class BlockEndAnchor extends Block {

    public static final int MIN_CHARGES = 0;
    public static final int MAX_CHARGES = 4;

    public BlockEndAnchor() {
        super(Material.rock);
        this.setBlockName("end_anchor");
        this.setHardness(50F);
        this.setResistance(1200F);
        this.setStepSound(soundTypeStone);
        this.setHarvestLevel("pickaxe", 3);
        this.setLightLevel(0F);
    }

    private static boolean isRespawnFuel(ItemStack stack) {
        return stack != null && stack.getItem() == Items.ender_pearl;
    }

    public static boolean canBeCharged(int meta) {
        return meta < MAX_CHARGES;
    }

    public static boolean canSetSpawn(World level) {
        if (EndAnchor.endExclusiveMode != null && EndAnchor.endExclusiveMode.getValue())
            return level.provider.dimensionId == 1;

        return EndAnchor.DIMENSION_BLACKLIST == null
            || !EndAnchor.DIMENSION_BLACKLIST.contains(level.provider.dimensionId);
    }

    public static void charge(World level, int x, int y, int z) {
        int meta = level.getBlockMetadata(x, y, z);
        level.setBlockMetadataWithNotify(x, y, z, Math.min(meta + 1, MAX_CHARGES), 3);
        level.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 1.0F, 1.0F);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem();
        int meta = world.getBlockMetadata(x, y, z);

        if (isRespawnFuel(stack) && canBeCharged(meta)) {
            if (!world.isRemote) {
                charge(world, x, y, z);
            }

            if (!player.capabilities.isCreativeMode) {
                --stack.stackSize;

                if (stack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }

            return true;
        } else if (meta == 0) return false;
        else if (!canSetSpawn(world)) {
            if (!world.isRemote) {
                this.explode(world, x, y, z);
            }

            return true;
        } else {
            if (!world.isRemote && player instanceof EntityPlayerMP) {
                EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
                ChunkCoordinates spawn = serverPlayer.getBedLocation(serverPlayer.dimension);
                ChunkCoordinates target = new ChunkCoordinates(x, y, z);

                if (spawn == null || !spawn.equals(target)) {
                    serverPlayer.setSpawnChunk(target, true, world.provider.dimensionId);
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 1.0F, 1.0F);
                }
            }

            return true;
        }
    }

    private void explode(World world, int x, int y, int z) {
        world.setBlockToAir(x, y, z);
        world.createExplosion(null, x + 0.5D, y + 0.5D, z + 0.5D, 5.0F, true);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (world.getBlockMetadata(x, y, z) > 0 && isWaterAdjacent(world, x, y, z)) {
            this.explode(world, x, y, z);
        }
    }

    private static boolean isWaterAdjacent(World world, int x, int y, int z) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)
                .getMaterial() == Material.water) return true;
        }

        if (world.getBlock(x, y + 1, z)
            .getMaterial() == Material.water) return true;

        return false;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return Math.round(world.getBlockMetadata(x, y, z) / (float) MAX_CHARGES * 15F);
    }

    public static int getScaledChargeLevel(int meta, int scale) {
        return (int) Math.floor(meta / 4.0F * scale);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        return getScaledChargeLevel(world.getBlockMetadata(x, y, z), 15);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {
        if (!world.isRemote) {
            this.dropBlockAsItem(world, x, y, z, new ItemStack(this, 1, meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        int meta = world.getBlockMetadata(x, y, z);

        if (meta != 0) {
            if (random.nextInt(100) == 0) {
                world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "portal.portal", 1.0F, 1.0F, false);
            }

            double d0 = x + 0.1 + (random.nextDouble() - 0.1);
            double d1 = y + 0.8D;
            double d2 = z + 0.1 + (random.nextDouble() - 0.1);
            world.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] chargeIcons;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister registry) {
        this.chargeIcons = new IIcon[MAX_CHARGES + 1];

        for (int i = 0; i <= MAX_CHARGES; i++) {
            this.chargeIcons[i] = registry.registerIcon("enigmaticlegacy:block/end_anchor_" + i);
        }

        this.blockIcon = this.chargeIcons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (this.chargeIcons == null) return this.blockIcon;

        return this.chargeIcons[Math.max(0, Math.min(MAX_CHARGES, meta))];
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

}
