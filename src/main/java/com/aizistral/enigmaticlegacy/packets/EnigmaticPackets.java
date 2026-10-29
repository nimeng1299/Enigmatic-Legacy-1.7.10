package com.aizistral.enigmaticlegacy.packets;

import com.aizistral.enigmaticlegacy.compat.network.SimpleChannel;
import com.aizistral.enigmaticlegacy.packets.clients.PacketCosmicRevive;
import com.aizistral.enigmaticlegacy.packets.clients.PacketFlameParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketForceArrowRotations;
import com.aizistral.enigmaticlegacy.packets.clients.PacketGenericParticleEffect;
import com.aizistral.enigmaticlegacy.packets.clients.PacketHandleItemPickup;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPatchouliForce;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPermadeath;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayQuote;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayerMotion;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayerRotations;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayerSetlook;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPortalParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketSetEntryState;
import com.aizistral.enigmaticlegacy.packets.clients.PacketSlotUnlocked;
import com.aizistral.enigmaticlegacy.packets.clients.PacketSyncPlayTime;
import com.aizistral.enigmaticlegacy.packets.clients.PacketSyncTransientData;
import com.aizistral.enigmaticlegacy.packets.clients.PacketUpdateCompass;
import com.aizistral.enigmaticlegacy.packets.clients.PacketUpdateExperience;
import com.aizistral.enigmaticlegacy.packets.clients.PacketUpdateNotification;
import com.aizistral.enigmaticlegacy.packets.clients.PacketWitherParticles;
import com.aizistral.enigmaticlegacy.packets.server.PacketAnvilField;
import com.aizistral.enigmaticlegacy.packets.server.PacketConfirmTeleportation;
import com.aizistral.enigmaticlegacy.packets.server.PacketEnchantingGUI;
import com.aizistral.enigmaticlegacy.packets.server.PacketEnderRingKey;
import com.aizistral.enigmaticlegacy.packets.server.PacketInkwellField;
import com.aizistral.enigmaticlegacy.packets.server.PacketSpellstoneKey;
import com.aizistral.enigmaticlegacy.packets.server.PacketToggleMagnetEffects;
import com.aizistral.enigmaticlegacy.packets.server.PacketUpdateElytraBoosting;
import com.aizistral.enigmaticlegacy.packets.server.PacketXPScrollKey;

/**
 * Registers all packets used by the mod onto the compatibility channel.
 */
public final class EnigmaticPackets {

    private EnigmaticPackets() {
        // NO-OP
    }

