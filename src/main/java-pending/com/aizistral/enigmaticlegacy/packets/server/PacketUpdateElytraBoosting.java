package com.aizistral.enigmaticlegacy.packets.server;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;

import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

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
		ctx.get().enqueueWork(() -> {
			EntityPlayerMP player = ctx.get().getSender();

			if (msg.boosting) {
				player.worldObj.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.AMBIENT, 3.0F, 1.0F);
			}

			TransientPlayerData data = TransientPlayerData.get(player);

			boolean wasBoosting = data.isElytraBoosting();
			data.setElytraBoosting(msg.boosting);

			if (wasBoosting != msg.boosting) {
				data.syncToAllClients();
			}
		});
		ctx.get().setPacketHandled(true);
	}

}
