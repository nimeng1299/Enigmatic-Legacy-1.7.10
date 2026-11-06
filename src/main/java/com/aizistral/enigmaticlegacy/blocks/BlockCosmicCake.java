package com.aizistral.enigmaticlegacy.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCosmicCake extends BlockCake {

    /**
     * 1.20 counterpart of {@code BlockCosmicCake.AS_TASTY_AS}, which is {@code Foods.GOLDEN_CARROT}.
     */
    private static final int NUTRITION = 6;
    private static final float SATURATION_MODIFIER = 1.2F;

    /**
     * Min Z of the cake box per bite count, mirroring 1.20 {@code SHAPE_BY_BITE}
     * (1, 2.5, 4.5, 6.5, 8.5, 10.5 and 12 pixels). {@link BlockCake} shrinks the box along X
     * instead, which does not line up with the JSON models shipped for the cake.
     */
    private static final float[] BITE_MIN_Z = { 1F / 16F, 2.5F / 16F, 4.5F / 16F, 6.5F / 16F, 8.5F / 16F, 10.5F / 16F,
        12F / 16F };

    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    private IIcon iconInner;

    public BlockCosmicCake() {
        super();
        this.setTickRandomly(true);
        this.setBlockName("cosmic_cake");
        this.setBlockTextureName("enigmaticlegacy:cosmic_cake");
        this.setHardness(0.5F);
        // 1.20 cake is built from Blocks.CAKE, which uses SoundType.WOOL; 1.7.10 defaults to stone.
        this.setStepSound(Block.soundTypeCloth);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        return this.eatSlice(world, x, y, z, player);
    }

    /**
     * Vanilla 1.7.10 lets the player eat a slice by left clicking the cake. 1.20 {@code CakeBlock}
     * has no such behaviour, and inheriting it here would feed vanilla cake values (2 / 0.1) plus
     * skip the sound and particles.
     */
    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        // NO-OP
    }

    /**
     * Equivalent of 1.20 {@code BlockCosmicCake#eat}. A cake with all six slices taken cannot be
     * eaten any further, which is what makes it eternal - random ticks keep restoring it.
     */
    private boolean eatSlice(World world, int x, int y, int z, EntityPlayer player) {
        int bites = world.getBlockMetadata(x, y, z);

        if (bites > 5 || !player.canEat(false)) return false;

        player.getFoodStats()
            .addStats(NUTRITION, SATURATION_MODIFIER);
        world.playSoundAtEntity(player, SoundEvents.GENERIC_EAT.getName(), 1F, 0.5F + (float) Math.random() * 0.5F);
        world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(this) + (bites << 12));

        if (bites < 6) {
            world.setBlockMetadataWithNotify(x, y, z, bites + 1, 3);
        } else {
            world.setBlockToAir(x, y, z);
        }

        return true;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        int bites = world.getBlockMetadata(x, y, z);

        if (bites <= 0) return;

        world.setBlockMetadataWithNotify(x, y, z, bites - 1, 3);
        // 1.20 also spawns the destroy particles for the state that is being restored.
        world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(this) + (bites << 12));
        world.playSoundEffect(
            x + 0.5D,
            y + 0.5D,
            z + 0.5D,
            EnigmaticSounds.EAT_REVERSE.getName(),
            1F,
            0.5F + (float) Math.random() * 0.5F);
    }

    private static int clampBites(int bites) {
        return Math.max(0, Math.min(BITE_MIN_Z.length - 1, bites));
    }

    private static float minZFor(int bites) {
        return BITE_MIN_Z[clampBites(bites)];
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(1F / 16F, 0F, minZFor(world.getBlockMetadata(x, y, z)), 15F / 16F, 0.5F, 15F / 16F);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(1F / 16F, 0F, 1F / 16F, 15F / 16F, 0.5F, 15F / 16F);
    }

    /**
     * {@link BlockCake} builds its own axis aligned boxes with a X-based, 7/16 tall box and never
     * looks at {@link #setBlockBoundsBasedOnState}, so both have to be overridden to keep collision
     * and selection in sync with the model and with 1.20.
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        float minZ = minZFor(world.getBlockMetadata(x, y, z));

        return AxisAlignedBB.getBoundingBox(x + 1F / 16F, y, z + minZ, x + 15F / 16F, y + 0.5D, z + 15F / 16F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return this.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    /**
     * 1.20 {@code CakeBlock} feeds {@code BITES} into a comparator; 1.7.10 cakes do not by default.
     */
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        return world.getBlockMetadata(x, y, z);
    }

    /**
     * The cake is a multi element JSON model with one variant per bite count, so it has to go
     * through GTNHLib's model ISBRH instead of the legacy IIcon cube renderer.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    /**
     * {@link BlockCake#registerBlockIcons} asks for {@code enigmaticlegacy:cosmic_cake_*}, which
     * lives under {@code textures/blocks/block/}. The icons only remain in use as an item icon
     * fallback and to keep the texture atlas free of missing texture entries.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister registry) {
        this.blockIcon = registry.registerIcon("enigmaticlegacy:block/cosmic_cake_side");
        this.iconTop = registry.registerIcon("enigmaticlegacy:block/cosmic_cake_top");
        this.iconBottom = registry.registerIcon("enigmaticlegacy:block/cosmic_cake_bottom");
        this.iconInner = registry.registerIcon("enigmaticlegacy:block/cosmic_cake_inner");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) return this.iconTop;
        if (side == 0) return this.iconBottom;

        return meta > 0 && side == 4 ? this.iconInner : this.blockIcon;
    }

}
