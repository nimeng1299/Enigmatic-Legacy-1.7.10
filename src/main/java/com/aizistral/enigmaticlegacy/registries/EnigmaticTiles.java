package com.aizistral.enigmaticlegacy.registries;

import com.aizistral.enigmaticlegacy.blocks.TileEndAnchor;

/**
 * Tile entity registration on 1.7.10 happens through
 * {@code GameRegistry.registerTileEntity}.
 */
public class EnigmaticTiles {

    private static final EnigmaticTiles INSTANCE = new EnigmaticTiles();

    private EnigmaticTiles() {
        // NO-OP
    }

    public static void init() {
        cpw.mods.fml.common.registry.GameRegistry.registerTileEntity(
            TileEndAnchor.class,
            com.aizistral.enigmaticlegacy.EnigmaticLegacy.MODID + ":tile_end_anchor");
    }

}
