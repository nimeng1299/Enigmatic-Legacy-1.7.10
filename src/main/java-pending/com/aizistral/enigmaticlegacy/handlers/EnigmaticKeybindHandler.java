package com.aizistral.enigmaticlegacy.handlers;

import org.lwjgl.glfw.GLFW;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.packets.server.PacketEnderRingKey;
import com.aizistral.enigmaticlegacy.packets.server.PacketSpellstoneKey;
import com.aizistral.enigmaticlegacy.packets.server.PacketXPScrollKey;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;

/**
 * Class for creating and handling keybinds on the client.
 * @author Integral
 */

public class EnigmaticKeybindHandler {
	@SideOnly(Side.CLIENT)
	public static boolean checkVariable;

	public KeyBinding enderRingKey;
	public KeyBinding spellstoneAbilityKey;
	public KeyBinding xpScrollKey;

	private boolean spaceDown = false;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START || !Minecraft.getMinecraft().isWindowActive() || Minecraft.getMinecraft().thePlayer == null)
			return;

		boolean spaceDown = Minecraft.getMinecraft().gameSettings.keyJump.isDown();
		boolean jumpClicked = false;

		if (this.spaceDown != spaceDown) {
			this.spaceDown = spaceDown;
			if (spaceDown) {
				jumpClicked = true;
			}
		}

		if (Minecraft.getMinecraft().thePlayer.isFallFlying()) {
			jumpClicked = Minecraft.getMinecraft().gameSettings.keyJump.isDown();
		}

		if (!OmniconfigHandler.angelBlessingDoubleJump.getValue()) {
			jumpClicked = false;
		}

		if (this.enderRingKey.consumeClick()) {
			if (Minecraft.getMinecraft().isWindowActive()) {
				EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketEnderRingKey(true));
			}
		}

		if (this.xpScrollKey.consumeClick()) {
			EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketXPScrollKey(true));
		}

		if (this.spellstoneAbilityKey.isDown() && SuperpositionHandler.hasCurio(Minecraft.getMinecraft().thePlayer, EnigmaticItems.ENIGMATIC_ITEM)) {
			EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketSpellstoneKey(true));
		} else if (this.spellstoneAbilityKey.consumeClick() && SuperpositionHandler.hasSpellstone(Minecraft.getMinecraft().thePlayer)) {
			EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketSpellstoneKey(true));
		} else if (jumpClicked) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

			if (!player.isInWater() && !player.onGround() && !player.isCreative() && !player.isSpectator()
					&& SuperpositionHandler.hasCurio(player, EnigmaticItems.ANGEL_BLESSING)) {
				EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketSpellstoneKey(true));
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onLivingJump(LivingJumpEvent event) {
		if (event.getEntity() instanceof EntityPlayerSP player) {
			//this.suppressNextJumpClick = true;
		}
	}

}
