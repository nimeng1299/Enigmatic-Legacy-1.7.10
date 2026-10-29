package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Lightweight block state stand-in; 1.7.10 blocks are identified by block + metadata.
 */
public class BlockState {

    private final Block block;
    private final int metadata;

    public BlockState(Block block, int metadata) {
        this.block = block;
        this.metadata = metadata;
    }

    public Block getBlock() {
        return this.block;
    }

    public int getMetadata() {
        return this.metadata;
    }

    public Material getMaterial() {
        return this.block.getMaterial();
    }

    public boolean isAir() {
        return this.block.getMaterial() == Material.air;
    }

    public boolean canOcclude() {
        return this.block.isOpaqueCube();
    }

    public float getDestroySpeed(net.minecraft.world.World world, com.gtnewhorizon.gtnhlib.blockpos.BlockPos pos) {
        return this.block.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean is(Block other) {
        return this.block == other;
    }

    public boolean is(TagKey<Block> tag) {
        return BlockTags.matches(this.block, tag);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockState)) return false;
        BlockState other = (BlockState) obj;
        return this.block == other.block && this.metadata == other.metadata;
    }

    @Override
    public int hashCode() {
        return Block.getIdFromBlock(this.block) * 31 + this.metadata;
    }

}
