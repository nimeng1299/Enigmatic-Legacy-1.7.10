package com.aizistral.enigmaticlegacy.blocks;

import com.aizistral.enigmaticlegacy.registries.EnigmaticTiles;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.aizistral.enigmaticlegacy.compat.BlockState;

public class TileEndAnchor extends BlockEntity {

	public TileEndAnchor(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileEndAnchor(BlockPos pos, BlockState state) {
		this(EnigmaticTiles.END_ANCHOR, pos, state);
	}

	public boolean shouldRenderFace(ForgeDirection face) {
		return face.getAxis() == ForgeDirection.Axis.Y;
	}

}
