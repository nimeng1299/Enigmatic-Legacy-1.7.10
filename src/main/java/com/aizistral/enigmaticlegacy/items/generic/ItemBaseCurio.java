package com.aizistral.enigmaticlegacy.items.generic;

import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.aizistral.enigmaticlegacy.api.items.IBindable;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.curios.ICurio.DropRule;
import com.aizistral.enigmaticlegacy.compat.curios.ICurioItem;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

import baubles.api.BaubleType;
import baubles.api.IBauble;

public abstract class ItemBaseCurio extends ItemBase implements ICurioItem, IBauble, IBindable, Vanishable {

    public ItemBaseCurio() {
        this(getDefaultProperties());
    }

    public ItemBaseCurio(Properties props) {
        super(props);
    }

    // ------------------------------------------------------------------
    // Modern-style hooks
    // ------------------------------------------------------------------

    public void onEquip(SlotContext context, ItemStack prevStack, ItemStack stack) {
        // NO-OP
    }

    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        // NO-OP
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        // NO-OP
    }

    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return !SuperpositionHandler.hasCurio(context.entity(), this);
    }

    public boolean canUnequip(SlotContext context, ItemStack stack) {
        return true;
    }

    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit,
        ItemStack stack) {
        return DropRule.DEFAULT;
    }

    // ------------------------------------------------------------------
    // Baubles integration
    // ------------------------------------------------------------------

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.AMULET;
    }

    protected SlotContext slotContext(EntityLivingBase entity) {
        return new SlotContext(entity, "amulet", 0);
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        this.curioTick(this.slotContext(player), stack);
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        SlotContext context = this.slotContext(player);
        this.onEquip(context, stack, stack);
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        SlotContext context = this.slotContext(player);
        this.onUnequip(context, stack, stack);
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return this.canEquip(this.slotContext(player), stack);
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return this.canUnequip(this.slotContext(player), stack);
    }

    public static Properties getDefaultProperties() {
        Properties props = new Properties();
        props.stacksTo(1);
        props.rarity(EnumRarity.common);
        return props;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        Map<Enchantment, Integer> list = com.aizistral.enigmaticlegacy.compat.Enchantments.getEnchantments(book);

        if (list.size() == 1 && list.containsKey(Enchantments.BINDING_CURSE)) return true;
        else return super.isBookEnchantable(stack, book);
    }

}
