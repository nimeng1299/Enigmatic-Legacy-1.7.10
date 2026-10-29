package com.aizistral.enigmaticlegacy.gui.containers;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.packets.server.PacketInkwellField;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client side of the Architect's Inkwell GUI. Mirrors the 1.20 screen: the vanilla container background with a single
 * borderless text field overlaying the input slot, where the player types the name and the lore directives.
 */
@SideOnly(Side.CLIENT)
public class LoreInscriberScreen extends GuiContainer {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(
        EnigmaticLegacy.MODID,
        "textures/gui/inkwell_gui.png");
    private static final int TITLE_COLOR = 0x404040;
    private static final int FIELD_TEXT_COLOR = -1;
    private static final int FIELD_MAX_LENGTH = 128;

    private final LoreInscriberContainer container;
    private final EntityPlayer player;

    private GuiTextField nameField;
    private ItemStack lastInputStack;
    private String lastFieldText = "";

    public LoreInscriberScreen(Container container, EntityPlayer player) {
        super(container);
        this.container = (LoreInscriberContainer) container;
        this.player = player;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.nameField = new GuiTextField(this.fontRendererObj, this.guiLeft + 55, this.guiTop + 30, 95, 12);
        this.nameField.setCanLoseFocus(false);
        this.nameField.setTextColor(FIELD_TEXT_COLOR);
        this.nameField.setDisabledTextColour(FIELD_TEXT_COLOR);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(FIELD_MAX_LENGTH);
        this.nameField.setFocused(true);

        this.lastInputStack = null;
        this.lastFieldText = "";
        this.syncNameField();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.nameField.drawTextBox();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.nameField.updateCursorCounter();

        ItemStack input = this.getInputStack();

        if (input != this.lastInputStack) {
            this.lastInputStack = input;
            this.syncNameField();
        } else {
            String text = this.nameField.getText();

            if (!text.equals(this.lastFieldText)) {
                this.lastFieldText = text;
                this.container.updateItemName(text);
                EnigmaticLegacy.packetInstance.sendToServer(new PacketInkwellField(text));
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            // Same as the 1.20 screen, the field never consumes the close keys.
            this.player.closeScreen();
            return;
        }

        if (!this.nameField.textboxKeyTyped(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(GUI_TEXTURE);

        int i = this.guiLeft;
        int j = this.guiTop;
        boolean inputFilled = this.getInputStack() != null;

        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(i + 52, j + 26, 0, this.ySize + (inputFilled ? 0 : 16), 102, 16);

        if (inputFilled && !this.container.getSlot(1)
            .getHasStack()) {
            this.drawTexturedModalRect(i + 71, j + 49, this.xSize, 0, 28, 21);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = StatCollector.translateToLocal("gui.enigmaticlegacy.lore_inscriber");
        String inventory = StatCollector.translateToLocal("container.inventory");

        this.fontRendererObj.drawString(title, 52, 13, TITLE_COLOR);
        this.fontRendererObj.drawString(inventory, 8, this.ySize - 96 + 2, TITLE_COLOR);
    }

    /**
     * Mirrors the 1.20 slot listener: whenever the input slot contents change, the field displays the stack name and is
     * only editable while a stack is present.
     */
    private void syncNameField() {
        ItemStack input = this.getInputStack();

        this.nameField.setText(input != null ? LoreInscriberContainer.getPlainDisplayName(input) : "");
        this.nameField.setEnabled(input != null);
        this.nameField.setFocused(true);
        this.lastFieldText = this.nameField.getText();
    }

    private ItemStack getInputStack() {
        Slot input = this.container.getSlot(0);

        return input != null ? input.getStack() : null;
    }

}
