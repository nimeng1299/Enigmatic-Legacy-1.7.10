package com.aizistral.enigmaticlegacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class QuoteHandler {

    public static final QuoteHandler INSTANCE = new QuoteHandler();
    private Quote currentQuote = null;
    private long startedPlaying = -1;
    private int delayTicks = -1;

    private QuoteHandler() {
        // NO-OP
    }

    private double getPlayTime() {
        long millis = System.currentTimeMillis() - this.startedPlaying;
        return ((double) millis) / 1000;
    }

    public void playQuote(Quote quote, int delayTicks) {
        if (quote == null) return;

        if (this.currentQuote == null) {
            this.currentQuote = quote;
            this.delayTicks = Math.max(1, delayTicks);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;

        Subtitles.tick();

        if (this.currentQuote == null) return;

        if (this.delayTicks > 0 && !(Minecraft.getMinecraft().currentScreen instanceof GuiDownloadTerrain)) {
            this.delayTicks--;

            if (this.delayTicks == 0) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(
                        PositionedSoundRecord.func_147673_a(
                            this.currentQuote.getSound()
                                .getLocation()));

                this.startedPlaying = System.currentTimeMillis();
            }
        }
    }

    public String getCurrentSubtitle() {
        if (this.currentQuote == null || this.delayTicks > 0) return "";

        return this.currentQuote.getSubtitles()
            .getLine(this.getPlayTime());
    }

    public void drawQuote() {
        if (this.currentQuote == null || this.delayTicks > 0) return;

        if (this.currentQuote.getSubtitles()
            .getDuration() - this.getPlayTime() <= 0.1) {
            this.currentQuote = null;
            this.startedPlaying = this.delayTicks = -1;
            Subtitles.show("", 0);
            return;
        }

        if (this.getPlayTime() < 0.05) return;

        if (OmniconfigHandler.disableQuoteSubtitles.getValue()) return;

        Minecraft mc = Minecraft.getMinecraft();
        Subtitles subtitles = this.currentQuote.getSubtitles();

        String currentLine = subtitles.getLine(this.getPlayTime());
        Subtitles.show(currentLine, 2);

        String[] text = wrapString(currentLine, mc.fontRenderer, 260);

        int alphaMod = 0xFF;

        if (this.getPlayTime() < 0.5) {
            alphaMod *= this.getPlayTime() / 0.5;
        } else if (this.currentQuote.getSubtitles()
            .getDuration() - this.getPlayTime() < 0.5) {
                alphaMod *= (this.currentQuote.getSubtitles()
                    .getDuration() - this.getPlayTime()) / 0.5;
            }

        if (alphaMod < 0) {
            alphaMod = 0xFF;
        }

        ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int width = resolution.getScaledWidth() / 2 - greatestWidth(mc.fontRenderer, text) / 2;
        int height = resolution.getScaledHeight() - 70 - ((mc.fontRenderer.FONT_HEIGHT + 2) * (text.length - 1));

        int fromX = width, fromY = height, toX = fromX + greatestWidth(mc.fontRenderer, text),
            toY = fromY + (mc.fontRenderer.FONT_HEIGHT * text.length) + 2 * text.length - 1;
        int color1 = 0x000000 | (((int) (alphaMod * 0.266)) << 24);

        drawRect(fromX - 4, fromY - 4, toX + 4, toY + 4, color1);
        drawRect(fromX - 6, fromY - 6, toX + 6, toY + 6, color1);
        drawRect(fromX - 8, fromY - 8, toX + 8, toY + 8, color1);

        int yellow = 0xFFFF55;
        float fade = alphaMod / 255F;
        int color2 = ((int) ((yellow >> 16 & 255) * fade) << 16) | ((int) ((yellow >> 8 & 255) * fade) << 8)
            | (int) ((yellow & 255) * fade);

        int counter = 0;
        for (String line : text) {
            mc.fontRenderer.drawStringWithShadow(
                line,
                resolution.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(line) / 2,
                height + (counter * (mc.fontRenderer.FONT_HEIGHT + 2)),
                color2);
            counter++;
        }
    }

    private static String[] wrapString(String text, net.minecraft.client.gui.FontRenderer font, int maxWidth) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String word : text.split(" ")) {
            String candidate = current.length() == 0 ? word : current + " " + word;

            if (font.getStringWidth(candidate) > maxWidth && current.length() > 0) {
                lines.add(current.toString());
                current.setLength(0);
                current.append(word);
            } else {
                current.setLength(0);
                current.append(candidate);
            }
        }

        if (current.length() > 0) {
            lines.add(current.toString());
        }

        return lines.toArray(new String[0]);
    }

    private static int greatestWidth(net.minecraft.client.gui.FontRenderer font, String[] text) {
        int width = 0;

        for (String line : text) {
            width = Math.max(width, font.getStringWidth(line));
        }

        return width;
    }

    private static void drawRect(int left, int top, int right, int bottom, int color) {
        float alpha = (color >> 24 & 255) / 255F;
        float red = (color >> 16 & 255) / 255F;
        float green = (color >> 8 & 255) / 255F;
        float blue = (color & 255) / 255F;

        if (alpha <= 0) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(left, bottom, 0.0D);
        tessellator.addVertex(right, bottom, 0.0D);
        tessellator.addVertex(right, top, 0.0D);
        tessellator.addVertex(left, top, 0.0D);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
