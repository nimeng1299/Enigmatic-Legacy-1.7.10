package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class EnchanterPearl extends ItemBaseCurio implements ICursed {

    public EnchanterPearl() {
        super(
            getDefaultProperties().rarity(EnumRarity.epic)
                .fireResistant()
                .stacksTo(1));

        // TODO Make non-cursed and add Crimson Pearl
    }

    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enchantersPearl1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enchantersPearl2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enchantersPearl3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enchantersPearl4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enchantersPearl5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enchantersPearl6");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(list);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();

        // 1.20 grants +1 charm slot here. Baubles allocates its slots statically for every
        // player, so there is no per-player slot to unlock - see PORTING_REMAINING, charm slots.

        return attributes;
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    public boolean isPresent(EntityPlayer player) {
        return SuperpositionHandler.isTheCursedOne(player) && SuperpositionHandler.hasCurio(player, this);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context.entity() instanceof EntityPlayer player && SuperpositionHandler.isTheCursedOne(player)
            && super.canEquip(context, stack);
    }

    /**
     * Reproduces the enchantment rolls 1.20 grants to a pearl bearer: the item is rolled twice,
     * the second time at a level 7 higher (capped at 40) and with treasure enchantments allowed,
     * and both results are merged - which is what "greater amount and power of enchantments"
     * in the tooltip amounts to.
     * <p>
     * The 1.20 implementation also converts a rolled Curse of Binding into Eternal Binding with a
     * 50% chance. 1.7.10 has no Curse of Binding at all, so that step is skipped; the mod's own
     * curses are ordinary enchantments here and can be rolled by the second pass directly
     * (1.7.10's {@code mapEnchantmentData} does not filter treasure-only enchantments).
     *
     * @return The enchanted stack, or null if the input cannot be enchanted or nothing rolled.
     */
    public static ItemStack applyEnchanting(ItemStack input, int level, Random rand) {
        if (input == null || !input.isItemEnchantable()) return null;

        List<EnchantmentData> primary = EnchantmentHelper.buildEnchantmentList(rand, input, level);

        if (primary == null || primary.isEmpty()) return null;

        // Books lose one of the rolled enchantments, exactly like vanilla does for both versions.
        if (input.getItem() == Items.book && primary.size() > 1) {
            primary.remove(rand.nextInt(primary.size()));
        }

        ItemStack result = prepareForEnchanting(input);
        applyEnchantments(result, primary);

        List<EnchantmentData> secondary = EnchantmentHelper.buildEnchantmentList(rand, input, Math.min(level + 7, 40));

        if (secondary != null && !secondary.isEmpty()) {
            ItemStack extra = prepareForEnchanting(input);
            applyEnchantments(extra, secondary);
            result = SuperpositionHandler.mergeEnchantments(result, extra, false, false);
        }

        return result;
    }

    private static ItemStack prepareForEnchanting(ItemStack input) {
        if (input.getItem() != Items.book) return input.copy();

        ItemStack book = new ItemStack(Items.enchanted_book);

        if (input.stackTagCompound != null) {
            book.stackTagCompound = (NBTTagCompound) input.stackTagCompound.copy();
        }

        return book;
    }

    private static void applyEnchantments(ItemStack stack, List<EnchantmentData> enchantments) {
        boolean isBook = stack.getItem() == Items.enchanted_book;

        for (EnchantmentData data : enchantments) {
            if (isBook) {
                Items.enchanted_book.addEnchantment(stack, data);
            } else {
                stack.addEnchantment(data.enchantmentobj, data.enchantmentLevel);
            }
        }
    }

}
