package com.aizistral.etherium.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockEtherium extends Block {

    public BlockEtherium() {
        super(Material.iron);
        this.setHardness(5F);
        this.setResistance(1200F);
        this.setLightLevel(10F / 15F);
        this.setStepSound(Block.soundTypeMetal);
        this.setHarvestLevel("pickaxe", 3);
        this.setBlockTextureName("enigmaticlegacy:etherium_block");
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this));
        return drops;
    }

}
