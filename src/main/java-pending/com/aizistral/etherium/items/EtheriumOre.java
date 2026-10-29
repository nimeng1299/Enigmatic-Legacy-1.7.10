package com.aizistral.etherium.items;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.core.EtheriumUtil;
import com.aizistral.etherium.core.IEtheriumConfig;

import net.minecraft.util.ResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class EtheriumOre extends Item implements ICreativeTabMember {

	public EtheriumOre() {
		super(EtheriumUtil.defaultProperties(EtheriumOre.class).stacksTo(64).fireResistant());
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return EnigmaticTabs.MAIN;
	}

}
