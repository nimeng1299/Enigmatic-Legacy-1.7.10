package com.aizistral.enigmaticlegacy.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.packets.server.PacketEnderRingKey;
import com.aizistral.enigmaticlegacy.packets.server.PacketSpellstoneKey;
import com.aizistral.enigmaticlegacy.packets.server.PacketXPScrollKey;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Class for creating and handling keybinds on the client.
 *
 * @author Integral
 */
@SideOnly(Side.CLIENT)
public class EnigmaticKeybindHandler {

    public KeyBinding enderRingKey;
    public KeyBinding spellstoneAbilityKey;
    public KeyBinding xpScrollKey;

    private boolean spaceDown = false;

    public EnigmaticKeybindHandler() {
        this.enderRingKey = new KeyBinding("key.enderRing", Keyboard.KEY_I, "key.categories.enigmaticLegacy");
        this.spellstoneAbilityKey = new KeyBinding(
            "key.spellstoneAbility",
            Keyboard.KEY_K,
            "key.categories.enigmaticLegacy");
        this.xpScrollKey = new KeyBinding("key.xpScroll", Keyboard.KEY_J, "key.categories.enigmaticLegacy");

        ClientRegistry.registerKeyBinding(this.enderRingKey);
        ClientRegistry.registerKeyBinding(this.spellstoneAbilityKey);
        ClientRegistry.registerKeyBinding(this.xpScrollKey);
    }

    @SubscribeEvent
    public void onKeyInput(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (event.phase != TickEvent.Phase.START || !Display.isActive() || minecraft.thePlayer == null) return;

        boolean spaceDown = minecraft.gameSettings.keyBindJump.getIsKeyPressed();
        boolean jumpClicked = false;

        if (this.spaceDown != spaceDown) {
            this.spaceDown = spaceDown;

            if (spaceDown) {
                jumpClicked = true;
            }
        }

        if (!OmniconfigHandler.angelBlessingDoubleJump.getValue()) {
            jumpClicked = false;
        }

        if (this.enderRingKey.isPressed()) {
            if (Display.isActive()) {
                EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketEnderRingKey(true));
            }
        }

        if (this.xpScrollKey.isPressed()) {
            EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketXPScrollKey(true));
        }

        if (this.spellstoneAbilityKey.getIsKeyPressed()
            && SuperpositionHandler.hasCurio(minecraft.thePlayer, EnigmaticItems.ENIGMATIC_ITEM)) {
            EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketSpellstoneKey(true));
        } else if (this.spellstoneAbilityKey.isPressed() && SuperpositionHandler.hasSpellstone(minecraft.thePlayer)) {
            EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketSpellstoneKey(true));
        } else if (jumpClicked) {
            EntityPlayerSP player = minecraft.thePlayer;

            if (!player.isInWater() && !player.onGround
                && !player.capabilities.isCreativeMode
                && SuperpositionHandler.hasCurio(player, EnigmaticItems.ANGEL_BLESSING)) {
                EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketSpellstoneKey(true));
            }
        }
    }

}
