package com.aizistral.enigmaticlegacy.items;

import java.awt.TextComponent;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vazkii.patchouli.api.PatchouliAPI;

public class TheAcknowledgment extends ItemBase implements Vanishable {
	private static final ItemStack UNSUSPECTING_DIAMOND_SWORD = new ItemStack(Items.DIAMOND_SWORD);
	private static final ResourceLocation BOOK_ID = new ResourceLocation(EnigmaticLegacy.MODID, "the_acknowledgment");
	private final Multimap<Attribute, AttributeModifier> attributes;
	private boolean allowAllEnchantments = false;

	protected TheAcknowledgment(Properties props, String name, double attackDamage, double attackSpeed) {
		super(props);

		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
		this.attributes = builder.build();
	}

	public TheAcknowledgment() {
		this(getDefaultProperties().rarity(EnumRarity.EPIC).stacksTo(1), BOOK_ID.getResourcePath(), 3.5, -2.1);
	}

	public static boolean isOpen() {
		return BOOK_ID.equals(PatchouliAPI.get().getOpenBookGui());
	}

	public static Component getEdition() {
		return PatchouliAPI.get().getSubtitle(BOOK_ID);
	}

	public static Component getTitle(ItemStack stack) {
		Component title = stack.getHoverName();

		String akashicTomeNBT = "akashictome:displayName";
		if (stack.hasTag() && stack.getTag().contains(akashicTomeNBT)) {
			title = Component.literal(stack.getTag().getString(akashicTomeNBT));
		}

		return title;
	}

	protected void setAllowAllEnchantments(boolean allowAllEnchantments) {
		this.allowAllEnchantments = allowAllEnchantments;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		target.setSecondsOnFire(4);

		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public int getEnchantmentValue(ItemStack stack) {
		return 24;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot.equals(EquipmentSlot.MAINHAND))
			return this.attributes;
		else
			return super.getAttributeModifiers(slot, stack);
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player instanceof EntityPlayerMP playerMP) {
			PatchouliAPI.get().openBookGUI(playerMP, BOOK_ID);
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		if (this.allowAllEnchantments)
			return !EnchantmentHelper.getEnchantments(book).keySet().contains(Enchantments.UNBREAKING) &&
					Items.DIAMOND_SWORD.isBookEnchantable(UNSUSPECTING_DIAMOND_SWORD, book);

		Map<Enchantment, Integer> list = EnchantmentHelper.getEnchantments(book);

		if (list.size() == 1 && list.containsKey(Enchantments.BANE_OF_ARTHROPODS))
			return true;
		else
			return super.isBookEnchantable(stack, book);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (this.allowAllEnchantments)
			return enchantment != Enchantments.UNBREAKING && Items.DIAMOND_SWORD.canApplyAtEnchantingTable(UNSUSPECTING_DIAMOND_SWORD, enchantment);

		return enchantment == Enchantments.BANE_OF_ARTHROPODS || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List<Component> list, TooltipFlag flag) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theAcknowledgment1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theAcknowledgment2");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		try {
			//list.add(Component.literal("").append(TheAcknowledgment.getEdition()).mergeStyle(ChatFormatting.DARK_PURPLE));
		} catch (Exception ex) {
			// Just don't do it lol
		}
	}

}
