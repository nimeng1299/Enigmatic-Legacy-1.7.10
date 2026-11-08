package com.aizistral.enigmaticlegacy.client.renderers;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Draws the "true name" line underneath an engraved Insignia nameplate.
 * <p>
 * Mirrors 1.20's {@code SuperpositionHandler#renderInsigniaNameplate}: the parenthesized true name is
 * rendered at {@code 0.4x} scale, ten screen pixels below the main label, without a backing plate.
 * The GL setup is the one vanilla's {@code Render#func_147906_a} uses for labels, so the small line
 * stays anchored to the exact same spot as the name it belongs to.
 */
@SideOnly(Side.CLIENT)
public final class InsigniaNameplateRenderer {

    /** 1.20 renders this line at 0.4x, ten pixels below the main name. */
    private static final float TRUE_NAME_SCALE = 0.4F;
    private static final float TRUE_NAME_OFFSET = 10.0F;
    /** Vanilla label transform: {@code 1.6F * 0.016666668F}. */
    private static final float LABEL_SCALE = 0.016666668F * 1.6F;
    /** Translucent white, the color vanilla labels use for their see-through pass. */
    private static final int LABEL_COLOR = 553648127;

    public static void renderTrueName(EntityLivingBase entity, String text, double x, double y, double z) {
        RenderManager rm = RenderManager.instance;
        FontRenderer font = rm.getFontRenderer();

        GL11.glPushMatrix();
        GL11.glTranslated(x, y + entity.height + 0.5D, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-LABEL_SCALE, -LABEL_SCALE, LABEL_SCALE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        GL11.glScalef(TRUE_NAME_SCALE, TRUE_NAME_SCALE, TRUE_NAME_SCALE);
        int lineOffset = (int) (TRUE_NAME_OFFSET / TRUE_NAME_SCALE);
        font.drawString(text, -font.getStringWidth(text) / 2, lineOffset, LABEL_COLOR);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private InsigniaNameplateRenderer() {
        // NO-OP
    }

}
