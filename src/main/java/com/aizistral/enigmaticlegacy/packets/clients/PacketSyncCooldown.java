package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import net.minecraft.item.Item;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Notifies the client that an item cooldown was started (or cleared), so items
 * can display it in the inventory.
 */
public class PacketSyncCooldown {

    private final Item item;
    private final int ticks;

    public PacketSyncCooldown(Item item, int ticks) {
        this.item = item;
        this.ticks = ticks;
    }

    public static void encode(PacketSyncCooldown msg, FriendlyByteBuf buf) {
        String name = Item.itemRegistry.getNameForObject(msg.item);
        buf.writeUtf(name != null ? name : "");
        buf.writeVarInt(msg.ticks);
    }

    public static PacketSyncCooldown decode(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        Item item = name.isEmpty() ? null : (Item) Item.itemRegistry.getObject(name);
        return new PacketSyncCooldown(item, buf.readVarInt());
    }

    public static void handle(PacketSyncCooldown msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get()
            .getSide()
            .isClient()) {
            com.aizistral.enigmaticlegacy.client.packets.ClientPacketHandlers.handleSyncCooldown(msg);
        }
        ctx.get()
            .setPacketHandled(true);
    }

    public Item getItem() {
        return this.item;
    }

    public int getTicks() {
        return this.ticks;
    }

}
