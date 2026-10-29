package com.aizistral.enigmaticlegacy.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.compat.PotionIds;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

public class GrowingHungerEffect extends Potion {

    public static Omniconfig.DoubleParameter damageBoost = null;
    public static Omniconfig.DoubleParameter exhaustionGain = null;
    public static Omniconfig.IntParameter ticksPerLevel = null;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("GrowingHunger");

        if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
            damageBoost = builder.comment("Damage boost granted by the Growing Hunger, per level of effect.")
                .max(100)
                .getDouble("DamageBoost", 0.1);

            exhaustionGain = builder.comment("Exhaustion applied by Growing Hunger every 4 ticks, per level of effect.")
                .getDouble("ExhaustionGain", 0.5);

            ticksPerLevel = builder
                .comment(
                    "How lock the The Voracious Pan needs to be held, in ticks, to increase the strength "
                        + "of the Growing Hunger effect by one level.")
                .getInt("TicksPerLevel", 300);
        }

        builder.popPrefix();
    }

    public GrowingHungerEffect() {
        super(PotionIds.next(), false, 0xBD1BE5);
        this.setPotionName("effect.growing_hunger");
        this.func_111184_a(
            SharedMonsterAttributes.attackDamage,
            "c281d54f-3277-4e4c-899e-c27f4f697b24",
            damageBoost != null ? damageBoost.getValue() : 0.1D,
            2);
    }

    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        if (living instanceof EntityPlayerMP) {
            ((EntityPlayerMP) living).addExhaustion((float) (exhaustionGain.getValue() * (1 + amplifier)));
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 4 == 0;
    }

}
