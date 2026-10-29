package com.aizistral.enigmaticlegacy.packets.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Packet for updating stored anvil field for targeted player on server side.
 * 
 * @author Integral
 */

public class PacketAnvilField {

    private String field;

    public PacketAnvilField(String field) {
        this.field = field;
    }

    public static void encode(PacketAnvilField msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.field);
    }

    public static PacketAnvilField decode(FriendlyByteBuf buf) {
        return new PacketAnvilField(buf.readUtf(128));
    }

    public static void handle(PacketAnvilField msg, Supplier<NetworkEvent.Context> ctx) {

        ctx.get()
            .enqueueWork(() -> {
                EntityPlayerMP playerServ = ctx.get()
                    .getSender();

                // EnigmaticEventHandler.anvilFields.put(playerServ, msg.field);

            });
        ctx.get()
            .setPacketHandled(true);
    }

}
