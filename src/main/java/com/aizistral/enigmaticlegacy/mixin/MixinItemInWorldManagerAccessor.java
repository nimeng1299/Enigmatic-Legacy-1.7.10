package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ItemInWorldManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes {@code ItemInWorldManager#thisPlayerMP} so the reach bonus injected by
 * {@link MixinItemInWorldManager} can tell whose charm to look at.
 */
@Mixin(ItemInWorldManager.class)
public interface MixinItemInWorldManagerAccessor {

    @Accessor("thisPlayerMP")
    EntityPlayerMP enigmaticlegacy$getThisPlayerMP();

}
