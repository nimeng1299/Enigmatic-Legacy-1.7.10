package com.aizistral.enigmaticlegacy.triggers;

import javax.annotation.Nonnull;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;

public class ForbiddenFruitTrigger extends SimpleCriterionTrigger<ForbiddenFruitTrigger.Instance> {
	public static final ResourceLocation ID = new ResourceLocation(EnigmaticLegacy.MODID, "consume_forbidden_fruit");
	public static final ForbiddenFruitTrigger INSTANCE = new ForbiddenFruitTrigger();

	private ForbiddenFruitTrigger() {}

	@Nonnull
	public ResourceLocation getId() {
		return ForbiddenFruitTrigger.ID;
	}

	@Nonnull
	public ForbiddenFruitTrigger.Instance createInstance(@Nonnull JsonObject json, @Nonnull ContextAwarePredicate playerPred, DeserializationContext conditions) {
		return new ForbiddenFruitTrigger.Instance(playerPred);
	}

	public void trigger(EntityPlayerMP player) {
		this.trigger(player, instance -> instance.test());
	}

	static class Instance extends AbstractCriterionTriggerInstance {
		Instance(ContextAwarePredicate playerPred) {
			super(ForbiddenFruitTrigger.ID, playerPred);
		}

		@Nonnull
		public ResourceLocation getCriterion() {
			return ForbiddenFruitTrigger.ID;
		}

		boolean test() {
			return true;
		}
	}

}
