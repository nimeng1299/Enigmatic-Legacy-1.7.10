package com.aizistral.enigmaticlegacy.compat.curios;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Simplified equivalent of Curios' slot context.
 */
public class SlotContext {

    private final EntityLivingBase entity;
    private final String identifier;
    private final int index;
    private final boolean cosmetic;

    public SlotContext(EntityLivingBase entity, String identifier, int index) {
        this(entity, identifier, index, false);
    }

    public SlotContext(EntityLivingBase entity, String identifier, int index, boolean cosmetic) {
        this.entity = entity;
        this.identifier = identifier;
        this.index = index;
        this.cosmetic = cosmetic;
    }

    public EntityLivingBase entity() {
        return this.entity;
    }

    public EntityPlayer getPlayer() {
        return this.entity instanceof EntityPlayer ? (EntityPlayer) this.entity : null;
    }

    public String identifier() {
        return this.identifier;
    }

    public int index() {
        return this.index;
    }

    public boolean cosmetic() {
        return this.cosmetic;
    }

}
