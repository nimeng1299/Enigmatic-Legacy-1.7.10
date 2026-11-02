package com.aizistral.enigmaticlegacy.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMassiveLamp extends Block {

    public BlockMassiveLamp() {
        super(Material.glass);
        this.setHardness(3.5F);
        this.setResistance(3.5F);
        this.setLightLevel(1.0F);
        this.setStepSound(Block.soundTypeGlass);
        this.setBlockTextureName(EnigmaticLegacy.MODID + ":massive_lamp");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this));
        return drops;
    }

}
