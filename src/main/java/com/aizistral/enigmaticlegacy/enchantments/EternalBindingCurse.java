package com.aizistral.enigmaticlegacy.enchantments;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;

import com.aizistral.enigmaticlegacy.api.items.IBindable;
import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

public class EternalBindingCurse extends com.aizistral.enigmaticlegacy.compat.CompatEnchantment {

    private final List<String> incompatibleKeywords = new ArrayList<>();

    public EternalBindingCurse(EquipmentSlot... slots) {
        super(EnumRarity.rare, EnchantmentCategory.WEARABLE, slots);

        this.incompatibleKeywords.add("soulbound");
        this.incompatibleKeywords.add("soulbinding");
    }

    public int getMinCost(int enchantmentLevel) {
        return 25;
    }

    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    public boolean canEnchant(ItemStack stack) {
        if (!OmniconfigHandler.isItemEnabled(this)) return false;

        if (stack.getItem() == EnigmaticItems.CURSED_RING || stack.getItem() == EnigmaticItems.ESCAPE_SCROLL
            || stack.getItem() == EnigmaticItems.ENIGMATIC_AMULET
            || stack.getItem() == EnigmaticItems.DESOLATION_RING) return false;

        return stack.getItem() instanceof IBindable || super.canEnchant(stack);
    }

    public boolean isTreasureOnly() {
        return true;
    }

    public boolean isCurse() {
        return true;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return OmniconfigHandler.isItemEnabled(this);
    }

    public boolean isDiscoverable() {
        return false;
    }

    protected boolean checkCompatibility(Enchantment ench) {
        if (this.incompatibleKeywords.stream()
            .anyMatch(
                keyword -> StringUtils.containsIgnoreCase(
                    keyword,
                    ForgeRegistries.ENCHANTMENTS.getKey(ench)
                        .getResourcePath())))
            return false;

        return ench != Enchantments.BINDING_CURSE && ench != Enchantments.VANISHING_CURSE
            ? super.checkCompatibility(ench)
            : false;
    }

}
