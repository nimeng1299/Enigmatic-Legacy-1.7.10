package com.aizistral.enigmaticlegacy.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.gui.containers.LoreInscriberContainer;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Routes every GUI id used by the mod to its container/screen pair. Registered in {@code CommonProxy#preInit}.
 */
public class EnigmaticGuiHandler implements IGuiHandler {

    public static final int LORE_INSCRIBER_GUI = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == LORE_INSCRIBER_GUI) return new LoreInscriberContainer(player.inventory, player);

        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == LORE_INSCRIBER_GUI) {
            Container container = new LoreInscriberContainer(player.inventory, player);
            return new com.aizistral.enigmaticlegacy.gui.containers.LoreInscriberScreen(container, player);
        }

        return null;
    }

}
