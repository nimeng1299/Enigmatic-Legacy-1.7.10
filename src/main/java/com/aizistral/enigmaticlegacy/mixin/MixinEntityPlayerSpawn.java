package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.blocks.BlockEndAnchor;
import com.aizistral.enigmaticlegacy.items.EndAnchor;
import com.aizistral.enigmaticlegacy.objects.AnchorSearchResult;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

/**
 * 1.7.10 only verifies beds (or, with {@code spawnForced}, an empty two-block column) when the
 * player respawns, so a spawn point set on the Dimensional Anchor is rejected with "your bed is
 * obstructed" and the player ends up at the world spawn. 1.20 sidesteps this because
 * {@code PlayerList#respawn} is replaced by {@code MixinPlayerList}, which asks
 * {@code EndAnchor#findEndAnchor}/{@code #useEndAnchor} instead of the vanilla spawn block check.
 *
 * <p>
 * {@code verifyRespawnCoordinates} is called from {@code ServerConfigurationManager#respawnPlayer}
 * only, which makes it the natural 1.7.10 counterpart of that hook: the anchor resolves to the
 * same stand-up position the 1.20 code searches for, and spends a charge (with
 * {@code EndAnchor.ChargeSaveChance} taken into account) exactly like {@code findAndUseEndAnchor}
 * does for a regular respawn.
 */
@Mixin(EntityPlayer.class)
public class MixinEntityPlayerSpawn {

    @Inject(method = "verifyRespawnCoordinates", at = @At("HEAD"), cancellable = true)
    private static void enigmaticlegacy$verifyEndAnchorSpawn(World world, ChunkCoordinates pos, boolean forced,
        CallbackInfoReturnable<ChunkCoordinates> info) {
        if (!(world instanceof WorldServer) || pos == null) return;

        Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);

        if (!(block instanceof BlockEndAnchor)) return;
        if (world.getBlockMetadata(pos.posX, pos.posY, pos.posZ) <= 0) return;
        if (!BlockEndAnchor.canSetSpawn(world)) return;

        ChunkCoordinates standUp = BlockEndAnchor.findStandUpPosition(world, pos.posX, pos.posY, pos.posZ);

        if (standUp == null) return;

        info.setReturnValue(standUp);

        AnchorSearchResult result = EndAnchor
            .findAndUseEndAnchor((WorldServer) world, new BlockPos(pos.posX, pos.posY, pos.posZ), 0.0F, forced, false);

        if (result.spentCharge()) {
            world.playSoundEffect(pos.posX + 0.5D, pos.posY + 0.5D, pos.posZ + 0.5D, "random.fizz", 1.0F, 0.6F);
        }
    }

}
