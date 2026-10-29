package com.aizistral.enigmaticlegacy.objects;

import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class WitherExplosion extends Explosion {

    private final boolean causesFire;
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    private final float size;

    public WitherExplosion(World worldIn, Entity exploderIn, double xIn, double yIn, double zIn, float sizeIn,
        boolean causesFireIn, boolean modeIgnored) {
        super(worldIn, exploderIn, xIn, yIn, zIn, sizeIn);

        this.world = worldIn;
        this.size = sizeIn;
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
        this.causesFire = causesFireIn;
        this.isFlaming = causesFireIn;
    }

    @Override
    public void doExplosionB(boolean spawnParticles) {
        if (this.world.isRemote) {
            this.world.playSoundEffect(
                this.x,
                this.y,
                this.z,
                "random.explode",
                4.0F,
                (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
        }

        if (spawnParticles) {
            if (this.size >= 2.0F) {
                this.world.spawnParticle("hugeexplosion", this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            } else {
                this.world.spawnParticle("explode", this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            }
        }

        if (this.isSmoking) {
            Collections.shuffle(this.affectedBlockPositions);

            for (net.minecraft.world.ChunkPosition pos : this.affectedBlockPositions) {
                Block block = this.world.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);

                if (!block.isAir(this.world, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ)) {
                    block.onBlockExploded(this.world, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, this);
                }
            }
        }

        if (this.causesFire) {
            for (net.minecraft.world.ChunkPosition pos : this.affectedBlockPositions) {
                if (this.world.rand.nextInt(3) == 0
                    && this.world.isAirBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ)
                    && this.world.getBlock(pos.chunkPosX, pos.chunkPosY - 1, pos.chunkPosZ)
                        .isOpaqueCube()) {
                    this.world.setBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, Blocks.fire);
                }
            }
        }
    }

}
