package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.client.Quote;
import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Tells the client to play one of The Architect's quotes after a delay.
 */
public class PacketPlayQuote {

    private final int quoteID;
    private final int delayTicks;

    public PacketPlayQuote(Quote quote, int delayTicks) {
        this(quote.getID(), delayTicks);
    }

    public PacketPlayQuote(int quoteID, int delayTicks) {
        this.quoteID = quoteID;
        this.delayTicks = delayTicks;
    }

    public static void encode(PacketPlayQuote msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.quoteID);
        buf.writeInt(msg.delayTicks);
    }

    public static PacketPlayQuote decode(FriendlyByteBuf buf) {
        return new PacketPlayQuote(buf.readInt(), buf.readInt());
    }

    public static void handle(PacketPlayQuote msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handlePlayQuote(msg);
        }
        ctx.get()
            .setPacketHandled(true);
    }

    public int getQuoteID() {
        return this.quoteID;
    }

    public int getDelayTicks() {
        return this.delayTicks;
    }

}
