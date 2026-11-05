package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import baubles.api.BaubleType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IronRing extends ItemBaseCurio {

    public IronRing() {
        super(ItemBaseCurio.getDefaultProperties());
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    /**
     * Curios renders the +1 armor line automatically on 1.20; Baubles does not,
     * so the port has to add it explicitly to keep the effect visible to players.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flag) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.ironRingArmor");
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        Multimap<Attribute, AttributeModifier> atts = HashMultimap.create();
        atts.put(
            Attributes.ARMOR,
            new AttributeModifier(
                UUID.fromString("51faf191-bf72-4654-b349-cc1f4f1143bf"),
                "Armor bonus",
                1.0,
                AttributeModifier.Operation.ADDITION));

        return atts;
    }

}
