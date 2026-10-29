package com.aizistral.enigmaticlegacy.packets.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;

/**
 * Packet for toggling magnet rings effects.
 * 
 * @author Integral
 */

public class PacketToggleMagnetEffects {

    public PacketToggleMagnetEffects() {
        // NO-OP
    }

    public static void encode(PacketToggleMagnetEffects msg, FriendlyByteBuf buf) {
        // NO-OP
    }

    public static PacketToggleMagnetEffects decode(FriendlyByteBuf buf) {
        return new PacketToggleMagnetEffects();
    }

    public static void handle(PacketToggleMagnetEffects msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get()
            .enqueueWork(() -> {
                EntityPlayerMP playerServ = ctx.get()
                    .getSender();
                TransientPlayerData data = TransientPlayerData.get(playerServ);

                data.setDisabledMagnetRingEffects(!data.getDisabledMagnetRingEffects());
                data.syncToAllClients();
            });

        ctx.get()
            .setPacketHandled(true);
    }

}
