package com.aizistral.etherium.core;

import net.minecraft.item.EnumRarity;

import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

public class EtheriumUtil {

    public static ItemBase.Properties defaultProperties(Class<?> itemClass) {
        IEtheriumConfig config = EtheriumConfigHandler.instance();
        ItemBase.Properties props = new ItemBase.Properties();

        props.stacksTo(1);
        props.rarity(EnumRarity.rare);

        return props;
    }

    private static boolean isAmong(Class<?> theClass, Class<?>... classList) {
        for (Class<?> someClass : classList) {
            if (theClass == someClass) return true;
        }

        return false;
    }

}
