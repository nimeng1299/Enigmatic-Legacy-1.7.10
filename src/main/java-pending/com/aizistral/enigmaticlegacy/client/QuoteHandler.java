package com.aizistral.enigmaticlegacy.client;

import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.gui.GUIUtils;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@SideOnly(Side.CLIENT)
public class QuoteHandler {
	public static final QuoteHandler INSTANCE = new QuoteHandler();
	private static final RandomSource RANDOM = RandomSource.create();
	private Quote currentQuote = null;
	private long startedPlaying = -1;
	private int delayTicks = -1;
	private boolean shownExperimentalInfo = false;

	private QuoteHandler() {
		// NO-OP
	}

	private double getPlayTime() {
		long millis = System.currentTimeMillis() - this.startedPlaying;
		return ((double)millis) / 1000;
	}

	public void playQuote(Quote quote, int delayTicks) {
		if (this.currentQuote == null) {
			this.currentQuote = quote;
			this.delayTicks = delayTicks;
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.player == Minecraft.getMinecraft().thePlayer) {
			if (this.delayTicks > 0 && !(Minecraft.getMinecraft().currentScreen instanceof LevelLoadingScreen)
					&& !(Minecraft.getMinecraft().currentScreen instanceof ReceivingLevelScreen)) {
				this.delayTicks--;

				if (this.delayTicks == 0) {
					SimpleSoundInstance instance = new SimpleSoundInstance(this.currentQuote.getSound().getLocation(),
							SoundSource.VOICE, 0.7F, 1, RANDOM, false, 0, SoundInstance.Attenuation.NONE, 0, 0, 0, true);

					Minecraft.getMinecraft().getSoundManager().play(instance);

					this.startedPlaying = System.currentTimeMillis();
				}
			}
		}
	}

	@SubscribeEvent
	public void onOverlayRender(RenderGuiEvent.Post event) {
		if (Minecraft.getMinecraft().currentScreen != null || this.currentQuote == null || this.delayTicks > 0)
			return;

		this.drawQuote(event.getGuiGraphics(), event.getWindow());
	}

	@SubscribeEvent
	public void onScreenRender(ScreenEvent.Render.Post event) {
		if (this.currentQuote != null && this.delayTicks <= 0) {
			this.drawQuote(event.getGuiGraphics(), Minecraft.getMinecraft().getWindow());
			Minecraft.getMinecraft().getSoundManager().resume();
		}
	}

	private void sendExperimentalInfo(EntityPlayer player) {
		//		if (!this.shownExperimentalInfo) {
		//			player.sendMessage(Component.translatable("message.enigmaticlegacy.voiceover_experimental")
		//					.setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent(
		//							Action.OPEN_URL, "https://discord.gg/fuWK8ns"))), Util.NIL_UUID);
		//
		//			this.shownExperimentalInfo = true;
		//		}
	}

	private void drawQuote(GuiGraphics graphics, Window window) {
		if (this.currentQuote.getSubtitles().getDuration() - this.getPlayTime() <= 0.1) {
			if (Quote.NARRATOR_INTROS.contains(this.currentQuote) && Minecraft.getMinecraft().thePlayer != null) {
				this.sendExperimentalInfo(Minecraft.getMinecraft().thePlayer);
			}

			this.currentQuote = null;
			this.startedPlaying = this.delayTicks = -1;
			return;
		}

		if (this.getPlayTime() < 0.05)
			return;

		if (OmniconfigHandler.disableQuoteSubtitles.getValue())
			return;

		Subtitles subtitles = this.currentQuote.getSubtitles();

		Font font = Minecraft.getMinecraft().fontRenderer;
		String[] text = SuperpositionHandler.wrapString(subtitles.getLine(this.getPlayTime()), font, 260);

		int alphaMod = 0xFF;

		if (this.getPlayTime() < 0.5) {
			alphaMod *= this.getPlayTime() / 0.5;
		} else if (this.currentQuote.getSubtitles().getDuration() - this.getPlayTime() < 0.5) {
			alphaMod *= (this.currentQuote.getSubtitles().getDuration() - this.getPlayTime()) / 0.5;
		}

		if (alphaMod < 0) {
			alphaMod = 0xFF;
		}

		PoseStack stack = graphics.pose();
		int width = window.getGuiScaledWidth() / 2 - SuperpositionHandler.greatestWidth(font, text) / 2;
		int height = window.getGuiScaledHeight() - 70 - ((font.lineHeight + 2) * (text.length - 1));

		stack.pushPose();
		stack.scale(1F, 1F, 1F);

		int fromX = width, fromY = height, toX = fromX + SuperpositionHandler.greatestWidth(font, text),
				toY = fromY + (font.lineHeight * text.length) + 2 * text.length - 1;
		int color1 = 0x000000 | (((int)(alphaMod * 0.266)) << 24);
		int color2 = EnumChatFormatting.YELLOW.getColor() | ((alphaMod) << 24);

		GUIUtils.drawGradientRect(stack.last().pose(), 0, fromX - 4, fromY - 4, toX + 4, toY + 4, color1, color1);
		GUIUtils.drawGradientRect(stack.last().pose(), 0, fromX - 6, fromY - 6, toX + 6, toY + 6, color1, color1);
		GUIUtils.drawGradientRect(stack.last().pose(), 0, fromX - 8, fromY - 8, toX + 8, toY + 8, color1, color1);

		int counter = 0;
		for (String line : text) {
			graphics.drawString(font, line, window.getGuiScaledWidth() / 2 - font.width(line) / 2, height +
					(counter * (font.lineHeight + 2)), color2, true);
			counter++;
		}

		stack.popPose();
	}


}
