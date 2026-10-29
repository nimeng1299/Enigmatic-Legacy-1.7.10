package com.aizistral.enigmaticlegacy.blocks;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.block.Block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

public class BlockStarFabric extends Block {

    public BlockStarFabric() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(1F, 10F)
                .lightLevel(arg -> 10).noOcclusion().sound(SoundType.WOOL));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(this));
    }

}
