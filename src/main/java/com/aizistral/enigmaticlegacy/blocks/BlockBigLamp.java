package com.aizistral.enigmaticlegacy.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;

public class BlockBigLamp extends Block {

    public BlockBigLamp() {
        super(Material.glass);
        this.setHardness(3.5F);
        this.setResistance(3.5F);
        this.setLightLevel(1.0F);
        this.setStepSound(Block.soundTypeGlass);
        this.setBlockBounds(1F / 16F, 0F, 1F / 16F, 15F / 16F, 14F / 16F, 15F / 16F);
        this.setBlockTextureName(EnigmaticLegacy.MODID + ":big_lamp");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this));
        return drops;
    }

}
