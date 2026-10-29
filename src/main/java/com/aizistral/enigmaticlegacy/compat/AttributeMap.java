package com.aizistral.enigmaticlegacy.compat;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

import com.google.common.collect.Multimap;

/**
 * Simplified attribute container used by a few ported code paths. When bound to
 * an entity it applies modifiers to that entity's attribute map.
 */
public class AttributeMap {

    private final Map<IAttribute, Object> attributes = new HashMap<>();
    private final EntityLivingBase entity;

    public AttributeMap() {
        this.entity = null;
    }

    public AttributeMap(EntityLivingBase entity) {
        this.entity = entity;
    }

    public static AttributeMap of(EntityLivingBase entity) {
        return new AttributeMap(entity);
    }

    public Object getInstance(Attribute attribute) {
        return this.attributes.get(attribute.toVanilla());
    }

    public void put(Attribute attribute, Object instance) {
        this.attributes.put(attribute.toVanilla(), instance);
    }

    public void removeAttributeModifiers(Multimap modifiers) {
        this.apply(modifiers, false);
    }

    public void addTransientAttributeModifiers(Multimap modifiers) {
        this.apply(modifiers, true);
    }

    public void applyAttributeModifiers(Multimap modifiers) {
        this.apply(modifiers, true);
    }

    private void apply(Multimap modifiers, boolean add) {
        if (this.entity == null || modifiers == null) return;

        for (Object entryObject : modifiers.entries()) {
            Map.Entry entry = (Map.Entry) entryObject;
            IAttributeInstance instance = this.resolveInstance(entry.getKey());

            if (instance == null || !(entry.getValue() instanceof net.minecraft.entity.ai.attributes.AttributeModifier))
                continue;

            net.minecraft.entity.ai.attributes.AttributeModifier modifier = (net.minecraft.entity.ai.attributes.AttributeModifier) entry
                .getValue();

            if (add) {
                instance.applyModifier(modifier);
            } else {
                instance.removeModifier(modifier);
            }
        }
    }

    private IAttributeInstance resolveInstance(Object key) {
        IAttribute attribute = null;

        if (key instanceof Attribute) {
            attribute = ((Attribute) key).toVanilla();
        } else if (key instanceof IAttribute) {
            attribute = (IAttribute) key;
        } else if (key instanceof String) {
            IAttributeInstance byName = this.entity.getAttributeMap()
                .getAttributeInstanceByName((String) key);
            return byName;
        }

        if (attribute == null) return null;

        IAttributeInstance instance = this.entity.getEntityAttribute(attribute);

        if (instance == null) {
            try {
                instance = this.entity.getAttributeMap()
                    .registerAttribute(attribute);
            } catch (IllegalArgumentException ex) {
                instance = this.entity.getAttributeMap()
                    .getAttributeInstance(attribute);
            }
        }

        return instance;
    }

}
