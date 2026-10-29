package com.aizistral.enigmaticlegacy.api.materials;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

/**
 * 1.7.10 tools use enum-based materials created through EnumHelper.
 */
public final class EnigmaticMaterials {

    public static final ToolMaterial ELDRITCH_PAN = EnumHelper.addToolMaterial("ELDRITCH_PAN", 0, 4000, 6.0F, 3.0F, 24);
    public static final ToolMaterial FORBIDDEN_AXE = EnumHelper
        .addToolMaterial("FORBIDDEN_AXE", 0, 2000, 6.0F, 3.0F, 16);
    public static final ToolMaterial ENDER_SLAYER = EnumHelper.addToolMaterial("ENDER_SLAYER", 0, 2000, 6.0F, 3.0F, 16);
    public static final ToolMaterial ETHERIUM = EnumHelper.addToolMaterial("ETHERIUM", 5, 3000, 8.0F, 5.0F, 32);
    public static final ToolMaterial CREATION = EnumHelper.addToolMaterial("CREATION", 6, 10000, 32.0F, 0.0F, 40);

    private EnigmaticMaterials() {
        // NO-OP
    }

}
