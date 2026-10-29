package com.aizistral.enigmaticlegacy.enchantments;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

public class SorrowCurse extends com.aizistral.enigmaticlegacy.compat.CompatEnchantment {

    private final List<Potion> debuffList = new ArrayList<>();

    public SorrowCurse(EquipmentSlot... slots) {
        super(EnumRarity.rare, EnchantmentCategory.ARMOR, slots);

        this.debuffList.add(MobEffects.BLINDNESS);
        this.debuffList.add(MobEffects.CONFUSION);
        this.debuffList.add(MobEffects.WEAKNESS);
        this.debuffList.add(MobEffects.MOVEMENT_SLOWDOWN);
        this.debuffList.add(MobEffects.DIG_SLOWDOWN);
        this.debuffList.add(MobEffects.HUNGER);
    }

    public void maybeApplyDebuff(EntityPlayer player, float damage) {
        if (player.worldObj.rand.nextDouble() < 0.1) {
            float severity = damage > 4 ? damage / 4 : 1;
            severity *= 0.5F + player.worldObj.rand.nextFloat();

            int amplifier = (int) (severity / 2);

            if (amplifier > 3) {
                amplifier = 3;
            }

            Potion debuff = this.debuffList.get(player.worldObj.rand.nextInt(this.debuffList.size()));
            PotionEffect instance = new PotionEffect(debuff, (int) (300 * severity), amplifier, false, true);

            player.addPotionEffect(instance);
        }
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
        return OmniconfigHandler.isItemEnabled(this) && super.canEnchant(stack);
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
        return true;
    }

    protected boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench);
    }

}
