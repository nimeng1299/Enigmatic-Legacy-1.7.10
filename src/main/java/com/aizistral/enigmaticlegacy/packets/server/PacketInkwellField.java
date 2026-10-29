package com.aizistral.enigmaticlegacy.packets.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;
import com.aizistral.enigmaticlegacy.gui.containers.LoreInscriberContainer;

/**
 * Packet for updating stored anvil field for targeted player on server side.
 * 
 * @author Integral
 */

public class PacketInkwellField {

    private String field;

    public PacketInkwellField(String field) {
        this.field = field;
    }

    public static void encode(PacketInkwellField msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.field);
    }

    public static PacketInkwellField decode(FriendlyByteBuf buf) {
        return new PacketInkwellField(buf.readUtf(128));
    }

    public static void handle(PacketInkwellField msg, Supplier<NetworkEvent.Context> ctx) {

        ctx.get()
            .enqueueWork(() -> {
                EntityPlayerMP playerServ = ctx.get()
                    .getSender();

                if (playerServ != null && playerServ.openContainer instanceof LoreInscriberContainer) {
                    ((LoreInscriberContainer) playerServ.openContainer).updateItemName(msg.field);
                }
            });
        ctx.get()
            .setPacketHandled(true);
    }

}
