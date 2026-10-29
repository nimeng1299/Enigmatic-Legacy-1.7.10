package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;

public class MonsterCharm extends ItemBaseCurio {
	public static Omniconfig.PerhapsParameter undeadDamageBonus;
	public static Omniconfig.PerhapsParameter hostileDamageBonus;
	public static Omniconfig.BooleanParameter bonusLootingEnabled;
	public static Omniconfig.BooleanParameter doubleXPEnabled;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("MonsterCharm");

		undeadDamageBonus = builder
				.comment("Damage bonus against undead enemies for Emblem of Monster Slayer. Defined as percentage.")
				.max(1000)
				.getPerhaps("UndeadDamage", 25);

		hostileDamageBonus = builder
				.comment("Damage bonus against agressive creatures for Emblem of Monster Slayer. Defined as percentage.")
				.max(1000)
				.getPerhaps("HostileDamage", 10);

		bonusLootingEnabled = builder
				.comment("Whether or not Emblem of Monster Slayer should provide +1 Looting Level.")
				.getBoolean("BonusLooting", true);

		doubleXPEnabled = builder
				.comment("Whether or not Emblem of Monster Slayer should provide double experience drop from monsters.")
				.getBoolean("DoubleXP", true);

		builder.popPrefix();
	}

	public float bonusXPModifier = 2.0F;

	public MonsterCharm() {
		super(ItemBaseCurio.getDefaultProperties().rarity(EnumRarity.EPIC));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.monsterCharm1", EnumChatFormatting.GOLD, undeadDamageBonus.getValue().asPercentage() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.monsterCharm2", EnumChatFormatting.GOLD, hostileDamageBonus.getValue().asPercentage() + "%");
			if (bonusLootingEnabled.getValue()) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.monsterCharm3");
			}
			if (doubleXPEnabled.getValue()) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.monsterCharm4");
			}
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	@Override
	public int getLootingLevel(SlotContext slotContext, DamageSource source, EntityLivingBase target, int baseLooting, ItemStack curio) {
		return super.getLootingLevel(slotContext, source, target, baseLooting, curio) + 1;
	}

}
