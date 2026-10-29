package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.entities.UltimateWitherSkullEntity;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemSpellstoneCurio;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.packets.clients.PacketForceArrowRotations;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayerMotion;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.Minecraft;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import net.minecraft.util.DamageSource;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class AngelBlessing extends ItemSpellstoneCurio  {
	public static Omniconfig.IntParameter spellstoneCooldown;
	public static Omniconfig.DoubleParameter accelerationModifier;
	public static Omniconfig.DoubleParameter accelerationModifierElytra;
	public static Omniconfig.PerhapsParameter deflectChance;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("AngelBlessing");

		spellstoneCooldown = builder
				.comment("Active ability cooldown for Angel's Blessing. Measured in ticks. 20 ticks equal to 1 second.")
				.getInt("Cooldown", 40);

		accelerationModifier = builder
				.comment("Acceleration modifier for active ability of Angel's Blessing. The greater it is, the more momentum you will gain.")
				.max(256)
				.getDouble("AccelerationModifier", 1.0);

		accelerationModifierElytra = builder
				.comment("Separate acceleration modifier for active ability of Angel's Blessing when player is flying with Elytra.")
				.max(256)
				.getDouble("AccelerationModifierElytra", 0.6);

		deflectChance = builder
				.comment("Chance to deflect projectile when having Angel's Blessing equipped. Measured in percents.")
				.max(100)
				.getPerhaps("DeflectChance", 50);

		builder.popPrefix();
	}

	protected double range = 4.0D;

	public AngelBlessing() {
		super(ItemSpellstoneCurio.getDefaultProperties().rarity(EnumRarity.RARE));

		this.immunityList.add(DamageTypes.FALL);
		this.immunityList.add(DamageTypes.FLY_INTO_WALL);

		this.resistanceList.put(DamageTypes.WITHER, () -> 2F);
		this.resistanceList.put(DamageTypes.FELL_OUT_OF_WORLD, () -> 2F);
	}

	@Override
	public int getCooldown(EntityPlayer player) {
		if (player != null && reducedCooldowns.test(player))
			return 15;
		else
			return spellstoneCooldown.getValue();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessingCooldown", EnumChatFormatting.GOLD, ((this.getCooldown(Minecraft.getInstance().player))) / 20.0F);
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing4");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing5", EnumChatFormatting.GOLD, deflectChance.getValue().asPercentage() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing6");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing7");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing8");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing9");
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

		Vector3 accelerationVec = new Vector3(player.getLookAngle());
		Vector3 motionVec = new Vector3(player.getDeltaMovement());

		if (player.isFallFlying()) {
			accelerationVec = accelerationVec.multiply(accelerationModifierElytra.getValue());
			accelerationVec = accelerationVec.multiply(1 / (Math.max(0.15D, motionVec.mag()) * 2.25D));
		} else {
			accelerationVec = accelerationVec.multiply(accelerationModifier.getValue());
		}

		Vector3 finalMotion = new Vector3(motionVec.x + accelerationVec.x, motionVec.y + accelerationVec.y, motionVec.z + accelerationVec.z);

		EnigmaticLegacy.packetInstance.send(PacketDistributor.PLAYER.with(() -> player), new PacketPlayerMotion(finalMotion.x, finalMotion.y, finalMotion.z));
		player.setDeltaMovement(finalMotion.x, finalMotion.y, finalMotion.z);

		world.playSound(null, CompatPos.of(player), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 1.0F, (float) (0.6F + (Math.random() * 0.1D)));

		SuperpositionHandler.setSpellstoneCooldown(player, this.getCooldown(player));
	}

	@Override
	public void curioTick(SlotContext context, ItemStack stack) {
		EntityLivingBase living = context.entity();

		if (living.worldObj.isClientSide)
			return;

		List<AbstractHurtingProjectile> projectileEntities = living.worldObj.getEntitiesOfClass(AbstractHurtingProjectile.class, new AxisAlignedBB(living.getX() - this.range, living.getY() - this.range, living.getZ() - this.range, living.getX() + this.range, living.getY() + this.range, living.getZ() + this.range));
		List<AbstractArrow> arrowEntities = living.worldObj.getEntitiesOfClass(AbstractArrow.class, new AxisAlignedBB(living.getX() - this.range, living.getY() - this.range, living.getZ() - this.range, living.getX() + this.range, living.getY() + this.range, living.getZ() + this.range));
		List<ThrowableItemProjectile> potionEntities = living.worldObj.getEntitiesOfClass(ThrowableItemProjectile.class, new AxisAlignedBB(living.getX() - this.range, living.getY() - this.range, living.getZ() - this.range, living.getX() + this.range, living.getY() + this.range, living.getZ() + this.range));

		for (AbstractHurtingProjectile entity : projectileEntities) {
			this.redirect(living, entity);
		}

		for (AbstractArrow entity : arrowEntities) {
			this.redirect(living, entity);
		}

		for (ThrowableItemProjectile entity : potionEntities) {
			this.redirect(living, entity);
		}
	}

	public void redirect(EntityLivingBase bearer, Entity redirected) {
		if (redirected instanceof UltimateWitherSkullEntity || redirected instanceof WitherSkull)
			return;

		Vector3 entityPos = Vector3.fromEntityCenter(redirected);
		Vector3 bearerPos = Vector3.fromEntityCenter(bearer);

		Vector3 redirection = entityPos.subtract(bearerPos);
		redirection = redirection.normalize();

		if ((redirected instanceof AbstractArrow arrow && arrow.getOwner() == bearer)
				|| (redirected instanceof ThrowableItemProjectile projectile && projectile.getOwner() == bearer)) {
			if (redirected.getTags().contains("AB_ACCELERATED")) {
				if (redirected.worldObj instanceof WorldServer level) {
					//ServerChunkCache cache = level.getChunkSource();
					//cache.broadcastAndSend(redirected, new ClientboundSetEntityMotionPacket(redirected));
					//cache.broadcastAndSend(redirected, new ClientboundTeleportEntityPacket(redirected));
					//EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(bearer.getX(), bearer.getY(), bearer.getZ(), 64.0D, bearer.level.dimension())), new PacketForceArrowRotations(redirected.getId(), redirected.getYRot(), redirected.getXRot(), redirected.getDeltaMovement().x, redirected.getDeltaMovement().y, redirected.getDeltaMovement().z, redirected.getX(), redirected.getY(), redirected.getZ()));
				}

				return;
			}

			if (redirected.getTags().stream().anyMatch(tag -> tag.startsWith("AB_DEFLECTED")))
				return;

			if (redirected instanceof ThrownTrident) {
				ThrownTrident trident = (ThrownTrident) redirected;

				if (trident.clientSideReturnTridentTickCount > 0)
					return;
			}

			if (redirected.addTag("AB_ACCELERATED")) {
				redirected.setDeltaMovement(redirected.getDeltaMovement().x * 1.75D, redirected.getDeltaMovement().y * 1.75D, redirected.getDeltaMovement().z * 1.75D);

				if (redirected.worldObj instanceof WorldServer level) {
					EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(bearer.getX(), bearer.getY(), bearer.getZ(), 64.0D, bearer.worldObj.dimension())), new PacketForceArrowRotations(redirected.getId(), redirected.getYRot(), redirected.getXRot(), redirected.getDeltaMovement().x, redirected.getDeltaMovement().y, redirected.getDeltaMovement().z, redirected.getX(), redirected.getY(), redirected.getZ()));
				}

				//EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(bearer.getX(), bearer.getY(), bearer.getZ(), 64.0D, bearer.level.dimension())), new PacketForceArrowRotations(redirected.getId(), redirected.getYRot(), redirected.getXRot(), redirected.getDeltaMovement().x, redirected.getDeltaMovement().y, redirected.getDeltaMovement().z, redirected.getX(), redirected.getY(), redirected.getZ()));
			}
		} else {
			// redirected.setDeltaMovement(redirection.x, redirection.y, redirection.z);
		}

		/*
		if (redirected instanceof AbstractHurtingProjectile) {
			AbstractHurtingProjectile redirectedProjectile = (AbstractHurtingProjectile) redirected;
			redirectedProjectile.xPower = (redirection.x / 4.0);
			redirectedProjectile.yPower = (redirection.y / 4.0);
			redirectedProjectile.zPower = (redirection.z / 4.0);
		}
		 */
	}
}
