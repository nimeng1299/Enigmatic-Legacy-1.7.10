package com.aizistral.enigmaticlegacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.ParticleTypes;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EnigmaticElytra;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.packets.server.PacketUpdateElytraBoosting;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client half of 1.20 {@code EnigmaticElytra#handleBoosting} / {@code onPlayerTickClient}: holding
 * jump while gliding pushes the wearer forward and tells the server to do the same for everyone
 * else, and every player flagged as boosting trails particles.
 * <p>
 * 1.7.10 has no dragon breath particle (it was added together with the 1.9 dragon rework), so the
 * trail uses witch magic, which reads closest to the original purple puff.
 */
@SideOnly(Side.CLIENT)
public class EnigmaticElytraHandler {

    private static final int PARTICLES_PER_TICK = 4;
    private static final double PARTICLE_SPREAD = 0.1D;

    private boolean boosting;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null || mc.thePlayer == null) {
            this.boosting = false;
            return;
        }

        this.handleBoosting(mc);
        this.renderBoostParticles(mc);
    }

    private void handleBoosting(Minecraft mc) {
        EntityPlayer player = mc.thePlayer;

        boolean wantsBoost = SuperpositionHandler.getEnigmaticElytra(player) != null
            && mc.gameSettings.keyBindJump.getIsKeyPressed()
            && EnigmaticElytra.boostPlayer(player);

        if (wantsBoost) {
            if (!this.boosting) {
                this.setBoosting(true);
            }
        } else if (this.boosting) {
            this.setBoosting(false);
        }
    }

    /**
     * The server only hears about transitions, same as 1.20.
     */
    private void setBoosting(boolean boosting) {
        this.boosting = boosting;
        EnigmaticLegacy.packetInstance.sendToServer(new PacketUpdateElytraBoosting(boosting));
    }

    private void renderBoostParticles(Minecraft mc) {
        for (Object candidate : mc.theWorld.playerEntities) {
            EntityPlayer player = (EntityPlayer) candidate;
            TransientPlayerData data = TransientPlayerData.get(player);

            if (!data.isElytraBoosting()) continue;

            if (!EnigmaticElytra.isFallFlying(player)) {
                // 1.20 drops the flag for remote players that stopped gliding; for the local player
                // the next jump press re-evaluates it.
                if (player != mc.thePlayer) {
                    data.setElytraBoosting(false);
                }

                continue;
            }

            for (int counter = 0; counter < PARTICLES_PER_TICK; counter++) {
                double x = player.posX + Math.random() - 0.5D;
                double y = player.posY + player.height / 2.0D - 1.0D + Math.random() - 0.5D;
                double z = player.posZ + Math.random() - 0.5D;

                player.worldObj.spawnParticle(
                    ParticleTypes.WITCH,
                    x,
                    y,
                    z,
                    (Math.random() - 0.5D) * 2.0D * PARTICLE_SPREAD,
                    (Math.random() - 0.5D) * 2.0D * PARTICLE_SPREAD,
                    (Math.random() - 0.5D) * 2.0D * PARTICLE_SPREAD);
            }
        }
    }

}
