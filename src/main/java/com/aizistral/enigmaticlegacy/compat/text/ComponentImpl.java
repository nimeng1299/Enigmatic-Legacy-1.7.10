package com.aizistral.enigmaticlegacy.compat.text;

import java.util.Iterator;
import java.util.List;

import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ComponentImpl implements MutableComponent {

    private final IChatComponent delegate;
    private final Object contents;

    public ComponentImpl(IChatComponent delegate, Object contents) {
        this.delegate = delegate;
        this.contents = contents;
    }

    @Override
    public IChatComponent setChatStyle(ChatStyle style) {
        this.delegate.setChatStyle(style);
        return this;
    }

    @Override
    public ChatStyle getChatStyle() {
        return this.delegate.getChatStyle();
    }

    @Override
    public IChatComponent appendText(String text) {
        this.delegate.appendText(text);
        return this;
    }

    @Override
    public IChatComponent appendSibling(IChatComponent component) {
        this.delegate.appendSibling(component);
        return this;
    }

    @Override
    public String getUnformattedTextForChat() {
        return this.delegate.getUnformattedTextForChat();
    }

    @Override
    public String getUnformattedText() {
        return this.delegate.getUnformattedText();
    }

    @Override
    public String getFormattedText() {
        return this.delegate.getFormattedText();
    }

    @Override
    public List<IChatComponent> getSiblings() {
        return this.delegate.getSiblings();
    }

    @Override
    public IChatComponent createCopy() {
        return new ComponentImpl(this.delegate.createCopy(), this.contents);
    }

    @Override
    public Iterator<IChatComponent> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public String getString() {
        return this.delegate.getUnformattedText();
    }

    @Override
    public ChatStyle getStyle() {
        return this.delegate.getChatStyle();
    }

    @Override
    public Object getContents() {
        return this.contents;
    }

    @Override
    public MutableComponent copy() {
        return new ComponentImpl(this.delegate.createCopy(), this.contents);
    }

    @Override
    public MutableComponent withStyle(ChatStyle style) {
        this.delegate.setChatStyle(style);
        return this;
    }

    @Override
    public MutableComponent withStyle(EnumChatFormatting... formats) {
        ChatStyle style = this.delegate.getChatStyle();

        for (EnumChatFormatting format : formats) {
            if (format == EnumChatFormatting.RESET) {
                style = new ChatStyle();
            } else if (format.isColor()) {
                style.setColor(format);
            } else if (format == EnumChatFormatting.BOLD) {
                style.setBold(Boolean.TRUE);
            } else if (format == EnumChatFormatting.ITALIC) {
                style.setItalic(Boolean.TRUE);
            } else if (format == EnumChatFormatting.UNDERLINE) {
                style.setUnderlined(Boolean.TRUE);
            } else if (format == EnumChatFormatting.STRIKETHROUGH) {
                style.setStrikethrough(Boolean.TRUE);
            } else if (format == EnumChatFormatting.OBFUSCATED) {
                style.setObfuscated(Boolean.TRUE);
            }
        }

        this.delegate.setChatStyle(style);
        return this;
    }

    @Override
    public MutableComponent append(Component component) {
        this.delegate.appendSibling(component);
        return this;
    }

    @Override
    public MutableComponent append(IChatComponent component) {
        this.delegate.appendSibling(component);
        return this;
    }

    @Override
    public MutableComponent append(String text) {
        this.delegate.appendText(text);
        return this;
    }

}
