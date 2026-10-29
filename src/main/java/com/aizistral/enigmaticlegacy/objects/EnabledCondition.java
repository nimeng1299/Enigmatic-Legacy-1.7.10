package com.aizistral.enigmaticlegacy.objects;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

/**
 * Condition that checks whether item is instance of IPerhaps, and if it is,
 * whether or not it is enabled. Used to disable recipes for items disabled
 * in config.
 * 
 * @author Integral
 */

public class EnabledCondition {

    private static final ResourceLocation ID = new ResourceLocation(EnigmaticLegacy.MODID, "is_enabled");
    private final ResourceLocation item;

    public EnabledCondition(ResourceLocation item) {
        this.item = item;
    }

    public ResourceLocation getID() {
        return ID;
    }

    public boolean test(Object context) {
        Item item = ForgeRegistries.ITEMS.getValue(this.item);

        if (this.item.toString()
            .equals(EnigmaticLegacy.MODID + ":bonuswoolrecipes"))
            return OmniconfigHandler.bonusWoolRecipesEnabled.getValue();
        else return OmniconfigHandler.isItemEnabled(item);
    }

}
