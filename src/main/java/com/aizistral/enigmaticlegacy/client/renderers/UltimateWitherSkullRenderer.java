package com.aizistral.enigmaticlegacy.client.renderers;

import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.aizistral.enigmaticlegacy.entities.UltimateWitherSkullEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Renders the ultimate wither skull as a 3D wither skull model, scaled up
 * while the skull is in its invulnerable phase.
 */
@SideOnly(Side.CLIENT)
public class UltimateWitherSkullRenderer extends Render {

    private static final ResourceLocation INVULNERABLE_WITHER_TEXTURES = new ResourceLocation(
        "textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation WITHER_TEXTURES = new ResourceLocation("textures/entity/wither/wither.png");

    private final ModelSkeletonHead skeletonHeadModel = new ModelSkeletonHead();

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!(entity instanceof UltimateWitherSkullEntity)) return;

        UltimateWitherSkullEntity skull = (UltimateWitherSkullEntity) entity;
        float inflate = skull.isSkullInvulnerable() ? 1.4F : 1.0F;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(-inflate, -inflate, inflate);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        float rotationYaw = this.interpolateRotation(skull.prevRotationYaw, skull.rotationYaw, partialTicks);
        float rotationPitch = skull.prevRotationPitch + (skull.rotationPitch - skull.prevRotationPitch) * partialTicks;

        this.bindEntityTexture(skull);
        this.skeletonHeadModel.render(skull, 0.0F, 0.0F, 0.0F, rotationYaw, rotationPitch, 0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return entity instanceof UltimateWitherSkullEntity && ((UltimateWitherSkullEntity) entity).isSkullInvulnerable()
            ? INVULNERABLE_WITHER_TEXTURES
            : WITHER_TEXTURES;
    }

    private float interpolateRotation(float prevRotation, float rotation, float partialTicks) {
        float delta = rotation - prevRotation;

        while (delta < -180.0F) {
            delta += 360.0F;
        }

        while (delta >= 180.0F) {
            delta -= 360.0F;
        }

        return prevRotation + partialTicks * delta;
    }

}
