package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ISpellstone;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemSpellstoneCurio;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayerSetlook;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPortalParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.Minecraft;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import net.minecraft.util.DamageSource;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class EyeOfNebula extends ItemSpellstoneCurio implements ISpellstone {
	public static Omniconfig.IntParameter spellstoneCooldown;
	public static Omniconfig.PerhapsParameter dodgeProbability;
	public static Omniconfig.DoubleParameter dodgeRange;
	public static Omniconfig.DoubleParameter phaseRange;
	public static Omniconfig.PerhapsParameter magicResistance;
	public static Omniconfig.PerhapsParameter magicBoost;
	public static Omniconfig.PerhapsParameter attackEmpower;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("EyeOfNebula");

		spellstoneCooldown = builder
				.comment("Active ability cooldown for Eye of the Nebula. Measured in ticks. 20 ticks equal to 1 second.")
				.getInt("Cooldown", 60);

		dodgeProbability = builder
				.comment("Probability for Eye of the Nebula to teleport it's bearer from any attack without receiving any damage. Defined as percentage.")
				.max(100)
				.getPerhaps("DodgeChance", 15);

		dodgeRange = builder
				.comment("Range in which Eye of the Nebula searches for a position to teleport it's bearer to when dodging the attack.")
				.min(1)
				.max(128)
				.getDouble("DodgeRange", 16);

		phaseRange = builder
				.comment("Range in which Eye of the Nebula can reach an entity when using it's active ability.")
				.min(1)
				.max(128)
				.getDouble("PhaseRange", 32);

		magicResistance = builder
				.comment("Magic Damage resistance provided by Eye of the Nebula. Defined as percentage.")
				.max(100)
				.getPerhaps("MagicResistance", 65);

		magicBoost = builder
				.comment("Magic Damage boost provided by Eye of the Nebula. Defined as percentage.")
				.getPerhaps("MagicBoost", 40);

		attackEmpower = builder
				.comment("Attack damage increase for next attack after using active ability. Defined as percentage.")
				.getPerhaps("AttackEmpower", 150);

		builder.popPrefix();
	}

	public EyeOfNebula() {
		super(ItemSpellstoneCurio.getDefaultProperties().rarity(EnumRarity.EPIC));

		Supplier<Float> magicResistanceSupplier = () -> magicResistance.getValue().asModifierInverted();
		this.resistanceList.put(DamageTypes.MAGIC, magicResistanceSupplier);
		this.resistanceList.put(DamageTypes.DRAGON_BREATH, magicResistanceSupplier);
		this.resistanceList.put(DamageTypes.WITHER, magicResistanceSupplier);
	}

	@Override
	public int getCooldown(EntityPlayer player) {
		if (player != null && reducedCooldowns.test(player))
			return 30;
		else
			return spellstoneCooldown.getValue();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebula1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebula2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebulaCooldown", EnumChatFormatting.GOLD, ((this.getCooldown(Minecraft.getInstance().player))) / 20.0F);
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebula3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebula4", EnumChatFormatting.GOLD, magicBoost.getValue().asPercentage() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebula5", EnumChatFormatting.GOLD, magicResistance.getValue().asPercentage() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebula6", EnumChatFormatting.GOLD, dodgeProbability.getValue().asPercentage() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebula7");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebula8", EnumChatFormatting.GOLD, attackEmpower.getValue().asPercentage() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebula9");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eyeOfNebula10");
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
	public void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
		if (SuperpositionHandler.hasSpellstoneCooldown(player))
			return;

		EntityLivingBase target = SuperpositionHandler.getObservedEntity(player, world, 3.0F, (int) phaseRange.getValue());

		if (target != null) {
			Vector3 targetPos = Vector3.fromEntityCenter(target);
			Vector3 chaserPos = Vector3.fromEntityCenter(player);

			Vector3 dir = targetPos.subtract(chaserPos);
			dir = dir.normalize();
			dir = dir.multiply(1.5D);

			dir = targetPos.add(dir);

			world.playSound(null, CompatPos.of(player), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));
			EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(), 128, player.worldObj.dimension())), new PacketPortalParticles(player.getX(), player.getY() + (player.getBbHeight() / 2), player.getZ(), 72, 1.0F, false));

			player.teleportTo(dir.x, target.getY() + 0.25D, dir.z);
			EnigmaticLegacy.packetInstance.send(PacketDistributor.PLAYER.with(() -> player), new PacketPlayerSetlook(target.getX(), target.getY() - 1.0D + (target.getBbHeight() / 2), target.getZ()));

			world.playSound(null, CompatPos.of(player), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));
			EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(), 128, player.worldObj.dimension())), new PacketRecallParticles(player.getX(), player.getY() + (player.getBbHeight() / 2), player.getZ(), 24, false));

			SuperpositionHandler.setSpellstoneCooldown(player, this.getCooldown(player));
			TransientPlayerData.get(player).setEyeOfNebulaPower(true);
		}
	}

}
