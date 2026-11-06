package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

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
import ganymedes01.etfuturum.api.elytra.IElytraPlayer;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;

/**
 * The gliding itself is not implemented here: 1.7.10 has no elytra, and the mod that backports one
 * is Et Futurum Requiem. Its whole pipeline - takeoff on jump, flight physics, wing rendering,
 * cape hiding and the per-20-ticks durability drain - keys off
 * {@code ItemArmorElytra#getElytra(entity)}, which {@code MixinEtFuturumElytra} answers with this
 * item when it is worn. What remains here is the mid-air boost (1.20
 * {@code EnigmaticElytra#boostPlayer}) that replaces the firework rocket.
 */
public class EnigmaticElytra extends ItemBaseCurio implements IBindable {

    /**
     * Look-vector acceleration used by 1.20 {@code boostPlayer}: a mix of a constant push and a
     * pull towards 1.5 blocks per tick along the view direction.
     */
    private static final double BOOST_ACCEL = 0.1D;
    private static final double BOOST_TARGET = 1.5D;
    private static final double BOOST_BLEND = 0.5D;

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
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        return HashMultimap.create();
    }

    @Override
    public boolean isValidRepairItem(ItemStack repairedItem, ItemStack material) {
        return material != null && material.getItem() == EnigmaticItems.ETHERIUM_INGOT;
    }

    /**
     * Equivalent of 1.20 {@code EnigmaticElytra#boostPlayer}. Adds the impulse in place, so it runs
     * on the client for the wearer and on the server for everyone else reading the synced
     * {@code TransientPlayerData} flag.
     */
    public static boolean boostPlayer(EntityPlayer player) {
        if (!isFallFlying(player)) return false;

        Vec3 look = player.getLookVec();

        player.motionX += look.xCoord * BOOST_ACCEL + (look.xCoord * BOOST_TARGET - player.motionX) * BOOST_BLEND;
        player.motionY += look.yCoord * BOOST_ACCEL + (look.yCoord * BOOST_TARGET - player.motionY) * BOOST_BLEND;
        player.motionZ += look.zCoord * BOOST_ACCEL + (look.zCoord * BOOST_TARGET - player.motionZ) * BOOST_BLEND;

        return true;
    }

    /**
     * 1.20 counterpart is {@code LivingEntity#isFallFlying}.
     */
    public static boolean isFallFlying(EntityLivingBase entity) {
        return entity instanceof IElytraPlayer && ((IElytraPlayer) entity).etfu$isElytraFlying();
    }

    /**
     * The elytra Et Futurum resolves for this entity, but only if it is this item - same precedence
     * as {@code ItemArmorElytra#getElytra}, where a real Et Futurum elytra wins over the bauble.
     * Used by the wing layer so it does not draw over a vanilla elytra.
     */
    public static ItemStack getActiveElytra(EntityLivingBase entity) {
        ItemStack resolved = ItemArmorElytra.getElytra(entity);

        return resolved != null && resolved.getItem() == EnigmaticItems.ENIGMATIC_ELYTRA ? resolved : null;
    }

}
