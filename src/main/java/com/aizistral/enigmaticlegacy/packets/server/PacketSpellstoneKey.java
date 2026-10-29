package com.aizistral.enigmaticlegacy.packets.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.api.items.ISpellstone;
import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

/**
 * Packet for triggering active ability of the spellstone.
 * 
 * @author Integral
 */

public class PacketSpellstoneKey {

    private boolean pressed;

    public PacketSpellstoneKey(boolean pressed) {
        this.pressed = pressed;

    }

    public static void encode(PacketSpellstoneKey msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.pressed);
    }

    public static PacketSpellstoneKey decode(FriendlyByteBuf buf) {
        return new PacketSpellstoneKey(buf.readBoolean());
    }

    public static void handle(PacketSpellstoneKey msg, Supplier<NetworkEvent.Context> ctx) {

        ctx.get()
            .enqueueWork(() -> {
                EntityPlayerMP playerServ = ctx.get()
                    .getSender();

                if (SuperpositionHandler.hasSpellstone(playerServ)) {
                    ItemStack spellstone = SuperpositionHandler.getSpellstone(playerServ);
                    ISpellstone function = (ISpellstone) spellstone.getItem();

                    function.triggerActiveAbility(playerServ.worldObj, playerServ, spellstone);
                }

            });
        ctx.get()
            .setPacketHandled(true);
    }

}
