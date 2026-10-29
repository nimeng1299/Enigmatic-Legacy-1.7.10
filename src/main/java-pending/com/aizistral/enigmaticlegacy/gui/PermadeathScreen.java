package com.aizistral.enigmaticlegacy.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.text.MutableComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PermadeathScreen extends Screen {
	public static PermadeathScreen active = null;
	private final Component reason;
	private MultiLineLabel message = MultiLineLabel.EMPTY;
	private final Screen parent;
	private int textHeight;

	public PermadeathScreen(Screen parent, Component title, Component reason) {
		super(title);
		this.parent = parent;
		this.reason = reason;
	}

	public boolean shouldCloseOnEsc() {
		return false;
	}

	protected void init() {
		this.clearWidgets();

		this.message = MultiLineLabel.create(this.font, ((MutableComponent)this.reason).withStyle(EnumChatFormatting.WHITE), this.width - 50);
		this.textHeight = this.message.getLineCount() * 9;

		this.addRenderableWidget(Button.builder(Component.translatable("gui.enigmaticlegacy.toWorldList"), b -> {
			active = null;
			this.minecraft.setScreen(this.parent);
		}).bounds(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + 9, this.height - 30), 200, 20)
				.build());
	}

	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.init();

		this.renderBackground(graphics);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 -
				9 * 2, 11184810);
		this.message.renderCentered(graphics, this.width / 2, this.height / 2 - this.textHeight / 2);
		super.render(graphics, mouseX, mouseY, partialTick);
	}
}
