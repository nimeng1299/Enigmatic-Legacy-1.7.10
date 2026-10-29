package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.UUID;
import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.capabilities.IPlaytimeCounter;

import net.minecraft.client.Minecraft;
import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

public class PacketSyncPlayTime {
	private UUID playerID;
	private long timeWithCurses, timeWithoutCurses;

	public PacketSyncPlayTime(UUID playerID, long timeWithCurses, long timeWithoutCurses) {
		this.playerID = playerID;
		this.timeWithCurses = timeWithCurses;
		this.timeWithoutCurses =  timeWithoutCurses;
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
		ctx.get().enqueueWork(() -> {
			handle(msg.playerID, msg.timeWithCurses, msg.timeWithoutCurses);
		});

		ctx.get().setPacketHandled(true);
	}

	@SideOnly(Side.CLIENT)
	private static void handle(UUID playerID, long timeWithCurses, long timeWithoutCurses) {
		Minecraft.getInstance().level.players().stream().filter(player -> player.getUniqueID().equals(playerID))
		.findAny().ifPresent(player -> {
			var counter = IPlaytimeCounter.get(player);
			counter.setTimeWithCurses(timeWithCurses);
			counter.setTimeWithoutCurses(timeWithoutCurses);
		});
	}
}

