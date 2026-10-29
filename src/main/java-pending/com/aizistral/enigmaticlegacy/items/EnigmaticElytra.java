package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.IBindable;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.packets.server.PacketUpdateElytraBoosting;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.ParticleTypes;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.SoundEvent;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.Stats;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeInstance;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.world.item.ElytraItem;
import com.aizistral.enigmaticlegacy.compat.Equipable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import net.minecraft.world.World;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.util.Vec3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import top.theillusivec4.caelus.api.CaelusApi;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.curios.ICurio.SoundInfo;

public class EnigmaticElytra extends ItemBaseCurio implements IBindable {
	private static final AttributeModifier ELYTRA_MODIFIER = new AttributeModifier(UUID.fromString("44dfce5a-2f09-4f19-bc29-9b0324cd2a40"), EnigmaticLegacy.MODID+":elytra_modifier", 1.0, AttributeModifier.Operation.ADDITION);
	@SideOnly(Side.CLIENT)
	private static boolean isBoosting;

	public EnigmaticElytra() {
		super(ItemBaseCurio.getDefaultProperties().durability(5000).fireResistant().rarity(EnumRarity.EPIC));
		DispenserBlock.registerBehavior(this, ItemArmor.DISPENSE_ITEM_BEHAVIOR);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.CHEST;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List<Component> list, TooltipFlag flag) {
		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticElytra1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticElytra2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticElytra3");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	@Override
	public void curioTick(SlotContext context, ItemStack stack) {
		if (context.entity() instanceof EntityPlayer player && player.worldObj.isClientSide) {
			this.handleBoosting(player);
		}

		EntityLivingBase livingEntity = context.entity();
		int ticks = livingEntity.getFallFlyingTicks();

		if (ticks > 0 && livingEntity.isFallFlying()) {
			stack.elytraFlightTick(livingEntity, ticks);
		}
	}


	@SideOnly(Side.CLIENT)
	private void handleBoosting(EntityPlayer player) {
		if (Minecraft.getInstance().player != player)
			return;

		if (Minecraft.getInstance().options.keyJump.isDown() && this.boostPlayer(player)) {
			if (!isBoosting) {
				EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(),
						new PacketUpdateElytraBoosting(isBoosting = true));
			}
		} else {
			if (isBoosting) {
				EnigmaticLegacy.packetInstance.send(PacketDistributor.SERVER.noArg(),
						new PacketUpdateElytraBoosting(isBoosting = false));
			}
		}
	}

	private boolean boostPlayer(EntityPlayer player) {
		if (player.isFallFlying()) {
			Vec3 vec31 = player.getLookAngle();
			Vec3 vec32 = player.getDeltaMovement();
			player.setDeltaMovement(vec32.add(vec31.x * 0.1D + (vec31.x * 1.5D - vec32.x) * 0.5D, vec31.y * 0.1D + (vec31.y * 1.5D - vec32.y) * 0.5D, vec31.z * 0.1D + (vec31.z * 1.5D - vec32.z) * 0.5D));
			return true;
		} else
			return false;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
		return attributes;
	}

	@Override
	public boolean isValidRepairItem(ItemStack repairedItem, ItemStack material) {
		return material.is(EnigmaticItems.ETHERIUM_INGOT);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World level, EntityPlayer player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		EquipmentSlot equipmentslot = EntityLiving.getEquipmentSlotForItem(itemstack);
		ItemStack itemstack1 = player.getItemBySlot(equipmentslot);
		if (itemstack1.isEmpty()) {
			player.setItemSlot(equipmentslot, itemstack.copy());
			if (!level.isClientSide()) {
				player.awardStat(Stats.ITEM_USED.get(this));
			}

			itemstack.setCount(0);
			return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
		} else
			return InteractionResultHolder.fail(itemstack);
	}

	@Override
	public SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
		return new SoundInfo(SoundEvents.ARMOR_EQUIP_ELYTRA, 1F, 1F);
	}

	@Override
	public boolean canElytraFly(ItemStack stack, EntityLivingBase entity) {
		return entity instanceof EntityPlayer && ElytraItem.isFlyEnabled(stack);
	}

	@Override
	public boolean elytraFlightTick(ItemStack stack, EntityLivingBase entity, int flightTicks) {
		if (entity instanceof EntityPlayer player) {
			if (!entity.worldObj.isClientSide) {
				int nextFlightTick = flightTicks + 1;

				if (nextFlightTick % 10 == 0) {
					if (nextFlightTick % 20 == 0) {
						stack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(EquipmentSlot.CHEST));
					}
					entity.gameEvent(GameEvent.ELYTRA_GLIDE);
				}
			} else {
				this.handleBoosting(player);
			}

			return true;
		} else
			return false;
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == Phase.START) {
			ItemStack stack = null;
			AttributeInstance attribute = event.player.getAttribute(CaelusApi.getInstance().getFlightAttribute());
			attribute.removeModifier(ELYTRA_MODIFIER);

			if (!attribute.hasModifier(ELYTRA_MODIFIER)) {
				stack = SuperpositionHandler.getEnigmaticElytra(event.player);
				if (stack != null && stack.is(this) && ElytraItem.isFlyEnabled(stack)) {
					attribute.addTransientModifier(ELYTRA_MODIFIER);
				}
			}

			if (event.player instanceof EntityPlayerMP player)
				if (TransientPlayerData.get(player).isElytraBoosting()) {
					this.boostPlayer(player);

					if (stack != null && stack.is(this)) {
						int flightTicks = player.getFallFlyingTicks();
						int nextFlightTick = flightTicks + 1;
						if (nextFlightTick % 5 == 0) {
							stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(EquipmentSlot.CHEST));
						}
					}
				}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerTickClient(TickEvent.PlayerTickEvent event) {
		if (event.phase == Phase.START && event.player.worldObj.isClientSide()) {
			EntityPlayer player = event.player;

			if (TransientPlayerData.get(player).isElytraBoosting()) {
				if (!player.isFallFlying()) {
					if (event.player == Minecraft.getInstance().player) {
						this.handleBoosting(player);
						return;
					} else {
						TransientPlayerData.get(player).setElytraBoosting(false);
						return;
					}
				}

				int amount = 3;
				double rangeModifier = 0.1;

				for (int counter = 0; counter <= amount; counter++) {
					Vector3 vec = Vector3.fromEntityCenter(player);
					vec = vec.add(Math.random() - 0.5, -1.0 + Math.random() - 0.5, Math.random() - 0.5);
					player.worldObj.addParticle(ParticleTypes.DRAGON_BREATH, true, vec.x, vec.y, vec.z, ((Math.random()-0.5D)*2.0D)*rangeModifier, ((Math.random()-0.5D)*2.0D)*rangeModifier, ((Math.random()-0.5D)*2.0D)*rangeModifier);
				}
			}
		}
	}

}
