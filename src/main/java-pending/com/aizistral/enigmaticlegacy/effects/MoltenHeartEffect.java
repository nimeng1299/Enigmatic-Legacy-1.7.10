package com.aizistral.enigmaticlegacy.effects;

import java.util.List;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.ImmutableList;

import com.aizistral.enigmaticlegacy.compat.ResourceKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import com.aizistral.enigmaticlegacy.compat.DamageType;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import net.minecraft.potion.Potion;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.entity.EntityLivingBase;

public class MoltenHeartEffect extends Potion {
	private static final List<ResourceKey<DamageType>> IMMUNITIES = ImmutableList.of(DamageTypes.LAVA,
			DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.HOT_FLOOR);

	public static Omniconfig.DoubleParameter lavafogDensity;

	@SubscribeConfig(receiveClient = true)
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("MoltenHeart");

		if (builder.config.getSidedType() == Configuration.SidedConfigType.CLIENT) {
			lavafogDensity = builder
					.comment("Controls how obscured your vision is in lava when Molten Heart effect is active. Higher value equals more visibility.")
					.max(1024)
					.clientOnly()
					.getDouble("LavaDensity", 6);
		}

		builder.popPrefix();
	}

	public MoltenHeartEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xF28E0C);
	}

	@Override
	public void applyEffectTick(EntityLivingBase living, int amplifier) {
		if (living.isOnFire()) {
			living.clearFire();
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	public boolean providesImmunity(DamageSource source) {
		return IMMUNITIES.stream().anyMatch(source::is);
	}

}

