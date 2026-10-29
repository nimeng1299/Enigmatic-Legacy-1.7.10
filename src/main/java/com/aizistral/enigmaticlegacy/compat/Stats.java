package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;

/**
 * Minimal custom-stat support for 1.7.10.
 */
public final class Stats {

    public static final Custom CUSTOM = new Custom();
    public static final Custom ITEM_USED = CUSTOM;

    private Stats() {
        // NO-OP
    }

    public static class Custom {

        public StatBase get(ResourceLocation id) {
            return StatList.func_151177_a(id.toString());
        }

        public StatBase get(net.minecraft.item.Item item) {
            return StatList.objectUseStats[net.minecraft.item.Item.getIdFromItem(item)];
        }
    }

}
