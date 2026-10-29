package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 1.7.10 creatures with {@code isAIEnabled() == false} (zombie pigmen and endermen among them)
 * never run the task AI, so {@link EntityCreature#setAttackTarget} is a dead write for them.
 * Their hostility is driven exclusively by the legacy {@code entityToAttack} field, which
 * {@code EntityCreature#updateEntityActionState} consumes to pathfind and attack.
 */
@Mixin(EntityCreature.class)
public interface MixinEntityCreature {

    @Accessor("entityToAttack")
    Entity getLegacyAttackTarget();

    @Accessor("entityToAttack")
    void setLegacyAttackTarget(Entity target);

}
