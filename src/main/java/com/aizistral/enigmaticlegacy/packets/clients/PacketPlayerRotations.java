package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Packet for setting player's rotations on client side.
 * 
 * @author Integral
 */

public class PacketPlayerRotations {

    private float rotationYaw;
    private float rotationPitch;
    private float rotationYawHead;

    public PacketPlayerRotations(float rotationYaw, float rotationPitch, float rotationYawHead) {
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.rotationYawHead = rotationYawHead;
    }

    public static void encode(PacketPlayerRotations msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.rotationYaw);
        buf.writeFloat(msg.rotationPitch);
        buf.writeFloat(msg.rotationYawHead);
    }

    public static PacketPlayerRotations decode(FriendlyByteBuf buf) {
        return new PacketPlayerRotations(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(PacketPlayerRotations msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handlePlayerRotations(msg);
        }
        ctx.get()
            .setPacketHandled(true);
    }

    public float getRotationYaw() {
        return this.rotationYaw;
    }

    public float getRotationPitch() {
        return this.rotationPitch;
    }

    public float getRotationYawHead() {
        return this.rotationYawHead;
    }

}
