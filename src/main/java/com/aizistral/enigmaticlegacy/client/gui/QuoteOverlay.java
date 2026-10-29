package com.aizistral.enigmaticlegacy.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import com.aizistral.enigmaticlegacy.client.QuoteHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class QuoteOverlay {

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (Minecraft.getMinecraft().currentScreen != null) return;

        QuoteHandler.INSTANCE.drawQuote();
    }

    @SubscribeEvent
    public void onScreenRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        QuoteHandler.INSTANCE.drawQuote();
    }

}
