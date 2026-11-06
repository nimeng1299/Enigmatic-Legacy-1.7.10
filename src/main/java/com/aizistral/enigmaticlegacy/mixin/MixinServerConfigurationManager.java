package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.helpers.AdvancedSpawnLocationHelper;

/**
 * 1.7.10 always respawns the player in the dimension they died in, so a spawn point bound to an
 * End Anchor is ignored unless the player happened to die in the End. 1.20 respawns at
 * {@code player.getRespawnDimension()}, i.e. the dimension stored together with the respawn point
 * ({@code MixinPlayerList#respawn}).
 *
 * <p>
 * Rather than reimplementing {@code respawnPlayer} (as the 1.20 mixin does), this simply re-enters
 * it once with the anchor's dimension; the second pass sees a spawn point in the requested
 * dimension and falls through to the untouched vanilla body. Everything else - bed spawns, the
 * "conquered the End" respawn and players without any spawn point - behaves exactly as before.
 */
@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager {

    @Inject(method = "respawnPlayer", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$respawnAtEndAnchor(EntityPlayerMP player, int dimension, boolean conqueredEnd,
        CallbackInfoReturnable<EntityPlayerMP> info) {
        if (player == null || conqueredEnd) return;

        // Whatever the player has in the dimension they died in keeps priority over the anchor.
        if (player.getBedLocation(dimension) != null) return;

        int anchorDimension = AdvancedSpawnLocationHelper.findEndAnchorSpawnDimension(player);

        if (anchorDimension == Integer.MIN_VALUE || anchorDimension == dimension) return;

        ServerConfigurationManager self = (ServerConfigurationManager) (Object) this;

        info.setReturnValue(self.respawnPlayer(player, anchorDimension, false));
    }

}
