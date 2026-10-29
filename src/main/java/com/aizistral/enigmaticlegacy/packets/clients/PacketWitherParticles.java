package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Packet for bursting out particles for UltimateWitherSkull.
 * 
 * @author Integral
 */

public class PacketWitherParticles {

    private double x;
    private double y;
    private double z;
    private int num;
    private boolean check;
    private int mode;

    public PacketWitherParticles(double x, double y, double z, int number, boolean checkSettings) {
        this(x, y, z, number, checkSettings, 0);
    }

    public PacketWitherParticles(double x, double y, double z, int number, boolean checkSettings, int mode) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.num = number;
        this.check = checkSettings;
        this.mode = mode;
    }

    public static void encode(PacketWitherParticles msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeInt(msg.num);
        buf.writeBoolean(msg.check);
        buf.writeInt(msg.mode);
    }

    public static PacketWitherParticles decode(FriendlyByteBuf buf) {
        return new PacketWitherParticles(
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readInt(),
            buf.readBoolean(),
            buf.readInt());
    }

    public static void handle(PacketWitherParticles msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handleWitherParticles(msg);
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

    public int getMode() {
        return this.mode;
    }

}
