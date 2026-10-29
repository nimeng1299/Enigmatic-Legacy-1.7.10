package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Packet for setting the player to look at specific point in space,
 * defined by absolute x/y/z coordinates.
 * 
 * @author Integral
 */

public class PacketPlayerSetlook {

    private double x;
    private double y;
    private double z;

    public PacketPlayerSetlook(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(PacketPlayerSetlook msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
    }

    public static PacketPlayerSetlook decode(FriendlyByteBuf buf) {
        return new PacketPlayerSetlook(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handle(PacketPlayerSetlook msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handlePlayerSetlook(msg);
        }
        ctx.get()
            .setPacketHandled(true);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

}
