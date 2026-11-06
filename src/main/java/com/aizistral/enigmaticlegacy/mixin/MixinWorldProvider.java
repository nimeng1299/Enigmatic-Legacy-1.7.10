package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.blocks.BlockEndAnchor;

/**
 * 1.7.10 hardcodes the End (and the Nether) as "you cannot respawn here" - {@code respawnPlayer}
 * reroutes such deaths through {@code WorldProvider#getRespawnDimension}, which returns the
 * overworld for both. That is exactly the limitation the Dimensional Anchor exists to lift in
 * 1.20, where {@code MixinPlayerList} bypasses the whole vanilla respawn routine.
 *
 * <p>
 * This hook keeps the vanilla respawn routine intact and only answers "yes, this dimension is
 * fine" when the player actually has a charged anchor registered as their spawn point in this
 * very dimension.
 */
@Mixin(WorldProvider.class)
public class MixinWorldProvider {

    @Inject(
        method = "getRespawnDimension",
        at = @At("HEAD"),
        cancellable = true,
        // Forge-added method, absent from the vanilla obfuscation maps and never remapped.
        remap = false)
    private void enigmaticlegacy$anchorRespawnDimension(EntityPlayerMP player, CallbackInfoReturnable<Integer> info) {
        if (player == null) return;

        WorldProvider provider = (WorldProvider) (Object) this;
        int dimensionId = provider.dimensionId;

        if (dimensionId == 0) return;

        MinecraftServer server = MinecraftServer.getServer();

        if (server == null) return;

        WorldServer world = server.worldServerForDimension(dimensionId);

        if (world == null) return;

        ChunkCoordinates spawn = player.getBedLocation(dimensionId);

        if (spawn == null) return;

        if (!(world.getBlock(spawn.posX, spawn.posY, spawn.posZ) instanceof BlockEndAnchor)) return;
        if (world.getBlockMetadata(spawn.posX, spawn.posY, spawn.posZ) <= 0) return;
        if (!BlockEndAnchor.canSetSpawn(world)) return;

        info.setReturnValue(dimensionId);
    }

}
