package com.aizistral.enigmaticlegacy.compat.text;

import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * Minimal backport of the modern text component API on top of 1.7.10 chat components.
 * Instances are usable anywhere an {@link IChatComponent} is expected.
 */
public interface Component extends IChatComponent {

    static MutableComponent literal(String text) {
        return new ComponentImpl(new net.minecraft.util.ChatComponentText(text), new LiteralContents(text));
    }

    static MutableComponent translatable(String key, Object... args) {
        return new ComponentImpl(
            new net.minecraft.util.ChatComponentTranslation(key, args),
            new TranslatableContents(key, args));
    }

    static MutableComponent empty() {
        return literal("");
    }

    static MutableComponent keybind(String key) {
        return translatable(key);
    }

    static Component nullToEmpty(String text) {
        return text == null || text.isEmpty() ? empty() : literal(text);
    }

    String getString();

    ChatStyle getStyle();

    Object getContents();

    MutableComponent copy();

    MutableComponent withStyle(ChatStyle style);

    MutableComponent withStyle(EnumChatFormatting... formats);

    MutableComponent append(Component component);

    MutableComponent append(IChatComponent component);

    MutableComponent append(String text);

}
