package com.aizistral.enigmaticlegacy.compat.curios;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.google.common.collect.Multimap;

public interface ICurioItem {

    default void onEquip(SlotContext context, ItemStack prevStack, ItemStack stack) {
        // NO-OP
    }

    default void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        // NO-OP
    }

    default void curioTick(SlotContext context, ItemStack stack) {
        // NO-OP
    }

    default boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    default boolean canEquip(SlotContext context, ItemStack stack) {
        return true;
    }

    default boolean canUnequip(SlotContext context, ItemStack stack) {
        return true;
    }

    default ICurio.DropRule getDropRule(SlotContext context, DamageSource source, int lootingLevel, boolean recentlyHit,
        ItemStack stack) {
        return ICurio.DropRule.DEFAULT;
    }

    default int getFortuneLevel(SlotContext slotContext, com.aizistral.enigmaticlegacy.compat.LootContext lootContext,
        ItemStack curio) {
        return 0;
    }

    default int getLootingLevel(SlotContext slotContext, DamageSource source,
        net.minecraft.entity.EntityLivingBase target, int baseLooting, ItemStack curio) {
        return baseLooting;
    }

    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext context, UUID uuid,
        ItemStack stack) {
        return com.google.common.collect.ArrayListMultimap.create();
    }

}
