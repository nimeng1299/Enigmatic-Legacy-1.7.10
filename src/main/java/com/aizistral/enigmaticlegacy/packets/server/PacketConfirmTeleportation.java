package com.aizistral.enigmaticlegacy.packets.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EyeOfNebula;

/**
 * Packet for executing random teleportation.
 * Used to escape the event in which unnecessary checks take place.
 * 
 * @author Integral
 */

public class PacketConfirmTeleportation {

    private boolean pressed;

    public PacketConfirmTeleportation(boolean pressed) {
        this.pressed = pressed;
    }

    public static void encode(PacketConfirmTeleportation msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.pressed);
    }

    public static PacketConfirmTeleportation decode(FriendlyByteBuf buf) {
        return new PacketConfirmTeleportation(buf.readBoolean());
    }

    public static void handle(PacketConfirmTeleportation msg, Supplier<NetworkEvent.Context> ctx) {

        ctx.get()
            .enqueueWork(() -> {
                EntityPlayerMP playerServ = ctx.get()
                    .getSender();

                // System.out.println("Illuminati confirmed");

                for (int counter = 0; counter <= 32; counter++) {
                    if (SuperpositionHandler.validTeleportRandomly(
                        playerServ,
                        playerServ.worldObj,
                        (int) EyeOfNebula.dodgeRange.getValue())) {
                        break;
                    }
                }

            });
        ctx.get()
            .setPacketHandled(true);
    }

}
