package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

public class PacketPatchouliForce {

    public PacketPatchouliForce() {
        // NO-OP
    }

    public static void encode(PacketPatchouliForce msg, FriendlyByteBuf buf) {
        // NO-OP
    }

    public static PacketPatchouliForce decode(FriendlyByteBuf buf) {
        return new PacketPatchouliForce();
    }

    public static void handle(PacketPatchouliForce msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get()
            .enqueueWork(() -> {
                // Patchouli is not available on 1.7.10.
            });

        ctx.get()
            .setPacketHandled(true);
    }
}
