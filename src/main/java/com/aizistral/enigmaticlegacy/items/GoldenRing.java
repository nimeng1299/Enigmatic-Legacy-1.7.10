package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GoldenRing extends ItemBaseCurio {

    public GoldenRing() {
        super(
            ItemBaseCurio.getDefaultProperties()
                .rarity(EnumRarity.uncommon));
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World world, List<Component> list, TooltipFlag flag) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            if (Minecraft.getMinecraft().thePlayer != null
                && SuperpositionHandler.isTheCursedOne(Minecraft.getMinecraft().thePlayer)) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.goldenRing1Cursed");
            } else {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.goldenRing1");
            }
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(
            Attributes.LUCK,
            new AttributeModifier(
                UUID.fromString("6c913e9a-0d6f-4b3b-81b9-4c82f7778b52"),
                EnigmaticLegacy.MODID + ":luck_bonus",
                1.0,
                AttributeModifier.Operation.ADDITION));

        return attributes;
    }

    public boolean makesPiglinsNeutral(ItemStack stack, EntityLivingBase wearer) {
        return true;
    }

    public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    public boolean isPiglinCurrency(ItemStack stack) {
        return true;
    }

}
