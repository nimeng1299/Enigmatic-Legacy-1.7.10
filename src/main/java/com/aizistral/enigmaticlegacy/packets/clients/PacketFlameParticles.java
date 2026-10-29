package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.Random;
import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Packet for bursting out a bunch of flame breath particles.
 * 
 * @author Integral
 */

public class PacketFlameParticles {

    public static final Random random = new Random();

    private double x;
    private double y;
    private double z;
    private int num;
    private boolean check;

    public PacketFlameParticles(double x, double y, double z, int number, boolean checkSettings) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.num = number;
        this.check = checkSettings;
    }

    public static void encode(PacketFlameParticles msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeInt(msg.num);
        buf.writeBoolean(msg.check);
    }

    public static PacketFlameParticles decode(FriendlyByteBuf buf) {
        return new PacketFlameParticles(
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readInt(),
            buf.readBoolean());
    }

    public static void handle(PacketFlameParticles msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handleFlameParticles(msg);
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
