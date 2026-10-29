package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.IBindable;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnigmaticElytra extends ItemBaseCurio implements IBindable {

    private static final AttributeModifier ELYTRA_MODIFIER = new AttributeModifier(
        UUID.fromString("44dfce5a-2f09-4f19-bc29-9b0324cd2a40"),
        EnigmaticLegacy.MODID + ":elytra_modifier",
        1.0,
        AttributeModifier.Operation.ADDITION);

    public EnigmaticElytra() {
        super(
            ItemBaseCurio.getDefaultProperties()
                .durability(5000)
                .fireResistant()
                .rarity(EnumRarity.epic));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World world, List<Component> list, TooltipFlag flag) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticElytra1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticElytra2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticElytra3");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        EntityLivingBase entity = context.entity();

        if (entity instanceof EntityPlayer && !entity.worldObj.isRemote && !entity.onGround && !entity.isInWater()) {
            if (entity.motionY < -0.18) {
                entity.motionY = -0.18;
                entity.fallDistance = 0F;
            }

            if (entity.ticksExisted % 20 == 0 && entity.motionY < -0.05) {
                stack.damageItem(1, entity);
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        return HashMultimap.create();
    }

    @Override
    public boolean isValidRepairItem(ItemStack repairedItem, ItemStack material) {
        return material != null && material.getItem() == EnigmaticItems.ETHERIUM_INGOT;
    }

    public boolean canElytraFly(ItemStack stack, EntityLivingBase entity) {
        return entity instanceof EntityPlayer;
    }

    public boolean elytraFlightTick(ItemStack stack, EntityLivingBase entity, int flightTicks) {
        return entity instanceof EntityPlayer;
    }

}
