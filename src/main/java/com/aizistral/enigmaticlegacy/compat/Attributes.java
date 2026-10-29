package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public final class Attributes {

    public static final Attribute MAX_HEALTH = new Attribute(
        SharedMonsterAttributes.maxHealth,
        "attribute.name.generic.maxHealth");
    public static final Attribute FOLLOW_RANGE = new Attribute(
        SharedMonsterAttributes.followRange,
        "attribute.name.generic.followRange");
    public static final Attribute KNOCKBACK_RESISTANCE = new Attribute(
        SharedMonsterAttributes.knockbackResistance,
        "attribute.name.generic.knockbackResistance");
    public static final Attribute MOVEMENT_SPEED = new Attribute(
        SharedMonsterAttributes.movementSpeed,
        "attribute.name.generic.movementSpeed");
    public static final Attribute ATTACK_DAMAGE = new Attribute(
        SharedMonsterAttributes.attackDamage,
        "attribute.name.generic.attackDamage");
    public static final Attribute ARMOR = new Attribute(
        new RangedAttribute("generic.armor", 0.0D, 0.0D, 30.0D),
        "attribute.name.generic.armor");
    public static final Attribute ARMOR_TOUGHNESS = new Attribute(
        new RangedAttribute("generic.armorToughness", 0.0D, 0.0D, 20.0D),
        "attribute.name.generic.armorToughness");
    public static final Attribute ATTACK_SPEED = new Attribute(
        new RangedAttribute("generic.attackSpeed", 4.0D, 0.0D, 1024.0D),
        "attribute.name.generic.attackSpeed");
    public static final Attribute LUCK = new Attribute(
        new RangedAttribute("generic.luck", 0.0D, -1024.0D, 1024.0D),
        "attribute.name.generic.luck");

    private Attributes() {
        // NO-OP
    }

}
