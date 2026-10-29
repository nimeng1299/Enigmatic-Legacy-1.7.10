package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Packet for displaying Curio slot unlock notification.
 * 
 * @author Integral
 */

public class PacketSlotUnlocked {

    private String type;

    public PacketSlotUnlocked(String type) {
        this.type = type;
    }

    public static void encode(PacketSlotUnlocked msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.type);
    }

    public static PacketSlotUnlocked decode(FriendlyByteBuf buf) {
        return new PacketSlotUnlocked(buf.readUtf());
    }

    public static void handle(PacketSlotUnlocked msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handleSlotUnlocked(msg);
        }
        ctx.get()
            .setPacketHandled(true);
    }

    public String getType() {
        return this.type;
    }

}
