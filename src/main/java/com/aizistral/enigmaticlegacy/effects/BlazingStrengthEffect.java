package com.aizistral.enigmaticlegacy.effects;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.PotionAttackDamage;

import com.aizistral.enigmaticlegacy.compat.PotionIds;

public class BlazingStrengthEffect extends PotionAttackDamage {

    public BlazingStrengthEffect() {
        super(PotionIds.next(), false, 0xFF5000);
        this.setPotionName("effect.blazing_strength");
        this.func_111184_a(SharedMonsterAttributes.attackDamage, "9D86B288-C0E2-45DD-95BF-AE43ED7E2116", 3.0D, 0);
    }

}
