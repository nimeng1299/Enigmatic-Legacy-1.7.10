package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import baubles.api.BaublesApi;

public class PacketSyncXPScroll {

    private int xpStored;
    private boolean active;
    private boolean absorptionMode;

    public PacketSyncXPScroll(int xpStored, boolean active, boolean absorptionMode) {
        this.xpStored = xpStored;
        this.active = active;
        this.absorptionMode = absorptionMode;
    }

    public static void encode(PacketSyncXPScroll msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.xpStored);
        buf.writeBoolean(msg.active);
        buf.writeBoolean(msg.absorptionMode);
    }

    public static PacketSyncXPScroll decode(FriendlyByteBuf buf) {
        return new PacketSyncXPScroll(buf.readInt(), buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(PacketSyncXPScroll msg, Supplier<NetworkEvent.Context> ctx) {

        ctx.get()
            .enqueueWork(() -> {
                EntityPlayer player = EnigmaticLegacy.PROXY.getClientPlayer();

                if (player == null) return;

                IInventory baubles = BaublesApi.getBaubles(player);

                if (baubles == null) return;

                for (int slot = 0; slot < baubles.getSizeInventory(); slot++) {
                    ItemStack stack = baubles.getStackInSlot(slot);

                    if (stack != null && stack.getItem() == EnigmaticItems.XP_SCROLL) {
                        ItemNBTHelper.setInt(stack, "XPStored", msg.xpStored);
                        ItemNBTHelper.setBoolean(stack, "IsActive", msg.active);
                        ItemNBTHelper.setBoolean(stack, "AbsorptionMode", msg.absorptionMode);
                        break;
                    }
                }
            });

        ctx.get()
            .setPacketHandled(true);
    }

}
