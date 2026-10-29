package com.aizistral.enigmaticlegacy.api.materials;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;

/**
 * 1.7.10 armor materials are enum constants created through EnumHelper, so this
 * replaces the modern interface-based implementation with a static holder.
 */
public final class EnigmaticArmorMaterials {

    public static final ArmorMaterial ETHERIUM = EnumHelper
        .addArmorMaterial("ETHERIUM", 132, new int[] { 4, 7, 9, 4 }, 24);

    public static final String TEXTURE = EnigmaticLegacy.MODID + ":textures/models/armor/etherium_armor.png";

    private EnigmaticArmorMaterials() {
        // NO-OP
    }

    public static int getDurabilityForType(ArmorMaterial material, int slot) {
        return material.getDurability(slot);
    }

    public static int getDefenseForType(ArmorMaterial material, int slot) {
        return material.getDamageReductionAmount(slot);
    }

}
