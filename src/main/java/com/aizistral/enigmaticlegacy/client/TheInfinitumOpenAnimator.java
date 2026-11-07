package com.aizistral.enigmaticlegacy.client;

import net.minecraft.client.Minecraft;

import com.aizistral.enigmaticlegacy.items.TheInfinitum;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client half of the "book opens while held" animation of The Infinitum. 1.20 hooks this into the
 * {@code enigmaticlegacy:the_infinitum_open} item property and advances a per-player counter from
 * {@code EnigmaticEventHandler#onEntityTick}; 1.7.10 has neither, so the counter is driven from the
 * regular client tick instead and read back by {@link TheInfinitum#getIcon}.
 */
@SideOnly(Side.CLIENT)
public class TheInfinitumOpenAnimator {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        TheInfinitum.tickOpenStage(Minecraft.getMinecraft().thePlayer);
    }

}
