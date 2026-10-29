package com.aizistral.enigmaticlegacy.compat.text;

import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public interface MutableComponent extends Component {

    @Override
    MutableComponent copy();

    @Override
    MutableComponent withStyle(ChatStyle style);

    @Override
    MutableComponent withStyle(EnumChatFormatting... formats);

    @Override
    MutableComponent append(Component component);

    @Override
    MutableComponent append(IChatComponent component);

    @Override
    MutableComponent append(String text);

}
