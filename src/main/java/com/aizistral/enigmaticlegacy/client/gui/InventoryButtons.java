package com.aizistral.enigmaticlegacy.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EnderRing;
import com.aizistral.enigmaticlegacy.items.MagnetRing;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.packets.server.PacketEnderRingKey;
import com.aizistral.enigmaticlegacy.packets.server.PacketToggleMagnetEffects;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Adds the Ender Chest and magnet effect toggle buttons to the player inventory.
 */
@SideOnly(Side.CLIENT)
public class InventoryButtons {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        "enigmaticlegacy:textures/gui/ender_chest_button.png");

    @SubscribeEvent
    public void onInventoryGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        GuiScreen screen = event.gui;
        boolean creative = screen instanceof GuiContainerCreative;

        if (!(screen instanceof GuiInventory) && !creative) return;

        int guiLeft = (screen.width - (creative ? 195 : 176)) / 2;
        int guiTop = (screen.height - (creative ? 136 : 166)) / 2;

        EnderChestButton enderButton = new EnderChestButton(
            guiLeft + this.enderOffsetX(creative),
            guiTop + this.enderOffsetY(creative));

        MagnetButton magnetButton = new MagnetButton(
            guiLeft + this.magnetOffsetX(creative),
            guiTop + this.magnetOffsetY(creative));

        event.buttonList.add(enderButton);
        event.buttonList.add(magnetButton);
    }

    private int enderOffsetX(boolean creative) {
        return creative ? 170 + EnderRing.buttonOffsetXCreative.getValue() : 150 + EnderRing.buttonOffsetX.getValue();
    }

    private int enderOffsetY(boolean creative) {
        return creative ? 5 + EnderRing.buttonOffsetYCreative.getValue() : 61 + EnderRing.buttonOffsetY.getValue();
    }

    private int magnetOffsetX(boolean creative) {
        return creative ? 147 + MagnetRing.buttonOffsetXCreative.getValue() : 127 + MagnetRing.buttonOffsetX.getValue();
    }

    private int magnetOffsetY(boolean creative) {
        return creative ? 5 + MagnetRing.buttonOffsetYCreative.getValue() : 61 + MagnetRing.buttonOffsetY.getValue();
    }

    private static class EnderChestButton extends GuiButton {

        private EnderChestButton(int x, int y) {
            super(7001, x, y, 20, 18, "");
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            boolean hasRing = SuperpositionHandler.hasCurio(mc.thePlayer, EnigmaticItems.ENDER_RING)
                || SuperpositionHandler.hasCurio(mc.thePlayer, EnigmaticItems.CURSED_RING);

            this.visible = hasRing && EnderRing.inventoryButtonEnabled.getValue();
            this.enabled = this.visible;

            if (!this.visible) return;

            mc.getTextureManager()
                .bindTexture(TEXTURE);
            int v = this.isHovered(mouseX, mouseY) ? 19 : 0;
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, v, 20, 18);
        }

        private boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (!this.visible) return false;

            boolean pressed = super.mousePressed(mc, mouseX, mouseY);

            if (pressed) {
                EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketEnderRingKey(true));
            }

            return pressed;
        }

    }

    private static class MagnetButton extends GuiButton {

        private MagnetButton(int x, int y) {
            super(7002, x, y, 20, 18, "");
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            boolean hasRing = SuperpositionHandler.hasCurio(mc.thePlayer, EnigmaticItems.MAGNET_RING)
                || SuperpositionHandler.hasCurio(mc.thePlayer, EnigmaticItems.SUPER_MAGNET_RING);

            this.visible = hasRing && MagnetRing.inventoryButtonEnabled.getValue();
            this.enabled = this.visible;

            if (!this.visible) return;

            boolean disabled = TransientPlayerData.get(mc.thePlayer)
                .getDisabledMagnetRingEffects();
            int u = disabled ? 42 : 21;
            int v = this.isHovered(mouseX, mouseY) ? 19 : 0;

            mc.getTextureManager()
                .bindTexture(TEXTURE);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v, 20, 18);
        }

        private boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (!this.visible) return false;

            boolean pressed = super.mousePressed(mc, mouseX, mouseY);

            if (pressed) {
                EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketToggleMagnetEffects());
            }

            return pressed;
        }

    }

}
