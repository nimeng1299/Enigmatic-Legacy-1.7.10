package com.aizistral.enigmaticlegacy.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.gtnewhorizon.gtnhlib.blockstate.properties.BooleanBlockProperty;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBigLamp extends Block {

    public static final BooleanBlockProperty HANGING = BooleanBlockProperty.flag("hanging", 1);

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
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
        int metadata) {
        return side == 0 ? 1 : 0;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        if (HANGING.getBooleanValue(world, x, y, z)) {
            this.setBlockBounds(1F / 16F, 1F / 16F, 1F / 16F, 15F / 16F, 15F / 16F, 15F / 16F);
        } else {
            this.setBlockBounds(1F / 16F, 0F, 1F / 16F, 15F / 16F, 14F / 16F, 15F / 16F);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this));
        return drops;
    }

}
