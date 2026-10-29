package com.aizistral.enigmaticlegacy.effects;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.compat.DamageType;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import com.aizistral.enigmaticlegacy.compat.PotionIds;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.ImmutableList;

public class MoltenHeartEffect extends Potion {

    private static final List<DamageType> IMMUNITIES = ImmutableList
        .of(DamageTypes.LAVA, DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.HOT_FLOOR);

    public static Omniconfig.DoubleParameter lavafogDensity;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("MoltenHeart");

        if (builder.config.getSidedType() == Configuration.SidedConfigType.CLIENT) {
            lavafogDensity = builder.comment(
                "Controls how obscured your vision is in lava when Molten Heart effect is active. Higher value equals more visibility.")
                .max(1024)
                .clientOnly()
                .getDouble("LavaDensity", 6);
        }

        builder.popPrefix();
    }

    public MoltenHeartEffect() {
        super(PotionIds.next(), false, 0xF28E0C);
        this.setPotionName("effect.molten_heart");
    }

    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        if (living.isBurning()) {
            living.extinguish();
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    public boolean providesImmunity(DamageSource source) {
        for (DamageType type : IMMUNITIES) {
            if (type != null && type.id()
                .equals(source.getDamageType())) return true;
        }

        return false;
    }

}
