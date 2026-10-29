package com.aizistral.enigmaticlegacy.items;

import java.util.*;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.api.items.ITaintable;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseTool;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.*;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import com.aizistral.enigmaticlegacy.compat.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.NBTBase;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import com.aizistral.enigmaticlegacy.compat.BlockTags;
import net.minecraft.world.Difficulty;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.Entity;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.item.*;
import com.aizistral.enigmaticlegacy.compat.Ingredient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import net.minecraft.world.World;
import net.minecraft.world.level.block.DispenserBlock;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

public class EldritchPan extends TieredItem implements Vanishable, ICursed, ICreativeTabMember {
	private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("2d5cac0e-598f-475b-97cb-c7ab4741d0f5");
	private static final ItemStack UNSUSPECTING_DIAMOND_SWORD = new ItemStack(Items.DIAMOND_SWORD);
	public static final Map<EntityPlayer, Integer> HOLDING_DURATIONS = new WeakHashMap<>();

	public static Omniconfig.DoubleParameter attackDamage = null;
	public static Omniconfig.DoubleParameter attackSpeed = null;
	public static Omniconfig.DoubleParameter armorValue = null;
	public static Omniconfig.DoubleParameter lifeSteal = null;
	public static Omniconfig.DoubleParameter hungerSteal = null;
	public static Omniconfig.DoubleParameter uniqueDamageGain = null;
	public static Omniconfig.DoubleParameter uniqueArmorGain = null;
    public static Omniconfig.IntParameter uniqueGainLimit = null;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("EldritchPan");

		attackDamage = builder
				.comment("The base attack damage of The Voracious Pan.")
				.max(32768).getDouble("AttackDamage", 31);

		attackSpeed = builder
				.comment("The base attack speed of The Voracious Pan.")
				.minMax(32768).getDouble("AttackSpeed", -3.2);

		armorValue = builder
				.comment("The base armor value of The Voracious Pan.")
				.max(32768).getDouble("ArmorValue", 4);

		lifeSteal = builder
				.comment("Base Lifesteal fraction of The Voracious Pan.")
				.max(32768).getDouble("LifeSteal", 0.15);

		hungerSteal = builder
				.comment("Base Hungersteal value of The Voracious Pan.")
				.max(32768).getDouble("HungerSteal", 2);

		uniqueDamageGain = builder
				.comment("Base damage gain from unique mob kills for The Voracious Pan.")
				.max(32768).getDouble("UniqueDamageGain", 0.5);

		uniqueArmorGain = builder
				.comment("Base armor gain from unique mob kills for The Voracious Pan.")
				.max(32768).getDouble("UniqueArmorGain", 0.5);

		uniqueGainLimit = builder
				.comment("How many unique mob kills will count towards increasing the stats of The Voracious Pan.")
				.max(32768).getInt("UniqueGainLimit", 100);

