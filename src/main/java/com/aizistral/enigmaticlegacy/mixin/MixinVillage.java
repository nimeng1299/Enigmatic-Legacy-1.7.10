package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.village.Village;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.aizistral.enigmaticlegacy.handlers.EnigmaticEventHandler;

/**
 * 1.20 blocks the iron golems a village spawns on its own through
 * {@code MixinVillager#onGolemCreation}, which cancels {@code Villager#spawnGolemIfNeeded} whenever
 * the villager sits inside a desolation box. In 1.7.10 golem creation lives in {@code Village} and
 * calls {@link World#spawnEntityInWorld} directly, so it never passes through
 * {@code LivingSpawnEvent.CheckSpawn} and is therefore not covered by
 * {@code EnigmaticEventHandler#onEntitySpawn}. Redirecting that single call keeps the behaviour of
 * the desolation ring identical: village golems are suppressed, while golems a player builds out of
 * pumpkin and iron blocks are still spawned normally.
 */
@Mixin(Village.class)
public class MixinVillage {

    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z"))
    private boolean enigmaticlegacy$blockDesolationGolem(World world, Entity entity) {
        if (entity instanceof EntityIronGolem) {
            for (AxisAlignedBB box : EnigmaticEventHandler.DESOLATION_BOXES.values()) {
                if (box.intersectsWith(entity.boundingBox)) return false;
            }
        }

        return world.spawnEntityInWorld(entity);
    }

}
