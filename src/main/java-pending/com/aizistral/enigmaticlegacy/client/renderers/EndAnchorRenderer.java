package com.aizistral.enigmaticlegacy.client.renderers;

import org.joml.Matrix4f;

import com.aizistral.enigmaticlegacy.blocks.TileEndAnchor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EndAnchorRenderer implements BlockEntityRenderer<TileEndAnchor> {

	public EndAnchorRenderer(BlockEntityRendererProvider.Context pContext) {
		// NO-OP
	}

	public void render(TileEndAnchor pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
		if (pBlockEntity.getBlockState().getValue(BlockStateProperties.RESPAWN_ANCHOR_CHARGES) > 0) {
			Matrix4f matrix4f = pPoseStack.last().pose();
			this.renderCube(pBlockEntity, matrix4f, pBufferSource.getBuffer(this.renderType()));
		}
	}

	private void renderCube(TileEndAnchor pBlockEntity, Matrix4f pPose, VertexConsumer pConsumer) {
		float f = this.getOffsetDown();
		float f1 = this.getOffsetUp();
		this.renderFace(pBlockEntity, pPose, pConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, ForgeDirection.SOUTH);
		this.renderFace(pBlockEntity, pPose, pConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, ForgeDirection.NORTH);
		this.renderFace(pBlockEntity, pPose, pConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, ForgeDirection.EAST);
		this.renderFace(pBlockEntity, pPose, pConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, ForgeDirection.WEST);
		this.renderFace(pBlockEntity, pPose, pConsumer, 0.0F, 1.0F, f, f, 0.0F, 0.0F, 1.0F, 1.0F, ForgeDirection.DOWN);
		this.renderFace(pBlockEntity, pPose, pConsumer, 0.1F, 0.9F, f1, f1, 0.9F, 0.9F, 0.1F, 0.1F, ForgeDirection.UP);
	}

	private void renderFace(TileEndAnchor pBlockEntity, Matrix4f pPose, VertexConsumer pConsumer, float pX0, float pX1, float pY0, float pY1, float pZ0, float pZ1, float pZ2, float pZ3, ForgeDirection pDirection) {
		if (pBlockEntity.shouldRenderFace(pDirection)) {
			pConsumer.vertex(pPose, pX0, pY0, pZ0).endVertex();
			pConsumer.vertex(pPose, pX1, pY0, pZ1).endVertex();
			pConsumer.vertex(pPose, pX1, pY1, pZ2).endVertex();
			pConsumer.vertex(pPose, pX0, pY1, pZ3).endVertex();
		}

	}

	protected float getOffsetUp() {
		return 1.0F - (1F / 16F);
	}

	protected float getOffsetDown() {
		return 0.375F;
	}

	protected RenderType renderType() {
		return RenderType.endPortal();
	}
}
