package com.aizistral.enigmaticlegacy.compat.curios;

import java.util.Map;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.google.common.collect.Multimap;

import baubles.api.BaublesApi;

/**
 * Facade for Curios API calls; slot management itself is provided by Baubles.
 */
public class CuriosApi {

    private static final CuriosHelper HELPER = new CuriosHelper();

    public static CuriosHelper getCuriosHelper() {
        return HELPER;
    }

    /**
     * On 1.20 Curios applies {@link ICurioItem#getAttributeModifiers} automatically
     * while a curio is equipped. Baubles has no equivalent hook and 1.7.10 has no
     * vanilla armor attribute, so the port reads the equipped curio armor
     * modifiers directly instead of keeping a synchronised attribute map.
     *
     * <p>
     * Only additive {@link Attributes#ARMOR} modifiers are folded in. A modifier
     * that is already present on the entity's attribute map is skipped, so items
     * managing their own modifiers (e.g. Heart of the Golem) are not counted twice.
     */
    public static double getEquippedArmorBonus(EntityPlayer player, IAttributeInstance armorInstance) {
        IInventory baubles = BaublesApi.getBaubles(player);

        if (baubles == null) return 0.0D;

        double bonus = 0.0D;

        for (int slot = 0; slot < baubles.getSizeInventory(); slot++) {
            ItemStack stack = baubles.getStackInSlot(slot);

            if (stack == null || !(stack.getItem() instanceof ICurioItem)) continue;

            Multimap<Attribute, AttributeModifier> modifiers = ((ICurioItem) stack.getItem())
                .getAttributeModifiers(new SlotContext(player, "ring", slot), player.getUniqueID(), stack);

            if (modifiers == null || modifiers.isEmpty()) continue;

            for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
                Attribute attribute = entry.getKey();
                AttributeModifier modifier = entry.getValue();

                if (attribute == null || attribute.toVanilla() != Attributes.ARMOR.toVanilla()) continue;
                if (modifier == null || modifier.getOperation() != 0) continue;
                if (armorInstance != null && armorInstance.getModifier(modifier.getID()) != null) continue;

                bonus += modifier.getAmount();
            }
        }

        return bonus;
    }

    public static class CuriosHelper {

        public void addSlotModifier(Multimap<Attribute, AttributeModifier> map, String identifier, java.util.UUID uuid,
            double amount, AttributeModifier.Operation operation) {
            // Slot modifiers have no direct 1.7.10 equivalent; no-op.
        }
    }

}
