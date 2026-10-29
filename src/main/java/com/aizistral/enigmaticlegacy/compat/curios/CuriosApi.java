package com.aizistral.enigmaticlegacy.compat.curios;

import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.google.common.collect.Multimap;

/**
 * Facade for Curios API calls; slot management itself is provided by Baubles.
 */
public class CuriosApi {

    private static final CuriosHelper HELPER = new CuriosHelper();

    public static CuriosHelper getCuriosHelper() {
        return HELPER;
    }

    public static class CuriosHelper {

        public void addSlotModifier(Multimap<Attribute, AttributeModifier> map, String identifier, java.util.UUID uuid,
            double amount, AttributeModifier.Operation operation) {
            // Slot modifiers have no direct 1.7.10 equivalent; no-op.
        }
    }

}
