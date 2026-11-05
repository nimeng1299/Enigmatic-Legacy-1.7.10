package com.aizistral.etherium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.aizistral.etherium.items.EtheriumArmor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 1.7.10 equivalent of the modern {@code ShieldAuraLayer}: while the Etherium Armor shield is
 * active, the player is drawn a second time with an energy-swirl texture, slightly enlarged.
 * <p>
 * 1.20 does this through a {@code RenderLayer} plus {@code RenderType.energySwirl} (additive
 * blending with scrolling texture coordinates). 1.7.10 has no render-layer system, so the same
 * effect is reproduced from {@code RenderPlayerEvent.Specials.Post}, which still runs inside the
 * player's transform and leaves the model posed for the current frame. The parts are rendered
 * individually instead of calling {@code ModelBiped#render}, since the latter would call
 * {@code setRotationAngles} again and snap the model back to its default pose.
 */
@SideOnly(Side.CLIENT)
public class ShieldAuraLayer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        "enigmaticlegacy",
        "textures/models/misc/ultimate_wither_armor.png");

    @SubscribeEvent
    public void onRenderPlayerSpecials(RenderPlayerEvent.Specials.Post event) {
        EntityPlayer player = event.entityPlayer;

        if (!EtheriumArmor.hasShield(player)) return;

        RenderPlayer renderer = event.renderer;
        ModelBiped model = renderer.modelBipedMain;
        float ticks = player.ticksExisted + event.partialRenderTick;

        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TEXTURE);

        // Scroll the texture coordinates the same way RenderType.energySwirl does.
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glTranslated(MathHelper.cos(ticks * 0.02F) * 2.0F, ticks * 0.01F, 0.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        float scale = 1.05F;
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(0.0F, -(scale - 1.0F) / 2.0F, 0.0F);

        model.bipedHead.render(0.0625F);
        model.bipedHeadwear.render(0.0625F);
        model.bipedBody.render(0.0625F);
        model.bipedRightArm.render(0.0625F);
        model.bipedLeftArm.render(0.0625F);
        model.bipedRightLeg.render(0.0625F);
        model.bipedLeftLeg.render(0.0625F);

        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }

}
