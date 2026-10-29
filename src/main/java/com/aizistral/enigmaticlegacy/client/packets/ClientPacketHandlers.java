package com.aizistral.enigmaticlegacy.client.packets;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.capabilities.IPlaytimeCounter;
import com.aizistral.enigmaticlegacy.client.Quote;
import com.aizistral.enigmaticlegacy.client.QuoteHandler;
import com.aizistral.enigmaticlegacy.compat.ParticleTypes;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.AstralBreaker;
import com.aizistral.enigmaticlegacy.objects.SlotUnlockedToast;
import com.aizistral.enigmaticlegacy.packets.clients.PacketFlameParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketForceArrowRotations;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayQuote;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayerMotion;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayerRotations;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayerSetlook;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPortalParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketSlotUnlocked;
import com.aizistral.enigmaticlegacy.packets.clients.PacketSyncPlayTime;
import com.aizistral.enigmaticlegacy.packets.clients.PacketUpdateNotification;
import com.aizistral.enigmaticlegacy.packets.clients.PacketWitherParticles;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client-only bodies of packets that spawn particles or otherwise touch client
 * classes. The packet classes delegate here only when the message is received on
 * the client, so they stay loadable on a dedicated server.
 */
@SideOnly(Side.CLIENT)
public final class ClientPacketHandlers {

    private ClientPacketHandlers() {
        // NO-OP
    }

