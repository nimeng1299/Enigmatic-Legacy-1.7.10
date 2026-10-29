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

public class GrowingBloodlustEffect extends Potion {

    public static Omniconfig.DoubleParameter damageBoost = null;
    public static Omniconfig.DoubleParameter lifestealBoost = null;
    public static Omniconfig.DoubleParameter healthLossLimit = null;
    public static Omniconfig.IntParameter healthLossTicks = null;
    public static Omniconfig.IntParameter ticksPerLevel = null;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("GrowingBloodlust");

        if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
            damageBoost = builder.comment("Damage boost granted by the Growing Bloodlust, per level of effect.")
                .max(100)
                .getDouble("DamageBoost", 0.05);

            lifestealBoost = builder.comment("Lifesteal granted by the Growing Bloodlust, per level of effect.")
                .max(100)
                .getDouble("LifestealBoost", 0.025);

            healthLossTicks = builder
                .comment("How often the player loses 1 HP at level one of Growing Bloodlust, in ticks.")
                .getInt("HealthLossTicks", 160);

            healthLossLimit = builder
                .comment("How much health Growing Bloodlust leaves the player with, as a fraction of max health.")
                .getDouble("HealthLossLimit", 0.3);

            ticksPerLevel = builder
                .comment(
                    "How lock the The Voracious Pan needs to be held, in ticks, to increase the strength "
                        + "of the Growing Bloodlust effect by one level.")
                .getInt("TicksPerLevel", 300);
        }

        builder.popPrefix();
    }

    public GrowingBloodlustEffect() {
        super(PotionIds.next(), false, 0xC30018);
        this.setPotionName("effect.growing_bloodlust");
        this.func_111184_a(
            SharedMonsterAttributes.attackDamage,
            "d88f6930-fefb-4bf7-a418-f368458355ff",
            damageBoost != null ? damageBoost.getValue() : 0.05D,
            2);
    }

    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        if (living instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) living;

            if (!player.capabilities.isCreativeMode
                && player.getHealth() / player.getMaxHealth() > healthLossLimit.getValue()) {
                player.setHealth(player.getHealth() - 1);
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int period = healthLossTicks.getValue() / (1 + amplifier);
        return period > 0 && duration % period == 0;
    }

}
