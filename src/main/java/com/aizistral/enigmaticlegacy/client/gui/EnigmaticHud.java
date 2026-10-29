package com.aizistral.enigmaticlegacy.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.ForbiddenFruit;
import com.aizistral.enigmaticlegacy.items.OceanStone;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client HUD elements that replace or hide parts of the vanilla overlay.
 */
@SideOnly(Side.CLIENT)
public class EnigmaticHud {

    @SubscribeEvent(receiveCanceled = true)
    public void onOverlayRender(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null) return;

        switch (event.type) {
            case EXPERIENCE:
                this.renderFireBar(mc, event);
                break;
            case AIR:
                this.handleAirBar(mc, event);
                break;
            case FOOD:
                this.handleFoodBar(mc, event);
                break;
            default:
                break;
        }
    }

    private void renderFireBar(Minecraft mc, RenderGameOverlayEvent.Pre event) {
        TransientPlayerData data = TransientPlayerData.get(mc.thePlayer);

        if (data.getFireImmunityTimer() <= 0
            || !SuperpositionHandler.hasCurio(mc.thePlayer, EnigmaticItems.BLAZING_CORE)) return;

        event.setCanceled(true);

        float barFiller = data.getFireImmunityFraction(event.partialTicks);
        int x = event.resolution.getScaledWidth() / 2 - 91;
        int y = event.resolution.getScaledHeight() - 32 + 3;

        mc.getTextureManager()
            .bindTexture(com.aizistral.enigmaticlegacy.handlers.EnigmaticEventHandler.FIREBAR_LOCATION);
        mc.ingameGUI.drawTexturedModalRect(x, y, 0, 0, 182, 5);

        int fill = (int) (barFiller * 183.0F);

        if (fill > 0) {
            mc.ingameGUI.drawTexturedModalRect(x, y, 0, 5, fill, 5);
        }

        String title = StatCollector.translateToLocal("gui.enigmaticlegacy.blazing_core_bar_title");
        int titleX = (event.resolution.getScaledWidth() - mc.fontRenderer.getStringWidth(title)) / 2;
        int titleY = event.resolution.getScaledHeight() - 31 - 4;

        try {
            titleX += Integer.parseInt(StatCollector.translateToLocal("gui.enigmaticlegacy.blazing_core_bar_offsetX"));
            titleY += Integer.parseInt(StatCollector.translateToLocal("gui.enigmaticlegacy.blazing_core_bar_offsetY"));
        } catch (Exception ex) {
            // NO-OP
        }

        int boundaryColor = 5832704;
        mc.fontRenderer.drawString(title, titleX + 1, titleY, boundaryColor);
        mc.fontRenderer.drawString(title, titleX - 1, titleY, boundaryColor);
        mc.fontRenderer.drawString(title, titleX, titleY + 1, boundaryColor);
        mc.fontRenderer.drawString(title, titleX, titleY - 1, boundaryColor);
        mc.fontRenderer.drawString(title, titleX, titleY, 16770638);
    }

    private void handleAirBar(Minecraft mc, RenderGameOverlayEvent.Pre event) {
        if (!OceanStone.preventOxygenBarRender.getValue()) return;

        if (SuperpositionHandler.hasCurio(mc.thePlayer, EnigmaticItems.OCEAN_STONE)
            || SuperpositionHandler.hasCurio(mc.thePlayer, EnigmaticItems.VOID_PEARL)
            || SuperpositionHandler.hasCurio(mc.thePlayer, EnigmaticItems.THE_CUBE)) {
            event.setCanceled(true);
        }
    }

    private void handleFoodBar(Minecraft mc, RenderGameOverlayEvent.Pre event) {
        ForbiddenFruit fruit = EnigmaticItems.FORBIDDEN_FRUIT;

        if (fruit == null || !fruit.haveConsumedFruit(mc.thePlayer)) return;

        if (!ForbiddenFruit.renderHungerBar.getValue()) {
            event.setCanceled(true);
            return;
        }

        if (mc.thePlayer.capabilities.isCreativeMode || !ForbiddenFruit.replaceHungerBar.getValue()) return;

        event.setCanceled(true);

        int left = event.resolution.getScaledWidth() / 2 + 91;
        int top = event.resolution.getScaledHeight() - 39;

        mc.getTextureManager()
            .bindTexture(com.aizistral.enigmaticlegacy.handlers.EnigmaticEventHandler.ICONS_LOCATION);

        for (int i = 0; i < 10; i++) {
            int x = left - i * 8 - 9;
            mc.ingameGUI.drawTexturedModalRect(x, top, 0, 0, 9, 9);
        }

        mc.getTextureManager()
            .bindTexture(new net.minecraft.util.ResourceLocation("textures/gui/icons.png"));
    }

}
