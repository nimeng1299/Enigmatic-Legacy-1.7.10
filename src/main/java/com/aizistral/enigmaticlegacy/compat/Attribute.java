package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.entity.ai.attributes.IAttribute;

/**
 * Attribute descriptor wrapping a 1.7.10 attribute.
 */
public class Attribute {

    private final IAttribute vanilla;
    private final String descriptionId;

    public Attribute(IAttribute vanilla) {
        this.vanilla = vanilla;
        this.descriptionId = vanilla.getAttributeUnlocalizedName();
    }

    public Attribute(IAttribute vanilla, String descriptionId) {
        this.vanilla = vanilla;
        this.descriptionId = descriptionId;
    }

    public IAttribute toVanilla() {
        return this.vanilla;
    }

    public String getDescriptionId() {
        return this.descriptionId;
    }

    public double getDefaultValue() {
        return this.vanilla.getDefaultValue();
    }

    @Override
    public String toString() {
        return this.descriptionId;
    }

}
