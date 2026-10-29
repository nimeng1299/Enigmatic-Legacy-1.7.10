package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.UUID;
import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

public class PacketSyncPlayTime {

    private UUID playerID;
    private long timeWithCurses, timeWithoutCurses;

    public PacketSyncPlayTime(UUID playerID, long timeWithCurses, long timeWithoutCurses) {
        this.playerID = playerID;
        this.timeWithCurses = timeWithCurses;
        this.timeWithoutCurses = timeWithoutCurses;
    }

    public static void encode(PacketSyncPlayTime msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerID);
        buf.writeLong(msg.timeWithCurses);
        buf.writeLong(msg.timeWithoutCurses);
    }

    public static PacketSyncPlayTime decode(FriendlyByteBuf buf) {
        return new PacketSyncPlayTime(buf.readUUID(), buf.readLong(), buf.readLong());
    }

    public static void handle(PacketSyncPlayTime msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handleSyncPlayTime(msg);
        }
        ctx.get()
            .setPacketHandled(true);
    }

    public UUID getPlayerID() {
        return this.playerID;
    }

    public long getTimeWithCurses() {
        return this.timeWithCurses;
    }

    public long getTimeWithoutCurses() {
        return this.timeWithoutCurses;
    }

}
