package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Packet for setting arrows's rotations on client side.
 * 
 * @author Integral
 */

public class PacketForceArrowRotations {

    private int entityID;
    private double motionX, motionY, motionZ, posX, posY, posZ;
    private float rotationYaw;
    private float rotationPitch;

    public PacketForceArrowRotations(int entityID, float rotationYaw, float rotationPitch, double vecX, double vecY,
        double vecZ, double posX, double posY, double posZ) {
        this.entityID = entityID;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.motionX = vecX;
        this.motionY = vecY;
        this.motionZ = vecZ;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public static void encode(PacketForceArrowRotations msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityID);
        buf.writeFloat(msg.rotationYaw);
        buf.writeFloat(msg.rotationPitch);
        buf.writeDouble(msg.motionX);
        buf.writeDouble(msg.motionY);
        buf.writeDouble(msg.motionZ);
        buf.writeDouble(msg.posX);
        buf.writeDouble(msg.posY);
        buf.writeDouble(msg.posZ);
    }

    public static PacketForceArrowRotations decode(FriendlyByteBuf buf) {
        return new PacketForceArrowRotations(
            buf.readInt(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble());
    }

    public static void handle(PacketForceArrowRotations msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handleForceArrowRotations(msg);
        }
        ctx.get()
            .setPacketHandled(true);
    }

    public int getEntityID() {
        return this.entityID;
    }

    public double getMotionX() {
        return this.motionX;
    }

    public double getMotionY() {
        return this.motionY;
    }

    public double getMotionZ() {
        return this.motionZ;
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public float getRotationYaw() {
        return this.rotationYaw;
    }

    public float getRotationPitch() {
        return this.rotationPitch;
    }

}
