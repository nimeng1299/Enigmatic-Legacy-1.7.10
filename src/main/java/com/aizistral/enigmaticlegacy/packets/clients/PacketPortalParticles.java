package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Packet for spawning a bunch of portal particles around specific point.
 * 
 * @author Integral
 */

public class PacketPortalParticles {

    private double x;
    private double y;
    private double z;
    private int num;
    private double rangeModifier;
    private boolean check;

    public PacketPortalParticles(double x, double y, double z, int number, double rangeModifier,
        boolean checkSettings) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.num = number;
        this.rangeModifier = rangeModifier;
        this.check = checkSettings;
    }

    public static void encode(PacketPortalParticles msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeInt(msg.num);
        buf.writeDouble(msg.rangeModifier);
        buf.writeBoolean(msg.check);
    }

    public static PacketPortalParticles decode(FriendlyByteBuf buf) {
        return new PacketPortalParticles(
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readInt(),
            buf.readDouble(),
            buf.readBoolean());
    }

    public static void handle(PacketPortalParticles msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handlePortalParticles(msg);
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

    public int getNum() {
        return this.num;
    }

    public double getRangeModifier() {
        return this.rangeModifier;
    }

    public boolean getCheck() {
        return this.check;
    }

}
