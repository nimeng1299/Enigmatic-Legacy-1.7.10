package com.aizistral.enigmaticlegacy.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client-side screen shown once the player's world has been permanently lost. Mirrors the 1.20 screen: a dark
 * background with the permadeath title and message, plus a single button that returns to the main menu.
 */
@SideOnly(Side.CLIENT)
public class PermadeathScreen extends GuiScreen {

    private static final int BUTTON_RETURN = 0;
    private static final int TITLE_COLOR = 11184810;
    private static final int OVERLAY_COLOR = 0xB0000000;
    private static final int LINE_HEIGHT = 9;

    private final List<String> messageLines = new ArrayList<String>();
    private String title = "";
    private int textHeight;

    public PermadeathScreen() {
        // NO-OP
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.updateText();

        int buttonY = Math.min(this.height / 2 + this.textHeight / 2 + LINE_HEIGHT, this.height - 30);
        this.buttonList.add(
            new GuiButton(
                BUTTON_RETURN,
                this.width / 2 - 100,
                buttonY,
                200,
                20,
                StatCollector.translateToLocal("gui.enigmaticlegacy.toWorldList")));
    }

    @SuppressWarnings("unchecked")
    private void updateText() {
        this.title = StatCollector.translateToLocal("gui.enigmaticlegacy.permadeathTitle");
        this.messageLines.clear();
        this.messageLines.addAll(
            this.fontRendererObj.listFormattedStringToWidth(
                StatCollector.translateToLocal("message.enigmaticlegacy.permadeath"),
                this.width - 50));
        this.textHeight = this.messageLines.size() * LINE_HEIGHT;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        Gui.drawRect(0, 0, this.width, this.height, OVERLAY_COLOR);

        this.drawCenteredString(
            this.fontRendererObj,
            this.title,
            this.width / 2,
            this.height / 2 - this.textHeight / 2 - LINE_HEIGHT * 2,
            TITLE_COLOR);

        int lineY = this.height / 2 - this.textHeight / 2;

        for (String line : this.messageLines) {
            this.drawCenteredString(this.fontRendererObj, line, this.width / 2, lineY, 0xFFFFFF);
            lineY += LINE_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == BUTTON_RETURN) {
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        // The screen stays open until the button is pressed, so ESC must not dismiss it.
        if (keyCode != 1) {
            super.keyTyped(typedChar, keyCode);
        }
    }

}
