package com.aizistral.enigmaticlegacy.registries;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class EnigmaticTabs {

    public static final CreativeTabs MAIN = new CreativeTabs("enigmaticCreativeTab") {

        @Override
        public Item getTabIconItem() {
            return EnigmaticItems.ENIGMATIC_ITEM;
        }
    };

    public static final CreativeTabs POTIONS = new CreativeTabs("enigmaticPotionCreativeTab") {

        @Override
        public Item getTabIconItem() {
            return EnigmaticItems.RECALL_POTION;
        }
    };

    public static void init() {
        // Forces static initialization.
    }

}
