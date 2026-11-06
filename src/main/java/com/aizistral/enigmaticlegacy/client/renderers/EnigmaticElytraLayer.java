package com.aizistral.enigmaticlegacy.client.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.items.EnigmaticElytra;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.client.renderer.entity.elytra.ModelElytra;

/**
 * 1.7.10 equivalent of 1.20 {@code EnigmaticElytraLayer}: draws the wings with the mod's own
 * texture instead of the vanilla one. Et Futurum already renders a wing layer for anything
 * {@code ItemArmorElytra#getElytra} reports as wearing an elytra, but it always binds the player's
 * cape texture when one exists and the vanilla elytra texture otherwise - so that layer is
 * suppressed for this item (see {@code MixinEtFuturumElytraLayer}) and the same model is drawn here
 * with {@code textures/models/misc/elytra.png}.
 * <p>
 * {@code RenderPlayerEvent.Specials.Post} still runs inside the player transform with the model
 * posed for the current frame, which is why it is used instead of a render layer.
 */
@SideOnly(Side.CLIENT)
public class EnigmaticElytraLayer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        EnigmaticLegacy.MODID,
        "textures/models/misc/elytra.png");

    private static final ModelElytra MODEL = new ModelElytra();

    @SubscribeEvent
    public void onRenderPlayerSpecials(RenderPlayerEvent.Specials.Post event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack stack = EnigmaticElytra.getActiveElytra(player);

        if (stack == null) return;

        float limbSwing = player.limbSwing;
        float limbSwingAmount = player.limbSwingAmount;
        float ageInTicks = player.getAge() + event.partialRenderTick;

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TEXTURE);

        // ModelElytra disables rescale-normal and culling without restoring them.
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glTranslatef(0.0F, 0.0F, 0.125F);

        MODEL.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, 0.0F, 0.0F, 0.0625F, player);
        MODEL.render(player, limbSwing, limbSwingAmount, ageInTicks, 0.0F, 0.0F, 0.0625F);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

}
