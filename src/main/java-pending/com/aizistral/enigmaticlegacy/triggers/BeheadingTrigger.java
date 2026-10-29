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

/**
 * Special trigger that activates if player successfully beheads a mob with Axe of Executioner.
 * @author Integral
 */

public class BeheadingTrigger extends SimpleCriterionTrigger<BeheadingTrigger.Instance> {
	public static final ResourceLocation ID = new ResourceLocation(EnigmaticLegacy.MODID, "forbidden_axe_beheading");
	public static final BeheadingTrigger INSTANCE = new BeheadingTrigger();

	private BeheadingTrigger() {}

	@Nonnull
	public ResourceLocation getId() {
		return BeheadingTrigger.ID;
	}

	@Nonnull
	public BeheadingTrigger.Instance createInstance(@Nonnull JsonObject json, @Nonnull ContextAwarePredicate playerPred, DeserializationContext conditions) {
		return new BeheadingTrigger.Instance(playerPred);
	}

	public void trigger(EntityPlayerMP player) {
		this.trigger(player, instance -> instance.test());
	}

	static class Instance extends AbstractCriterionTriggerInstance {
		Instance(ContextAwarePredicate playerPred) {
			super(BeheadingTrigger.ID, playerPred);
		}

		@Nonnull
		public ResourceLocation getCriterion() {
			return BeheadingTrigger.ID;
		}

		boolean test() {
			return true;
		}
	}

}