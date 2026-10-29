package com.aizistral.enigmaticlegacy.packets.clients;

import java.util.function.Supplier;

import com.aizistral.enigmaticlegacy.handlers.EnigmaticUpdateHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent;

/**
 * Packet for handling update notification.
 * @author Integral
 */

public class PacketUpdateNotification {

	  public PacketUpdateNotification() {
	  }

	  public static void encode(PacketUpdateNotification msg, FriendlyByteBuf buf) {
	  }

	  public static PacketUpdateNotification decode(FriendlyByteBuf buf) {
	    return new PacketUpdateNotification();
	 }

	  public static void handle(PacketUpdateNotification msg, Supplier<NetworkEvent.Context> ctx) {

		    ctx.get().enqueueWork(() -> {
		      EntityPlayerSP player = Minecraft.getInstance().player;
		      EnigmaticUpdateHandler.handleShowup(player);
		      
		    });
		    ctx.get().setPacketHandled(true);
	  }

}


