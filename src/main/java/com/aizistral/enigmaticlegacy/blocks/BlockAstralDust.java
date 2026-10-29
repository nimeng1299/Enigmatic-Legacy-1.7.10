package com.aizistral.enigmaticlegacy.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;

public class BlockAstralDust extends Block {

    public BlockAstralDust() {
        super(Material.cloth);
        this.setHardness(1F);
        this.setResistance(10F);
        this.setLightLevel(10F / 15F);
        this.setLightOpacity(0);
        this.setStepSound(Block.soundTypeCloth);
        this.setBlockTextureName(EnigmaticLegacy.MODID + ":astral_block");
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
