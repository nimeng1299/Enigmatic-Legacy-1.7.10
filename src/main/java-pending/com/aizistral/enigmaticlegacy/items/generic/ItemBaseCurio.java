package com.aizistral.enigmaticlegacy.items.generic;

import java.util.Map;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.IBindable;
import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

import net.minecraft.util.DamageSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import com.aizistral.enigmaticlegacy.compat.Equipable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import net.minecraft.world.World;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.curios.ICurio.DropRule;
import com.aizistral.enigmaticlegacy.compat.curios.ICurioItem;

public abstract class ItemBaseCurio extends ItemBase implements ICurioItem, IBindable, Vanishable {

	public ItemBaseCurio() {
		this(getDefaultProperties());
	}

	public ItemBaseCurio(Properties props) {
		super(props);
	}

	@Override
	public void onEquip(SlotContext context, ItemStack prevStack, ItemStack stack) {
		// Insert existential void here
	}

	@Override
	public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
		// Insert existential void here
	}

	@Override
	public void curioTick(SlotContext context, ItemStack stack) {
		// Insert existential void here
	}

	@Override
	public void onCraftedBy(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		// Insert existential void here
	}

	@Override
	public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
		return true;
	}

	@Override
	public boolean canEquip(SlotContext context, ItemStack stack) {
		return !SuperpositionHandler.hasCurio(context.entity(), this);
	}

	@Override
	public boolean canUnequip(SlotContext context, ItemStack stack) {
		return true;
	}

	@Override
	public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
		return DropRule.DEFAULT;
	}

	public static Properties getDefaultProperties() {
		Properties props = new Item.Properties();

		props.stacksTo(1);
		props.rarity(EnumRarity.COMMON);

		return props;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		Map<Enchantment, Integer> list = EnchantmentHelper.getEnchantments(book);

		if (list.size() == 1 && list.containsKey(Enchantments.BINDING_CURSE))
			return true;
		else
			return super.isBookEnchantable(stack, book);
	}

}
