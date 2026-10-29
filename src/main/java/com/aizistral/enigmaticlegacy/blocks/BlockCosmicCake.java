package com.aizistral.enigmaticlegacy.blocks;

import java.util.Random;

import net.minecraft.block.BlockCake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;

public class BlockCosmicCake extends BlockCake {

    public BlockCosmicCake() {
        super();
        this.setTickRandomly(true);
        this.setBlockName("cosmic_cake");
        this.setBlockTextureName("enigmaticlegacy:cosmic_cake");
        this.setHardness(0.5F);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        int bites = world.getBlockMetadata(x, y, z);

        if (bites > 5 || !player.canEat(false)) return false;

        player.getFoodStats()
            .addStats(6, 1.2F);
        world.playSoundAtEntity(player, SoundEvents.GENERIC_EAT.getName(), 1F, 0.5F + (float) Math.random() * 0.5F);
        world.playAuxSFX(2001, x, y, z, net.minecraft.block.Block.getIdFromBlock(this) + (bites << 12));

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

        if (bites > 0) {
            world.setBlockMetadataWithNotify(x, y, z, bites - 1, 3);
            world.playSoundEffect(
                x + 0.5D,
                y + 0.5D,
                z + 0.5D,
                EnigmaticSounds.EAT_REVERSE.getName(),
                1F,
                0.5F + (float) Math.random() * 0.5F);
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int bites = world.getBlockMetadata(x, y, z);

        switch (bites) {
            case 0:
                this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.5F, 0.9375F);
                break;
            case 1:
                this.setBlockBounds(0.0625F, 0.0F, 0.125F, 0.9375F, 0.5F, 0.9375F);
                break;
            case 2:
                this.setBlockBounds(0.0625F, 0.0F, 0.25F, 0.9375F, 0.5F, 0.9375F);
                break;
            case 3:
                this.setBlockBounds(0.0625F, 0.0F, 0.375F, 0.9375F, 0.5F, 0.9375F);
                break;
            case 4:
                this.setBlockBounds(0.0625F, 0.0F, 0.5F, 0.9375F, 0.5F, 0.9375F);
                break;
            case 5:
                this.setBlockBounds(0.0625F, 0.0F, 0.625F, 0.9375F, 0.5F, 0.9375F);
                break;
            default:
                this.setBlockBounds(0.0625F, 0.0F, 0.75F, 0.9375F, 0.5F, 0.9375F);
                break;
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.5F, 0.9375F);
    }

}
