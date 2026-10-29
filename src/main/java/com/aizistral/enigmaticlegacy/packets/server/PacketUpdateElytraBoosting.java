package com.aizistral.enigmaticlegacy.packets.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;

import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;

public class PacketUpdateElytraBoosting {

    private boolean boosting;

    public PacketUpdateElytraBoosting(boolean boosting) {
        this.boosting = boosting;
    }

    public static void encode(PacketUpdateElytraBoosting msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.boosting);
    }

    public static PacketUpdateElytraBoosting decode(FriendlyByteBuf buf) {
        return new PacketUpdateElytraBoosting(buf.readBoolean());
    }

    public static void handle(PacketUpdateElytraBoosting msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get()
            .enqueueWork(() -> {
                EntityPlayerMP player = ctx.get()
                    .getSender();

                if (msg.boosting) {
                    player.worldObj.playSoundEffect(
                        player.posX,
                        player.posY,
                        player.posZ,
                        (SoundEvents.ENDER_EYE_LAUNCH).getName(),
                        3.0F,
                        1.0F);
                }

                TransientPlayerData data = TransientPlayerData.get(player);

                boolean wasBoosting = data.isElytraBoosting();
                data.setElytraBoosting(msg.boosting);

                if (wasBoosting != msg.boosting) {
                    data.syncToAllClients();
                }
            });
        ctx.get()
            .setPacketHandled(true);
    }

}