    public static void handleFlameParticles(PacketFlameParticles msg) {
        if (!AstralBreaker.flameParticlesToggle.getValue()) return;

        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                int amount = msg.getNum();
                float modifier = 1.0F;

                if (msg.getCheck()) {
                    if (Minecraft.getMinecraft().gameSettings.particleSetting == 2) {
                        modifier = 0.10F;
                    } else if (Minecraft.getMinecraft().gameSettings.particleSetting == 1) {
                        modifier = 0.25F;
                    } else {
                        modifier = 0.35F;
                    }
                }

                amount *= modifier;

                for (int counter = 0; counter <= amount; counter++) {
                    player.worldObj.spawnParticle(
                        ParticleTypes.FLAME,
                        msg.getX() + (Math.random() - 0.5),
                        msg.getY() + (Math.random() - 0.5),
                        msg.getZ() + (Math.random() - 0.5),
                        (Math.random() - 0.5D) * 0.1D,
                        (Math.random() - 0.5D) * 0.1D,
                        (Math.random() - 0.5D) * 0.1D);
                }

            });
    }

    public static void handleForceArrowRotations(PacketForceArrowRotations msg) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                World theWorld = Minecraft.getMinecraft().theWorld;
                Entity arrow = theWorld.getEntityByID(msg.getEntityID());

                if (arrow != null) {
                    arrow.getEntityData()
                        .setBoolean("enigmaticlegacy.redirected", true);

                    arrow.setPosition(msg.getPosX(), msg.getPosY(), msg.getPosZ());
                    arrow.motionX = msg.getMotionX();
                    arrow.motionY = msg.getMotionY();
                    arrow.motionZ = msg.getMotionZ();
                    arrow.rotationYaw = msg.getRotationYaw();
                    arrow.prevRotationYaw = msg.getRotationYaw();
                    arrow.rotationPitch = msg.getRotationPitch();
                    arrow.prevRotationPitch = msg.getRotationPitch();
                }

            });
    }

    public static void handlePlayerMotion(PacketPlayerMotion msg) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                player.motionX = msg.getX();
                player.motionY = msg.getY();
                player.motionZ = msg.getZ();

            });
    }

    public static void handlePlayerRotations(PacketPlayerRotations msg) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                player.rotationYaw = msg.getRotationYaw();
                player.rotationPitch = msg.getRotationPitch();
                player.rotationYawHead = msg.getRotationYawHead();

            });
    }

    public static void handlePlayerSetlook(PacketPlayerSetlook msg) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                SuperpositionHandler.lookAt(msg.getX(), msg.getY(), msg.getZ(), player);

            });
    }

    public static void handlePortalParticles(PacketPortalParticles msg) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                int amount = msg.getNum();

                if (msg.getCheck()) {
                    amount *= SuperpositionHandler.getParticleMultiplier();
                }

                for (int counter = 0; counter <= amount; counter++) {
                    player.worldObj.spawnParticle(
                        ParticleTypes.PORTAL,
                        msg.getX(),
                        msg.getY(),
                        msg.getZ(),
                        ((Math.random() - 0.5D) * 2.0D) * msg.getRangeModifier(),
                        ((Math.random() - 0.5D) * 2.0D) * msg.getRangeModifier(),
                        ((Math.random() - 0.5D) * 2.0D) * msg.getRangeModifier());
                }

            });
    }

    public static void handleRecallParticles(PacketRecallParticles msg) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                int amount = msg.getNum();

                if (msg.getCheck()) {
                    amount *= SuperpositionHandler.getParticleMultiplier();
                }

                for (int counter = 0; counter <= amount; counter++) {
                    player.worldObj.spawnParticle(
                        ParticleTypes.DRAGON_BREATH,
                        msg.getX(),
                        msg.getY(),
                        msg.getZ(),
                        (Math.random() - 0.5D) * 0.2D,
                        (Math.random() - 0.5D) * 0.2D,
                        (Math.random() - 0.5D) * 0.2D);
                }

            });
    }

    public static void handlePlayQuote(PacketPlayQuote msg) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                Quote quote = Quote.getByID(msg.getQuoteID());

                if (quote != null) {
                    QuoteHandler.INSTANCE.playQuote(quote, msg.getDelayTicks());
                }

            });
    }

    public static void handleSlotUnlocked(PacketSlotUnlocked msg) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                ItemStack stack;

                if (msg.getType()
                    .equals("ring")) stack = new ItemStack(EnigmaticItems.IRON_RING);
                else if (msg.getType()
                    .equals("scroll")) stack = new ItemStack(EnigmaticItems.THICC_SCROLL);
                else if (msg.getType()
                    .equals("spellstone")) stack = new ItemStack(EnigmaticItems.VOID_PEARL);
                else stack = new ItemStack(EnigmaticItems.IRON_RING);

                SlotUnlockedToast toast = new SlotUnlockedToast(stack, msg.getType());
                SuperpositionHandler.SCHEDULED_TOASTS.add(toast);
                SuperpositionHandler.DEFERRED_TOASTS.put(player, 5);

            });
    }

    public static void handleSyncPlayTime(PacketSyncPlayTime msg) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> syncPlayTime(msg.getPlayerID(), msg.getTimeWithCurses(), msg.getTimeWithoutCurses()));
    }

    public static void handleUpdateNotification(PacketUpdateNotification msg) {
        // Update notifications are not implemented on 1.7.10.
    }

    public static void handleWitherParticles(PacketWitherParticles msg) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {

                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                int amount = msg.getNum();

                if (msg.getCheck()) {
                    amount *= SuperpositionHandler.getParticleMultiplier();
                }

                if (msg.getMode() == 0) {
                    for (int counter = 0; counter <= amount; counter++) {
                        player.worldObj.spawnParticle(
                            ParticleTypes.LARGE_SMOKE,
                            msg.getX(),
                            msg.getY(),
                            msg.getZ(),
                            (Math.random() - 0.5D) * 0.2D,
                            (Math.random() - 0.5D) * 0.2D,
                            (Math.random() - 0.5D) * 0.2D);
                    }
                } else if (msg.getMode() == 1) {
                    for (int counter = 0; counter <= amount; counter++) {
                        player.worldObj.spawnParticle(
                            ParticleTypes.LARGE_SMOKE,
                            msg.getX() + (Math.random() - 0.5D),
                            msg.getY(),
                            msg.getZ() + (Math.random() - 0.5D),
                            0.0D,
                            0.0D,
                            0.0D);
                    }
                }

            });
    }

    private static void syncPlayTime(UUID playerID, long timeWithCurses, long timeWithoutCurses) {
        Minecraft.getMinecraft().theWorld.playerEntities.stream()
            .filter(
                player -> player.getUniqueID()
                    .equals(playerID))
            .findAny()
            .ifPresent(player -> {
                IPlaytimeCounter counter = IPlaytimeCounter.get(player);
                counter.setTimeWithCurses(timeWithCurses);
                counter.setTimeWithoutCurses(timeWithoutCurses);
            });
    }

}
