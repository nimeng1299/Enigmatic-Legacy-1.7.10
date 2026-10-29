package com.aizistral.enigmaticlegacy.client.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.aizistral.enigmaticlegacy.api.items.IPermanentCrystal;
import com.aizistral.enigmaticlegacy.entities.PermanentItemEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Renders the item stack held by a {@link PermanentItemEntity}, mirroring
 * vanilla item entity rendering instead of showing a static soul crystal.
 */
@SideOnly(Side.CLIENT)
public class PermanentItemRenderer extends Render {

    private final RenderItem itemRenderer = new RenderItem();
    private final EntityItem proxy = new EntityItem(null);

    public PermanentItemRenderer() {
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!(entity instanceof PermanentItemEntity)) return;

        PermanentItemEntity permanentEntity = (PermanentItemEntity) entity;
        ItemStack stack = permanentEntity.getItem();

        if (stack == null || stack.getItem() == null) return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer != null && !mc.thePlayer.isEntityAlive()
            && mc.thePlayer.getDistanceSq(permanentEntity.posX, permanentEntity.posY, permanentEntity.posZ) <= 1.0D)
            return;

        this.proxy.setWorld(entity.worldObj);
        this.proxy.age = permanentEntity.getAge();
        this.proxy.hoverStart = permanentEntity.hoverStart;
        this.proxy.setEntityItemStack(stack);
        this.itemRenderer.setRenderManager(RenderManager.instance);

        if (stack.getItem() instanceof IPermanentCrystal) {
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            GL11.glScalef(1.25F, 1.25F, 1.25F);
            GL11.glTranslated(0.0D, -0.1125D, 0.0D);
            this.itemRenderer.doRender(this.proxy, 0.0D, 0.0D, 0.0D, yaw, partialTicks);
            GL11.glPopMatrix();
        } else {
            this.itemRenderer.doRender(this.proxy, x, y, z, yaw, partialTicks);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.locationItemsTexture;
    }

}
