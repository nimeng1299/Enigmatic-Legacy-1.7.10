package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the private sleep timer so the curse of insomnia can clamp it below the
 * fully-asleep threshold (100), mirroring the upstream {@code sleepCounter} clamp.
 * While it stays under 100, {@code WorldServer#areAllPlayersAsleep} never passes,
 * so the night is not skipped and the player simply lies in bed.
 */
@Mixin(EntityPlayer.class)
public interface MixinEntityPlayer {

    @Accessor("sleepTimer")
    int getSleepTimerAccess();

    @Accessor("sleepTimer")
    void setSleepTimer(int value);

}
