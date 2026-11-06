package com.aizistral.enigmaticlegacy.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
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
import com.gtnewhorizon.gtnhlib.blockstate.properties.IntegerBlockProperty;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 1.7.10 respawn anchor equivalent; charge is stored in block metadata (0-4).
 */
public class BlockEndAnchor extends Block {

    public static final int MIN_CHARGES = 0;
    public static final int MAX_CHARGES = 4;

    /** Metadata 0-4, exposed to the JSON model pipeline as the {@code charges} blockstate property. */
    public static final IntegerBlockProperty CHARGES = IntegerBlockProperty.meta("charges");

    /**
     * Horizontal offsets around the anchor, in the same order the 1.20 version walks them
     * ({@code BlockEndAnchor.RESPAWN_HORIZONTAL_OFFSETS}). The player is put on top of the
     * first candidate that has two blocks of headroom and a solid floor.
     */
    private static final int[][] STAND_UP_OFFSETS = { { 0, 0 }, { 0, -1 }, { -1, 0 }, { 0, 1 }, { 1, 0 }, { -1, -1 },
        { 1, -1 }, { -1, 1 }, { 1, 1 } };

    private static boolean dispenseBehaviorRegistered = false;

    public BlockEndAnchor() {
        super(Material.rock);
        this.setBlockName("end_anchor");
        this.setHardness(50F);
        this.setResistance(1200F);
        this.setStepSound(soundTypeStone);
        this.setHarvestLevel("pickaxe", 3);
        this.setLightLevel(0F);
        this.registerDispenseBehavior();
    }

    /**
     * Mirrors the dispenser behavior 1.20 registers in the block constructor: an ender pearl
     * shot into a chargeable anchor charges it instead of being thrown out.
     */
    private void registerDispenseBehavior() {
        if (dispenseBehaviorRegistered) return;

        dispenseBehaviorRegistered = true;

        IBehaviorDispenseItem previous = (IBehaviorDispenseItem) BlockDispenser.dispenseBehaviorRegistry
            .getObject(Items.ender_pearl);

        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.ender_pearl, new EndAnchorDispenseBehavior(previous));
    }

    public static class EndAnchorDispenseBehavior implements IBehaviorDispenseItem {

        private final IBehaviorDispenseItem fallback;

        public EndAnchorDispenseBehavior(IBehaviorDispenseItem fallback) {
            this.fallback = fallback;
        }

        @Override
        public ItemStack dispense(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            int x = source.getXInt();
            int y = source.getYInt();
            int z = source.getZInt();
            ForgeDirection facing = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
            int targetX = x + facing.offsetX;
            int targetY = y + facing.offsetY;
            int targetZ = z + facing.offsetZ;

            if (world.getBlock(targetX, targetY, targetZ) instanceof BlockEndAnchor) {
                int meta = world.getBlockMetadata(targetX, targetY, targetZ);

                if (!canBeCharged(meta)) return stack;

                if (!world.isRemote) {
                    charge(world, targetX, targetY, targetZ);
                }

                stack.splitStack(1);
                return stack;
            }

            return this.fallback != null ? this.fallback.dispense(source, stack) : stack;
        }

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

        return !isDimensionBlacklisted(level);
    }

    /**
     * 1.20 matches entries of {@code EndAnchorDimensionBlacklist} against dimension keys
     * ({@code minecraft:overworld} and the like). 1.7.10 has no dimension registry, so an
     * entry is accepted as numeric id, vanilla dimension key or provider name.
     */
    public static boolean isDimensionBlacklisted(World world) {
        if (EndAnchor.DIMENSION_BLACKLIST == null || EndAnchor.DIMENSION_BLACKLIST.isEmpty()) return false;

        int dimensionId = world.provider.dimensionId;
        String dimensionKey = getDimensionKey(dimensionId);
        String providerName = world.provider.getDimensionName();

        for (String entry : EndAnchor.DIMENSION_BLACKLIST) {
            if (entry == null) continue;

            String trimmed = entry.trim();

            if (trimmed.isEmpty()) continue;
            if (trimmed.equals(Integer.toString(dimensionId))) return true;
            if (dimensionKey != null && trimmed.equalsIgnoreCase(dimensionKey)) return true;
            if (providerName != null && trimmed.equalsIgnoreCase(providerName)) return true;
        }

        return false;
    }

    private static String getDimensionKey(int dimensionId) {
        switch (dimensionId) {
            case 0:
                return "minecraft:overworld";
            case -1:
                return "minecraft:the_nether";
            case 1:
                return "minecraft:the_end";
            default:
                return null;
        }
    }

    public static void charge(World level, int x, int y, int z) {
        int meta = level.getBlockMetadata(x, y, z);
        level.setBlockMetadataWithNotify(x, y, z, Math.min(meta + 1, MAX_CHARGES), 3);
        level.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 1.0F, 1.0F);
    }

    /**
     * Equivalent of 1.20 {@code BlockEndAnchor.findStandUpPosition}. Returns the feet position
     * of the first spot around (and on top of) the anchor that the player fits into, or null if
     * there is none.
     */
    public static ChunkCoordinates findStandUpPosition(World world, int x, int y, int z) {
        for (int deltaY = 1; deltaY <= 2; deltaY++) {
            for (int[] offset : STAND_UP_OFFSETS) {
                int posX = x + offset[0];
                int posY = y + deltaY;
                int posZ = z + offset[1];

                if (posY < 1 || posY > 254) continue;
                if (isStandUpSpace(world, posX, posY, posZ)) return new ChunkCoordinates(posX, posY, posZ);
            }
        }

        return null;
    }

    private static boolean isStandUpSpace(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z)
            .getMaterial()
            .blocksMovement()) return false;

        if (world.getBlock(x, y + 1, z)
            .getMaterial()
            .blocksMovement()) return false;

        return world.getBlock(x, y - 1, z)
            .getMaterial()
            .isSolid();
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

    /**
     * Not present in the 1.20 version (nor in the vanilla respawn anchor) - kept as-is from the
     * earlier 1.7.10 port, where a charged anchor detonates when water reaches it.
     */
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
        return getScaledChargeLevel(world.getBlockMetadata(x, y, z), 15);
    }

    public static int getScaledChargeLevel(int meta, int scale) {
        return (int) Math.floor(meta / (float) MAX_CHARGES * scale);
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
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {
        if (!world.isRemote) {
            // 1.20 always drops a plain item, charges are not carried over.
            this.dropBlockAsItem(world, x, y, z, new ItemStack(this));
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

    /**
     * The anchor is a multi-element JSON model (base frame plus the glowing inner rim), one model
     * per charge level, so it has to go through GTNHLib's model ISBRH instead of the legacy IIcon
     * cube renderer.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

}
