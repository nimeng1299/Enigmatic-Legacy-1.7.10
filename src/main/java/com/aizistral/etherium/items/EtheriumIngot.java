package com.aizistral.etherium.items;

import net.minecraft.creativetab.CreativeTabs;

import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.items.generic.SimpleEtheriumItem;

public class EtheriumIngot extends SimpleEtheriumItem {

    public EtheriumIngot() {
        super();
        this.setMaxStackSize(64);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

}
