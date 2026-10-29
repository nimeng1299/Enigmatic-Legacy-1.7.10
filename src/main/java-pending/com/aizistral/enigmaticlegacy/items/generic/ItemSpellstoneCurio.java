package com.aizistral.enigmaticlegacy.items.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ISpellstone;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import com.aizistral.enigmaticlegacy.compat.ResourceKey;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.DamageType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;

public abstract class ItemSpellstoneCurio extends ItemBaseCurio implements ISpellstone {
	public static Omniconfig.BooleanParameter multiequip;
	public static Predicate<EntityPlayer> reducedCooldowns = SuperpositionHandler::hasArchitectsFavor;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("Spellstones");

		multiequip = builder.comment("Whether or not it should be allowed to equip multiple spellstones if they are different items," + " granted player somehow gets more than one spellstone slot.").getBoolean("Multiequip", false);

		builder.popPrefix();
	}

	public List<ResourceKey<DamageType>> immunityList = new ArrayList<>();
	public HashMap<ResourceKey<DamageType>, Supplier<Float>> resistanceList = new HashMap<>();

	public ItemSpellstoneCurio() {
		this(ItemSpellstoneCurio.getDefaultProperties());
	}

	public ItemSpellstoneCurio(Properties props) {
		super(props);
	}

	public boolean isResistantTo(String damageType) {
		return this.resistanceList.containsKey(damageType);
	}

	public boolean isImmuneTo(String damageType) {
		return this.immunityList.contains(damageType);
	}

	public Supplier<Float> getResistanceModifier(String damageType) {
		return this.resistanceList.get(damageType);
	}

	public static Properties getDefaultProperties() {
		return ItemBaseCurio.getDefaultProperties();
	}

	@Override
	public boolean canEquip(SlotContext context, ItemStack stack) {
		if (multiequip.getValue())
			return super.canEquip(context, stack);
		else
			return super.canEquip(context, stack) && SuperpositionHandler.getSpellstone(context.entity()) == null;
	}

	public int getCooldown(@Nullable EntityPlayer player) {
		throw new UnsupportedOperationException("This spellstone does not implement getCooldown method.");
	}

}
