package com.aizistral.enigmaticlegacy.effects;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.ImmutableList;
import com.aizistral.enigmaticlegacy.compat.ResourceKey;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import com.aizistral.enigmaticlegacy.compat.DamageType;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import net.minecraft.potion.Potion;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.entity.EntityLivingBase;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;

import java.util.List;
import java.util.Map;

public class GrowingHungerEffect extends Potion {
    public static Omniconfig.DoubleParameter damageBoost = null;
    public static Omniconfig.DoubleParameter exhaustionGain = null;
    public static Omniconfig.IntParameter ticksPerLevel = null;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("GrowingHunger");

        if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
            damageBoost = builder
                    .comment("Damage boost granted by the Growing Hunger, per level of effect.")
                    .max(100)
                    .getDouble("DamageBoost", 0.1);

            exhaustionGain = builder
                    .comment("Exhaustion applied by Growing Hunger every 4 ticks, per level of effect.")
                    .getDouble("ExhaustionGain", 0.5);

            ticksPerLevel = builder
                    .comment("How lock the The Voracious Pan needs to be held, in ticks, to increase the strength "
                        + "of the Growing Hunger effect by one level.")
                    .getInt("TicksPerLevel", 300);
        }

        builder.popPrefix();
    }

    public GrowingHungerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xBD1BE5);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "c281d54f-3277-4e4c-899e-c27f4f697b24",
                damageBoost.getValue(), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(EntityLivingBase living, int amplifier) {
        if (living instanceof EntityPlayerMP player) {
            player.causeFoodExhaustion((float) (exhaustionGain.getValue() * (1 + amplifier)));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 4 == 0;
    }

}