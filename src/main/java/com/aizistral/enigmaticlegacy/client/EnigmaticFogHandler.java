package com.aizistral.enigmaticlegacy.client;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import org.lwjgl.opengl.GL11;

import com.aizistral.enigmaticlegacy.effects.MoltenHeartEffect;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.BlazingCore;
import com.aizistral.enigmaticlegacy.registries.EnigmaticEffects;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 1.7.10 hardcodes a fog density of 2.0 while the camera is inside lava.
 * Blazing Core and Molten Heart instead turn it into linear fog ending at the
 * configured distance, matching the modern implementation's fog end values.
 */
@SideOnly(Side.CLIENT)
public class EnigmaticFogHandler {

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (event.block == null || event.block.getMaterial() != Material.lava) return;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (player == null) return;

        double visibility;

        if (player.isPotionActive(EnigmaticEffects.MOLTEN_HEART)) {
            visibility = MoltenHeartEffect.lavafogDensity.getValue();
        } else if (SuperpositionHandler.hasCurio(player, EnigmaticItems.BLAZING_CORE)) {
            visibility = BlazingCore.lavafogDensity.getValue();
        } else {
            return;
        }

        if (visibility <= 0D) return;

        GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
        GL11.glFogf(GL11.GL_FOG_START, 0.0F);
        GL11.glFogf(GL11.GL_FOG_END, (float) visibility);
        event.setCanceled(true);
    }

}
