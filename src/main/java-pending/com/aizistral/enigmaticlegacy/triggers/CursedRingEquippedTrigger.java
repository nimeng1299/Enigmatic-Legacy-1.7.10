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

public class CursedRingEquippedTrigger extends SimpleCriterionTrigger<CursedRingEquippedTrigger.Instance> {
	public static final ResourceLocation ID = new ResourceLocation(EnigmaticLegacy.MODID, "equip_cursed_ring");
	public static final CursedRingEquippedTrigger INSTANCE = new CursedRingEquippedTrigger();

	private CursedRingEquippedTrigger() {}

	@Nonnull
	public ResourceLocation getId() {
		return CursedRingEquippedTrigger.ID;
	}

	@Nonnull
	public CursedRingEquippedTrigger.Instance createInstance(@Nonnull JsonObject json, @Nonnull ContextAwarePredicate playerPred, DeserializationContext conditions) {
		return new CursedRingEquippedTrigger.Instance(playerPred);
	}

	public void trigger(EntityPlayerMP player) {
		this.trigger(player, instance -> instance.test());
	}

	static class Instance extends AbstractCriterionTriggerInstance {
		Instance(ContextAwarePredicate playerPred) {
			super(CursedRingEquippedTrigger.ID, playerPred);
		}

		@Nonnull
		public ResourceLocation getCriterion() {
			return CursedRingEquippedTrigger.ID;
		}

		boolean test() {
			return true;
		}
	}

}
