package com.aizistral.enigmaticlegacy.compat;

import java.util.UUID;

/**
 * Attribute modifier with the modern operation enum constructor.
 */
public class AttributeModifier extends net.minecraft.entity.ai.attributes.AttributeModifier {

    public enum Operation {

        ADDITION(0),
        MULTIPLY_BASE(1),
        MULTIPLY_TOTAL(2);

        private final int id;

        Operation(int id) {
            this.id = id;
        }

        public int toValue() {
            return this.id;
        }
    }

    public AttributeModifier(UUID id, String name, double amount, Operation operation) {
        super(id, name, amount, operation.toValue());
    }

    public AttributeModifier(String name, double amount, Operation operation) {
        this(UUID.randomUUID(), name, amount, operation);
    }

    public static AttributeModifier of(String name, double amount, Operation operation) {
        return new AttributeModifier(name, amount, operation);
    }

}
