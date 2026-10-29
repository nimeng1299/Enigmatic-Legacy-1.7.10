package com.aizistral.etherium.items;

import net.minecraft.creativetab.CreativeTabs;

import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.items.generic.SimpleEtheriumItem;

public class EnderRod extends SimpleEtheriumItem {

    public EnderRod() {
        super();
        this.setMaxStackSize(64);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }
}
