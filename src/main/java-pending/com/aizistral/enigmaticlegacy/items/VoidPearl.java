package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ISpellstone;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemSpellstoneCurio;
import com.aizistral.enigmaticlegacy.registries.EnigmaticDamageTypes;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.settings.KeyBinding;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.ResourceKey;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import net.minecraft.util.DamageSource;
import com.aizistral.enigmaticlegacy.compat.DamageType;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import net.minecraft.potion.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class VoidPearl extends ItemSpellstoneCurio implements ISpellstone {
	public static Omniconfig.IntParameter spellstoneCooldown;
	public static Omniconfig.DoubleParameter baseDarknessDamage;
	public static Omniconfig.DoubleParameter regenerationDemodifier;
	public static Omniconfig.DoubleParameter shadowRange;
	public static Omniconfig.PerhapsParameter undeadProbability;
	public static Omniconfig.IntParameter witheringTime;
	public static Omniconfig.IntParameter witheringLevel;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("VoidPearl");

		spellstoneCooldown = builder
				.comment("Active ability cooldown for Pearl of the Void. Measured in ticks. 20 ticks equal to 1 second.")
				.getInt("Cooldown", 0);

		baseDarknessDamage = builder
				.comment("Base damage dealt by Darkness every half a second, when it devours a creature in proximity of bearer of the pearl.")
				.max(1000)
				.getDouble("BaseDarknessDamage", 4.0);

		regenerationDemodifier = builder
				.comment("Modifier for slowing down player's regeneration when bearing the pearl. This includes natural regeneration, as well as artificial healing effects that work over time. The greater it is, the slower player will regenerate.")
				.max(1000)
				.getDouble("RegenerationModifier", 1.0);

		shadowRange = builder
				.comment("Range in which Pearl of the Void will force darkness to devour living creatures.")
				.max(128)
				.getDouble("ShadowRange", 16.0);

		undeadProbability = builder
				.comment("Chance for Pearl of the Void to prevent it's bearer death from receiving lethal amout of damage. Defined as percentage.")
				.max(100)
				.getPerhaps("UndeadChance", 35);

		witheringTime = builder
				.comment("Amout of ticks for which bearer of the pearl will apply Withering effect to entities they attack. 20 ticks equals to 1 second.")
				.getInt("WitheringTime", 100);

		witheringLevel = builder
				.comment("Level of Withering that bearer of the pearl will apply to entitities they attack.")
				.max(3)
				.getInt("WitheringLevel", 2);

		builder.popPrefix();
	}

	public List<ResourceKey<DamageType>> healList = new ArrayList<>();

	public VoidPearl() {
		super(ItemSpellstoneCurio.getDefaultProperties().stacksTo(1).rarity(EnumRarity.EPIC).fireResistant());

		this.immunityList.add(DamageTypes.DROWN);
		this.immunityList.add(DamageTypes.IN_WALL);

		this.healList.add(DamageTypes.WITHER);
		this.healList.add(DamageTypes.MAGIC);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {

		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearlCooldown", EnumChatFormatting.GOLD, ((spellstoneCooldown.getValue())) / 20.0F);
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl3");
			// These lines describe hunger negation trait that was removed since release 2.5.0
			//ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl4");
			//ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl5");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl6");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl7");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl8");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl9");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl10");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl11");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl12");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl13", EnumChatFormatting.GOLD, undeadProbability.getValue().asPercentage() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.voidPearl14");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		try {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.currentKeybind", EnumChatFormatting.LIGHT_PURPLE, KeyBinding.createNameSupplier("key.spellstoneAbility").get().getString().toUpperCase());
		} catch (NullPointerException ex) {
			// Just don't do it lol
		}
	}

	@Override
	public void curioTick(SlotContext context, ItemStack stack) {
		if (context.entity() instanceof EntityPlayer player) {
			/*
			FoodData stats = player.getFoodStats();
			stats.setFoodLevel(20);

			//((AccessorFoodStats) stats).setFoodSaturationLevel(0);


			try {
				ObfuscatedFields.foodSaturationField.setFloat(stats, 0F);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			 */

			if (player.getAirSupply() < 300) {
				player.setAirSupply(300);
			}

			if (player.isOnFire()) {
				player.clearFire();
			}

			for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
				if (effect.getEffect() == MobEffects.NIGHT_VISION) {
					if (effect.getDuration() >= EnigmaticItems.MINING_CHARM.nightVisionDuration-10 && effect.getDuration() <= EnigmaticItems.MINING_CHARM.nightVisionDuration) {
						continue;
					}
				} else if (ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect()).equals(new ResourceLocation("mana-and-artifice", "chrono-exhaustion"))) {
					continue;
				}

				player.removePotionEffect(effect.getEffect());
			}

			if (player.ticksExisted % 10 == 0) {
				List<EntityLivingBase> entities = player.worldObj.getEntitiesOfClass(EntityLivingBase.class, new AxisAlignedBB(player.getX() - shadowRange.getValue(), player.getY() - shadowRange.getValue(), player.getZ() - shadowRange.getValue(), player.getX() + shadowRange.getValue(), player.getY() + shadowRange.getValue(), player.getZ() + shadowRange.getValue()));
				boolean hasAnimalGuide = SuperpositionHandler.hasItem(player, EnigmaticItems.ANIMAL_GUIDEBOOK);

				if (entities.contains(player)) {
					entities.remove(player);
				}

				for (EntityLivingBase victim : entities) {
					if (victim.worldObj.getMaxLocalRawBrightness(CompatPos.of(victim), 0) < 3 || (victim instanceof Phantom && !victim.isOnFire())) {
						if (hasAnimalGuide && EnigmaticItems.ANIMAL_GUIDEBOOK.isProtectedAnimal(victim)) {
							continue;
						}

						if (victim instanceof EntityPlayer) {
							EntityPlayer playerVictim = (EntityPlayer) victim;
							if (SuperpositionHandler.hasCurio(playerVictim, EnigmaticItems.VOID_PEARL)) {
								playerVictim.addPotionEffect(new PotionEffect(MobEffects.WITHER, 80, 1, false, true));
								continue;
							}
						}

						if (!(victim instanceof EntityPlayer) || player.canHarmPlayer((EntityPlayer) victim)) {
							boolean attack = victim.attackEntityFrom(victim.damageSources().source(EnigmaticDamageTypes.DARKNESS, player), (float) baseDarknessDamage.getValue());

							if (attack) {
								player.worldObj.playSound(null, CompatPos.of(victim), SoundEvents.PHANTOM_BITE, SoundSource.PLAYERS, 1.0F, (float) (0.3F + (Math.random() * 0.4D)));

								victim.addPotionEffect(new PotionEffect(MobEffects.WITHER, 80, 1, false, true));
								victim.addPotionEffect(new PotionEffect(MobEffects.MOVEMENT_SLOWDOWN, 100, 2, false, true));
								victim.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 0, false, true));
								victim.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 160, 2, false, true));
								victim.addPotionEffect(new PotionEffect(MobEffects.DIG_SLOWDOWN, 100, 3, false, true));
							}
						}
					}
				}
			}
		}

	}

}