    public static void register(SimpleChannel channel) {
        channel.registerMessage(
            1,
            PacketCosmicRevive.class,
            PacketCosmicRevive::encode,
            PacketCosmicRevive::decode,
            PacketCosmicRevive::handle);
        channel.registerMessage(
            2,
            PacketFlameParticles.class,
            PacketFlameParticles::encode,
            PacketFlameParticles::decode,
            PacketFlameParticles::handle);
        channel.registerMessage(
            3,
            PacketForceArrowRotations.class,
            PacketForceArrowRotations::encode,
            PacketForceArrowRotations::decode,
            PacketForceArrowRotations::handle);
        channel.registerMessage(
            4,
            PacketGenericParticleEffect.class,
            PacketGenericParticleEffect::encode,
            PacketGenericParticleEffect::decode,
            PacketGenericParticleEffect::handle);
        channel.registerMessage(
            5,
            PacketHandleItemPickup.class,
            PacketHandleItemPickup::encode,
            PacketHandleItemPickup::decode,
            PacketHandleItemPickup::handle);
        channel.registerMessage(
            6,
            PacketPatchouliForce.class,
            PacketPatchouliForce::encode,
            PacketPatchouliForce::decode,
            PacketPatchouliForce::handle);
        channel.registerMessage(
            7,
            PacketPermadeath.class,
            PacketPermadeath::encode,
            PacketPermadeath::decode,
            PacketPermadeath::handle);
        channel.registerMessage(
            8,
            PacketPlayerMotion.class,
            PacketPlayerMotion::encode,
            PacketPlayerMotion::decode,
            PacketPlayerMotion::handle);
        channel.registerMessage(
            9,
            PacketPlayerRotations.class,
            PacketPlayerRotations::encode,
            PacketPlayerRotations::decode,
            PacketPlayerRotations::handle);
        channel.registerMessage(
            10,
            PacketPlayerSetlook.class,
            PacketPlayerSetlook::encode,
            PacketPlayerSetlook::decode,
            PacketPlayerSetlook::handle);
        channel.registerMessage(
            11,
            PacketPortalParticles.class,
            PacketPortalParticles::encode,
            PacketPortalParticles::decode,
            PacketPortalParticles::handle);
        channel.registerMessage(
            12,
            PacketRecallParticles.class,
            PacketRecallParticles::encode,
            PacketRecallParticles::decode,
            PacketRecallParticles::handle);
        channel.registerMessage(
            13,
            PacketSetEntryState.class,
            PacketSetEntryState::encode,
            PacketSetEntryState::decode,
            PacketSetEntryState::handle);
        channel.registerMessage(
            14,
            PacketSlotUnlocked.class,
            PacketSlotUnlocked::encode,
            PacketSlotUnlocked::decode,
            PacketSlotUnlocked::handle);
        channel.registerMessage(
            15,
            PacketSyncPlayTime.class,
            PacketSyncPlayTime::encode,
            PacketSyncPlayTime::decode,
            PacketSyncPlayTime::handle);
        channel.registerMessage(
            16,
            PacketSyncTransientData.class,
            PacketSyncTransientData::encode,
            PacketSyncTransientData::decode,
            PacketSyncTransientData::handle);
        channel.registerMessage(
            17,
            PacketUpdateCompass.class,
            PacketUpdateCompass::encode,
            PacketUpdateCompass::decode,
            PacketUpdateCompass::handle);
        channel.registerMessage(
            18,
            PacketUpdateExperience.class,
            PacketUpdateExperience::encode,
            PacketUpdateExperience::decode,
            PacketUpdateExperience::handle);
        channel.registerMessage(
            19,
            PacketUpdateNotification.class,
            PacketUpdateNotification::encode,
            PacketUpdateNotification::decode,
            PacketUpdateNotification::handle);
        channel.registerMessage(
            21,
            PacketWitherParticles.class,
            PacketWitherParticles::encode,
            PacketWitherParticles::decode,
            PacketWitherParticles::handle);
        channel.registerMessage(
            22,
            PacketPlayQuote.class,
            PacketPlayQuote::encode,
            PacketPlayQuote::decode,
            PacketPlayQuote::handle);

        channel.registerMessage(
            31,
            PacketAnvilField.class,
            PacketAnvilField::encode,
            PacketAnvilField::decode,
            PacketAnvilField::handle);
        channel.registerMessage(
            32,
            PacketConfirmTeleportation.class,
            PacketConfirmTeleportation::encode,
            PacketConfirmTeleportation::decode,
            PacketConfirmTeleportation::handle);
        channel.registerMessage(
            33,
            PacketEnchantingGUI.class,
            PacketEnchantingGUI::encode,
            PacketEnchantingGUI::decode,
            PacketEnchantingGUI::handle);
        channel.registerMessage(
            34,
            PacketEnderRingKey.class,
            PacketEnderRingKey::encode,
            PacketEnderRingKey::decode,
            PacketEnderRingKey::handle);
        channel.registerMessage(
            35,
            PacketInkwellField.class,
            PacketInkwellField::encode,
            PacketInkwellField::decode,
            PacketInkwellField::handle);
        channel.registerMessage(
            37,
            PacketSpellstoneKey.class,
            PacketSpellstoneKey::encode,
            PacketSpellstoneKey::decode,
            PacketSpellstoneKey::handle);
        channel.registerMessage(
            38,
            PacketToggleMagnetEffects.class,
            PacketToggleMagnetEffects::encode,
            PacketToggleMagnetEffects::decode,
            PacketToggleMagnetEffects::handle);
        channel.registerMessage(
            39,
            PacketUpdateElytraBoosting.class,
            PacketUpdateElytraBoosting::encode,
            PacketUpdateElytraBoosting::decode,
            PacketUpdateElytraBoosting::handle);
        channel.registerMessage(
            40,
            PacketXPScrollKey.class,
            PacketXPScrollKey::encode,
            PacketXPScrollKey::decode,
            PacketXPScrollKey::handle);
    }

}