		builder.popPrefix();
	}

	private final Multimap<Attribute, AttributeModifier> defaultMainhandModifiers;
	private final Multimap<Attribute, AttributeModifier> defaultOffhandModifiers;
	//private final float attackDamage, attackSpeed, armorValue;

	public EldritchPan() {
		super(EnigmaticMaterials.ELDRITCH_PAN,
				ItemBaseTool.getDefaultProperties()
				.defaultDurability(4000)
				.rarity(EnumRarity.EPIC)
				.fireResistant());


		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage.getValue(), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed.getValue(), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIER_UUID, "Weapon modifier", armorValue.getValue(), AttributeModifier.Operation.ADDITION));
		this.defaultMainhandModifiers = builder.build();

		builder = ImmutableMultimap.builder();
		builder.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIER_UUID, "Weapon modifier", armorValue.getValue(), AttributeModifier.Operation.ADDITION));
		this.defaultOffhandModifiers = builder.build();

		DispenserBlock.registerBehavior(this, ItemArmor.DISPENSE_ITEM_BEHAVIOR);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND)
			return super.getDefaultAttributeModifiers(slot);

		return slot == EquipmentSlot.MAINHAND ? this.defaultMainhandModifiers : this.defaultOffhandModifiers;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND)
            return super.getDefaultAttributeModifiers(slot);

		int kills = getKillCount(stack);

		if (kills <= 0)
			return super.getAttributeModifiers(slot, stack);

		double armor = armorValue.getValue() + (uniqueArmorGain.getValue() * kills);
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

		builder.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIER_UUID, "Weapon modifier",
				armor, AttributeModifier.Operation.ADDITION));

		if (slot != EquipmentSlot.MAINHAND)
			return builder.build();

		double damage = attackDamage.getValue() + (uniqueDamageGain.getValue() * kills);

		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
				attackSpeed.getValue(), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
				damage, AttributeModifier.Operation.ADDITION));

		return builder.build();
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if (attacker.getRandom().nextDouble() < 0.0001) {
			attacker.worldObj.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
					EnigmaticSounds.PAN_CLANG_FR, SoundSource.PLAYERS, 1F,
					attacker.worldObj.random.nextFloat() * 0.2F + 0.8F);
		} else {
			attacker.worldObj.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
					EnigmaticSounds.PAN_CLANG, SoundSource.PLAYERS, 0.5F,
					attacker.worldObj.random.nextFloat() * 0.1F + 0.9F);
		}

		stack.hurtAndBreak(1, attacker, (entity) -> {
			entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
		});

		return true;
	}

	public boolean mineBlock(ItemStack stack, World level, BlockState state, BlockPos pos, EntityLivingBase entity) {
		if (state.getDestroySpeed(level, pos) != 0.0F) {
			stack.hurtAndBreak(2, entity, (living) -> {
				living.broadcastBreakEvent(EquipmentSlot.MAINHAND);
			});
		}

		return true;
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		// NO-OP
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			String life = "+" + this.toString(100 * lifeSteal.getValue()) + "%";
			String hunger = "+" + this.toString(hungerSteal.getValue());
			String damageGain = "+" + this.toString(uniqueDamageGain.getValue());
			String armorGain = "+" + this.toString(uniqueArmorGain.getValue());

			boolean noHunger = SuperpositionHandler.cannotHunger((EnigmaticLegacy.PROXY.getClientPlayer()));

			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan1");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan2");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan3",
					EnumChatFormatting.GOLD, life);

			if (!noHunger) {
				ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan4",
					EnumChatFormatting.GOLD, hunger);
			} else {
				ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan4_alt",
						EnumChatFormatting.GOLD, hunger);
			}

			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan5");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan6");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");

			if (!noHunger) {
				ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan7");
				ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan8");
			} else {
				ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan7_alt");
				ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan8_alt");
				ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan8p_alt");
			}
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan9",
					EnumChatFormatting.GOLD, damageGain);
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan10",
					EnumChatFormatting.GOLD, armorGain);
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.indicateWorthyOnesOnly(tooltip);
		} else {
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPanLore1");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.holdShift");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
			this.writeKillCount(tooltip, stack);
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.indicateCursedOnesOnly(tooltip);
		}
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (hand == InteractionHand.MAIN_HAND) {
			ItemStack offhandStack = player.getHeldItemOffhand();

			if (offhandStack.getItem().getUseAnimation(offhandStack) == UseAnim.BLOCK)
				return InteractionResultHolder.pass(stack);
		}

		if (SuperpositionHandler.isTheCursedOne(player)) {
			player.startUsingItem(hand);
			return InteractionResultHolder.consume(stack);
		} else
			return InteractionResultHolder.pass(stack);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		// TODO: make it consume projectiles for hunger
		return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
	}

	private void writeKillCount(List<Component> tooltip, ItemStack pan) {
		int kills = getKillCount(pan);

		ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPanKills1",
				EnumChatFormatting.GOLD, kills);

		if (kills >= uniqueGainLimit.getValue()) {
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPanKillsMax");
		}
	}

	@Override
	public @Nullable CreativeTabs getCreativeTab() {
		return EnigmaticTabs.MAIN;
	}

	private String toString(double value) {
		if (Math.floor(value) == value) {
			return Integer.toString((int) value);
		} else {
			return Double.toString(value);
		}
	}

	public static int getKillCount(ItemStack pan) {
		CompoundTag tag = pan.getTag();

		if (tag == null || !tag.contains("PanUniqueKills", Tag.TAG_LIST))
			return 0;

		ListTag list = tag.getList("PanUniqueKills", Tag.TAG_STRING);

		return list.size();
	}

	public static List<ResourceLocation> getUniqueKills(ItemStack pan) {
		CompoundTag tag = pan.getTag();

		if (tag == null || !tag.contains("PanUniqueKills", Tag.TAG_LIST))
			return Collections.emptyList();

		ListTag list = tag.getList("PanUniqueKills", Tag.TAG_STRING);

		return list.stream().map(e -> new ResourceLocation(((StringTag) e).getAsString())).toList();
	}

	public static void setUniqueKills(ItemStack pan, List<ResourceLocation> mobs) {
		CompoundTag tag = pan.getOrCreateTag();

		ListTag list = new ListTag();
		mobs.forEach(entity -> list.add(StringTag.valueOf(entity.toString())));
		tag.put("PanUniqueKills", list);

		pan.setTag(tag);
	}

	public static void addUniqueKill(ItemStack pan, ResourceLocation mob) {
		CompoundTag tag = pan.getOrCreateTag();
		ListTag list;

		if (!tag.contains("PanUniqueKills", Tag.TAG_LIST)) {
			list = new ListTag();
		} else {
			list = tag.getList("PanUniqueKills", Tag.TAG_STRING);
		}

		list.add(StringTag.valueOf(mob.toString()));
		
		tag.put("PanUniqueKills", list);
		pan.setTag(tag);
	}

	public static boolean addKillIfNotPresent(ItemStack pan, ResourceLocation mob) {
		List<ResourceLocation> kills = getUniqueKills(pan);

		if (kills.size() < 100 && !kills.contains(mob)) {
			addUniqueKill(pan, mob);
			return true;
		}

		return false;
	}

	public static Ingredient getRepairMaterial() {
		return Ingredient.of(
				Items.BEEF,
				Items.PORKCHOP,
				Items.MUTTON,
				Items.ROTTEN_FLESH,
				Items.APPLE,
				Items.GOLDEN_APPLE,
				Items.ENCHANTED_GOLDEN_APPLE,
				Items.POISONOUS_POTATO
		);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getEnchantmentValue(ItemStack stack) {
		return 24;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return Items.DIAMOND_SWORD.isBookEnchantable(UNSUSPECTING_DIAMOND_SWORD, book);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return Items.DIAMOND_SWORD.canApplyAtEnchantingTable(UNSUSPECTING_DIAMOND_SWORD, enchantment);
	}

}
