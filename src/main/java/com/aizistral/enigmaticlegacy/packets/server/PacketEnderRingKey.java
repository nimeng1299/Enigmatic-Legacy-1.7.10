package com.aizistral.enigmaticlegacy.packets.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;

import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * Packet for opening Ender Chest inventory to a player.
 * 
 * @author Integral
 */

public class PacketEnderRingKey {

    private boolean pressed;

    public PacketEnderRingKey(boolean pressed) {
        this.pressed = pressed;
    }

    public static void encode(PacketEnderRingKey msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.pressed);
    }

    public static PacketEnderRingKey decode(FriendlyByteBuf buf) {
        return new PacketEnderRingKey(buf.readBoolean());
    }

    public static void handle(PacketEnderRingKey msg, Supplier<NetworkEvent.Context> ctx) {

        ctx.get()
            .enqueueWork(() -> {
                EntityPlayerMP playerServ = ctx.get()
                    .getSender();

                if (SuperpositionHandler.hasCurio(playerServ, EnigmaticItems.ENDER_RING)
                    || SuperpositionHandler.hasCurio(playerServ, EnigmaticItems.CURSED_RING)) {
                    playerServ.displayGUIChest(playerServ.getInventoryEnderChest());
                    playerServ.worldObj.playSoundAtEntity(
                        playerServ,
                        (SoundEvents.ENDER_CHEST_OPEN).getName(),
                        1.0F,
                        (float) (0.8F + (Math.random() * 0.2)));
                }
            });
        ctx.get()
            .setPacketHandled(true);
    }

}
