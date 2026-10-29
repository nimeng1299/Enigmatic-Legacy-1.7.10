package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Packet for bursting out a bunch of dragon breath particles.
 * 
 * @author Integral
 */

public class PacketRecallParticles {

    private double x;
    private double y;
    private double z;
    private int num;
    private boolean check;

    public PacketRecallParticles(double x, double y, double z, int number, boolean checkSettings) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.num = number;
        this.check = checkSettings;
    }

    public static void encode(PacketRecallParticles msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeInt(msg.num);
        buf.writeBoolean(msg.check);
    }

    public static PacketRecallParticles decode(FriendlyByteBuf buf) {
        return new PacketRecallParticles(
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readInt(),
            buf.readBoolean());
    }

    public static void handle(PacketRecallParticles msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handleRecallParticles(msg);
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

    public boolean getCheck() {
        return this.check;
    }

}
