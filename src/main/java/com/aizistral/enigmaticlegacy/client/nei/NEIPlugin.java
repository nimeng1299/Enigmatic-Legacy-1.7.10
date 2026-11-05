package com.aizistral.enigmaticlegacy.client.nei;

import net.minecraft.nbt.NBTTagCompound;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;

import codechicken.nei.api.API;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Registers the mod's brewing recipes with NotEnoughItems. Only loaded when NEI
 * is present; see {@code ClientProxy#init}.
 */
@SideOnly(Side.CLIENT)
public final class NEIPlugin {

    private NEIPlugin() {
        // NO-OP
    }

    public static void register() {
        API.registerRecipeHandler(new EnigmaticBrewingRecipeHandler());
        API.registerUsageHandler(new EnigmaticBrewingRecipeHandler());

        NBTTagCompound info = new NBTTagCompound();
        info.setString("handler", EnigmaticBrewingRecipeHandler.class.getName());
        info.setString("modName", "Enigmatic Legacy");
        info.setString("modId", EnigmaticLegacy.MODID);
        info.setString("itemName", EnigmaticLegacy.MODID + ":ultimate_potion");
        info.setInteger("handlerWidth", 166);
        info.setInteger("handlerHeight", 65);
        info.setBoolean("multipleWidgetsAllowed", true);
        info.setInteger("yShift", 0);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", info);
    }

    public static void preload() {
        EnigmaticBrewingRecipeHandler handler = new EnigmaticBrewingRecipeHandler();
        handler.loadCraftingRecipes("brewing");
        EnigmaticLegacy.LOGGER.info("Registered {} brewing recipes with NotEnoughItems.", handler.numRecipes());
    }

}
