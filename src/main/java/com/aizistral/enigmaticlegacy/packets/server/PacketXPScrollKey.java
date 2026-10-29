package com.aizistral.enigmaticlegacy.packets.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * Packet for handing the keybind of Scroll of Ageless Wisdom.
 * 
 * @author Integral
 */

public class PacketXPScrollKey {

    private boolean pressed;

    public PacketXPScrollKey(boolean pressed) {
        this.pressed = pressed;
    }

    public static void encode(PacketXPScrollKey msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.pressed);
    }

    public static PacketXPScrollKey decode(FriendlyByteBuf buf) {
        return new PacketXPScrollKey(buf.readBoolean());
    }

    public static void handle(PacketXPScrollKey msg, Supplier<NetworkEvent.Context> ctx) {

        ctx.get()
            .enqueueWork(() -> {
                EntityPlayerMP playerServ = ctx.get()
                    .getSender();

                if (SuperpositionHandler.hasCurio(playerServ, EnigmaticItems.XP_SCROLL)) {
                    ItemStack scroll = SuperpositionHandler.getCurioStack(playerServ, EnigmaticItems.XP_SCROLL);
                    EnigmaticItems.XP_SCROLL
                        .trigger(playerServ.worldObj, scroll, playerServ, InteractionHand.MAIN_HAND, false);
                }

            });
        ctx.get()
            .setPacketHandled(true);
    }

}
